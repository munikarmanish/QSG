import org.junit.*;
import static org.junit.Assert.*;


/**
 * Unit tests for the User class.
 */
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
        Integer id = u.getId();
        assertEquals(u, User.findById(id));
    }

    @Test
    public void find_user_by_username() {
        User u = new User("test").save();
        assertEquals(u, User.findByUsername("test"));
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
    public void get_all_exams() {
        User u = new User("test").save();
        Exam s1 = new Exam(u, "test").save();
        Exam s2 = new Exam(u, "test").save();

        assertEquals(2, u.getExams().size());
        assertTrue(u.getExams().contains(s1));
        assertTrue(u.getExams().contains(s2));
    }

    @Test
    public void update_user_using_save() {
        User u = new User("test").save();
        Integer id = u.getId();
        u.setName("Name");
        u.save();
        assertEquals("Name", User.findById(id).getName());
    }
}
