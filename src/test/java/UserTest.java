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

    @Test
    public void get_all_users() {
        User u = new User();
        u.setEmail("test@example.com");
        u.setUsername("test");
        u.setPasswordHash(Utils.sha256Base64("test"));
        u.setName("Test");
        u.save();
        assertEquals(1, User.all().size());
        assertTrue(User.all().contains(u));
    }

}
