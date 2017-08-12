import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import org.sql2o.*;


public class User extends Timestamped {

    // static

    public static final Integer ROLE_EXAMINER = 0;
    public static final Integer ROLE_OPERATOR = 1;
    public static final Integer ROLE_ADMIN = 2;
    public static final Integer DEFAULT_ROLE = ROLE_EXAMINER;

    // variables

    private String email;
    private String username;
    private String passwordHash;
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
        if (!passwordHash.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$")) {
            throw new IllegalArgumentException("Invalid password hash, regex fail");
        }
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

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof User)) return false;
        User user = (User) obj;
        return this.email.equals(user.getEmail()) &&
            this.username.equals(user.getUsername()) &&
            this.passwordHash.equals(user.getPasswordHash()) &&
            this.name.equals(user.getName()) &&
            this.id.equals(user.getId());
    }

    // methods

    public boolean checkPassword(String password) {
        String newHash = Utils.bytesToBase64(Utils.sha256(password));
        return newHash.equals(this.passwordHash);
    }

    public User save() {
        try (Connection con = DB.sql2o.open()) {
            String sql;
            if (this.id != null && this.id > 0) {
                sql = "UPDATE users SET email=:email, passwordHash=:passwordHash, "
                      + "username=:username, name=:name, role=:role "
                      + "WHERE id=:id";
                con.createQuery(sql).bind(this).executeUpdate();
            } else {
                sql = "INSERT INTO users (email, username, passwordHash, name, role)"
                        + "VALUES (:email, :username, :passwordHash, :name, :role)";
                this.id = con.createQuery(sql, true)
                            .bind(this)
                            .executeUpdate()
                            .getKey(Integer.class);
            }
            return User.findById(this.id);
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM users WHERE id=:id";
            con.createQuery(sql)
                .addParameter("id", this.id)
                .executeUpdate();
            this.id = 0;
        }
    }

    // relations lookup

    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM questions WHERE userId=:id";
            return con.createQuery(sql).bind(this).executeAndFetch(Question.class);
        }
    }

    public List<Exam> getExams() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM exams WHERE userId=:id";
            return con.createQuery(sql).bind(this).executeAndFetch(Exam.class);
        }
    }

    // static methods

    public static List<User> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM users";
            return con.createQuery(sql).executeAndFetch(User.class);
        }
    }

    public static User findById(Integer id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM users WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(User.class);
        }
    }

    public static User findByUsername(String username) {
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery("SELECT * FROM users WHERE username=:username")
                .addParameter("username", username)
                .executeAndFetchFirst(User.class);
        }
    }
}
