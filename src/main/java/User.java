import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import org.sql2o.*;


public class User implements Timestamped {

    // variables

    private int id;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private String email;
    private String username;
    private String passwordHash;
    private String name;

    // constructors

    // getters and setters

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp timestamp) {
        this.createdAt = timestamp;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Timestamp timestamp) {
        this.updatedAt = timestamp;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        if (validator.isValid(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email");
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof User)) return false;
        User user = (User) obj;
        if (!this.email.equals(user.getEmail())) return false;
        if (!this.username.equals(user.getUsername())) return false;
        if (!this.passwordHash.equals(user.getPasswordHash())) return false;
        if (!this.name.equals(user.getName())) return false;
        if (this.id != user.getId()) return false;
        return true;
    }

    // methods

    public boolean checkPassword(String password) {
        String newHash = Utils.bytesToBase64(Utils.sha256(password));
        return newHash.equals(this.passwordHash);
    }

    public User save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO users (email, username, passwordHash, name)"
                + "VALUES (:email, :username, :passwordHash, :name)";
            this.id = con.createQuery(sql, true)
                .bind(this)
                .executeUpdate()
                .getKey(int.class);
            return this;
        }
    }

    // static methods

    public static List<User> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM users";
            return con.createQuery(sql).executeAndFetch(User.class);
        }
    }
}
