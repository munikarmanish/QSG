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

    @Test
    public void get_all_questions() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q1 = new Question(u, c, "Math", 0).save();
        Question q2 = new Question(u, c, "Science", 0).save();

        assertEquals(2, u.getQuestions().size());
        assertTrue(u.getQuestions().contains(q1));
        assertTrue(u.getQuestions().contains(q2));
    }

    @Test
    public void get_all_interviews() {
        User u = new User("test").save();
        Interview s1 = new Interview(u, "test").save();
        Interview s2 = new Interview(u, "test").save();
        
        assertEquals(2, u.getInterviews().size());
        assertTrue(u.getInterviews().contains(s1));
        assertTrue(u.getInterviews().contains(s2));
    }

}
