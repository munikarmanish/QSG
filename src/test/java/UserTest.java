import org.junit.*;
import static org.junit.Assert.*;


public class UserTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void check_password() {
        String password = "Some random password";
        User u = new User("test@example.com", "test", password, "test");
        assert(u.checkPassword(password));
    }

    @Test
    public void get_all_users() {
        User u = new User("test@example.com", "test", "test", "test");
        u.save();
        assertEquals(1, User.all().size());
        assertTrue(User.all().contains(u));
    }

    @Test
    public void delete_user_from_db() {
        User u = new User("test@example.com", "test", "test", "test");
        u.save();
        assertEquals(1, User.all().size());
        u.delete();
        assertEquals(0, User.all().size());
    }

    @Test
    public void find_user_by_id() {
        User u = new User("test").save();
        int id = u.getId();
        assertEquals(u, User.findById(id));
    }

}
