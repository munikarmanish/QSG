import java.sql.Timestamp;


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
    this.email = email;
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

  // methods

  public boolean checkPassword(String password) {
    String newHash = Utils.bytesToBase64(Utils.sha256(password));
    return newHash.equals(this.passwordHash);
  }
}
