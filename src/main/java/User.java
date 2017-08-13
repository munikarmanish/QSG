import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import org.sql2o.*;


/**
 * Represents a user in the system.
 *
 * Users can be of one of 3 roles:
 *
 *      0: Examiner (can generate question sets)
 *      1: Operator (can also create new questions)
 *      2: Admin (can also add new users)
 *
 * @author Manish Munikar
 * @since 2017-08-13
 */
public class User extends Timestamped {

    // static

    public static final Integer ROLE_EXAMINER = 0;
    public static final Integer ROLE_OPERATOR = 1;
    public static final Integer ROLE_ADMIN = 2;
    public static final Integer DEFAULT_ROLE = ROLE_EXAMINER;

    // variables

    private String email;
    private String username;
    private String passwordHash;    // SHA-256 digest of password
    private String name;
    private Integer role;

    // constructors

    public User() {
        // NOTHING
    }

    public User(String email, String username, String password, String name) {
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.setName(name);
        this.setRole(DEFAULT_ROLE);
    }

    public User(String email, String username, String password, String name, Integer role) {
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.setName(name);
        this.setRole(role);
    }

    public User(String name, Integer role) {
        this.setEmail(name + "@example.com");
        this.setUsername(name);
        this.setPassword(name);
        this.setName(name);
        this.setRole(role);
    }

    public User(String name) {
        this.setEmail(name + "@example.com");
        this.setUsername(name);
        this.setPassword(name);
        this.setName(name);
        this.setRole(DEFAULT_ROLE);
    }

    // getters and setters

    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email. Validates email before setting.
     *
     * @param email The email address to set.
     *
     * @return Updated User instance.
     */
    public User setEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        if (validator.isValid(email)) {
            this.email = email;
            return this;
        } else {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public String getUsername() {
        return this.username;
    }

    public User setUsername(String username) {
        if (username.matches("[A-Za-z0-9_.-]+")) {
            this.username = username;
            return this;
        } else {
            throw new IllegalArgumentException("Invalid username");
        }
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public User setPasswordHash(String passwordHash) {
        // Validate hash characters.
        if (!passwordHash.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$")) {
            throw new IllegalArgumentException("Invalid password hash, regex fail");
        }
        // Validate hash length.
        if (passwordHash.length() != 44) {
            throw new IllegalArgumentException("Invalid password hash, not 44 length");
        }
        this.passwordHash = passwordHash;
        return this;
    }

    public User setPassword(String password) {
        this.setPasswordHash(Utils.sha256Base64(password));
        return this;
    }

    public String getName() {
        return this.name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isAdmin() {
        return this.role == ROLE_ADMIN;
    }

    public boolean isOperator() {
        return (this.role == ROLE_OPERATOR || this.role == ROLE_ADMIN);
    }

    public boolean isExaminer() {
        return (this.role == ROLE_EXAMINER || this.role == ROLE_ADMIN);
    }

    public Integer getRole() {
        return this.role;
    }

    public User setRole(Integer role) {
        this.role = role;
        return this;
    }

    // operators

    /**
     * Equality comparator.
     *
     * @param obj Any java object.
     *
     * @return True, if equal. False, if not.
     */
    @Override
    public boolean equals(Object obj) {
        // If obj is not a User instance, directly return false.
        if (! (obj instanceof User)) return false;
        User user = (User) obj;
        return this.email.equals(user.getEmail()) &&
            this.username.equals(user.getUsername()) &&
            this.passwordHash.equals(user.getPasswordHash()) &&
            this.name.equals(user.getName()) &&
            this.id.equals(user.getId());
    }

    // methods

    /**
     * Checks if the given password is correct the user.
     *
     * @param password Password to check.
     *
     * @return True, if pass. False, if fail.
     */
    public boolean checkPassword(String password) {
        String newHash = Utils.bytesToBase64(Utils.sha256(password));
        return newHash.equals(this.passwordHash);
    }

    /**
     * Saves the User instance in the database. Updates if already saved.
     *
     * @return Saved (or updated) instance.
     */
    public User save() {
        try (Connection con = DB.sql2o.open();) {
            String query;
            if (this.id != null && this.id > 0) {
                query = "UPDATE users SET email=:email, passwordHash=:passwordHash, "
                      + "username=:username, name=:name, role=:role "
                      + "WHERE id=:id";
                con.createQuery(query).bind(this).executeUpdate();
            } else {
                query = "INSERT INTO users (email, username, passwordHash, name, role)"
                        + "VALUES (:email, :username, :passwordHash, :name, :role)";
                this.id = con.createQuery(query, true)
                            .bind(this)
                            .executeUpdate()
                            .getKey(Integer.class);
            }
            return User.findById(this.id);
        }
    }

    /**
     * Deletes this User instance from the database.
     */
    public void delete() {
        try (Connection con = DB.sql2o.open();) {
            String query = "DELETE FROM users WHERE id=:id";
            con.createQuery(query)
                .bind(this)
                .executeUpdate();
            this.id = 0;
        }
    }

    // relations lookup

    /**
     * Gets the questions added by this user.
     *
     * @return List of Question instances added by this User.
     */
    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM questions WHERE userId=:id";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetch(Question.class);
        }
    }

    /**
     * Gets the exams created by this user.
     *
     * @return List of Exam instances created by this User.
     */
    public List<Exam> getExams() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM exams WHERE userId=:id";
            return con.createQuery(query).bind(this).executeAndFetch(Exam.class);
        }
    }

    // static methods

    /**
     * Gets all the users in the database.
     *
     * @return List of all the User instances in the database.
     */
    public static List<User> all() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM users";
            return con.createQuery(query).executeAndFetch(User.class);
        }
    }

    /**
     * Finds user by ID.
     *
     * @param id User ID to look for.
     *
     * @return User instance with given ID. Null, if not found.
     */
    public static User findById(Integer id) {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM users WHERE id=:id";
            return con.createQuery(query)
                .addParameter("id", id)
                .executeAndFetchFirst(User.class);
        }
    }

    /**
     * Finds user with given username. This works because username has a UNIQUE
     * constraint in the database.
     *
     * @param username Username to look for.
     *
     * @return User instance with given username. Null, if not found.
     */
    public static User findByUsername(String username) {
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery("SELECT * FROM users WHERE username=:username")
                .addParameter("username", username)
                .executeAndFetchFirst(User.class);
        }
    }
}
