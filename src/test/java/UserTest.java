import org.junit.*;
import static org.junit.Assert.*;


public class UserTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();


  @Test
  public void check_password() {
    User u = new User();
    String password = "Some random password";
    u.setPasswordHash(Utils.bytesToBase64(Utils.sha256(password)));
    assert(u.checkPassword(password));
  }

}
