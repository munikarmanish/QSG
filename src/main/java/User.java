import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import org.sql2o.*;


public class User extends Timestamped {

    // variables
    
    private String email;
    private String username;
    private String passwordHash;
    private String name;

    // constructors

    public User(String email, String username, String password, String name) {
        this.setEmail(email);
        this.setUsername(username);
        this.setPassword(password);
        this.setName(name);
    }

    // getters and setters

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
        if (username.matches("[A-Za-z0-9_.-]+")) {
            this.username = username;
        } else {
            throw new IllegalArgumentException("Invalid username");
        }
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (!passwordHash.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$")) {
            throw new IllegalArgumentException("Invalid password hash, regex fail");
        }
        if (passwordHash.length() != 44) {
            throw new IllegalArgumentException("Invalid password hash, not 44 length");
        }
        this.passwordHash = passwordHash;
    }

    public void setPassword(String password) {
        this.setPasswordHash(Utils.sha256Base64(password));
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

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM users WHERE id=:id";
            con.createQuery(sql)
                .addParameter("id", this.id)
                .executeUpdate();
            this.id = 0;
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
