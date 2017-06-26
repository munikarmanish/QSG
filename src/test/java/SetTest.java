import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;
import org.sql2o.*;


public class SetTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void set_timestamp_from_string() {
        User u = new User("test").save();
        Set s = new Set(u, 100).save();
        s.setExamTime("2017-07-01 10:00:00");
        assertEquals(2017-1900, s.getExamTime().getYear());
        assertEquals(7-1, s.getExamTime().getMonth());
        assertEquals(1, s.getExamTime().getDate());
        assertEquals(10, s.getExamTime().getHours());
        assertEquals(0, s.getExamTime().getMinutes());
    }

    @Test
    public void get_all_sets() {
        User u = new User("test").save();
        Set s = new Set(u, 100).save();
        assertEquals(1, Set.all().size());
        assertTrue(Set.all().contains(s));
    }

    @Test
    public void delete_set_from_db() {
        User u = new User("test").save();
        Set s = new Set(u, 100).save();
        assertEquals(1, Set.all().size());
        s.delete();
        assertEquals(0, Set.all().size());
    }

    @Test
    public void find_set_by_id() {
        User u = new User("test").save();
        Set s = new Set(u, 100).save();
        int id = s.getId();
        assertEquals(s, Set.findById(id));
    }

    // relations

    @Test
    public void get_user() {
        User u = new User("test").save();
        Set s = new Set(u, 100).save();
        assertEquals(u, s.getUser());
    }

    @Test
    public void getQuestions() {
        User u = new User("test").save();
        Set s = new Set(u, 100).save();
        Category c = new Category("test").save();
        Question q1 = new Question(u, c, "Question 1", 0).save();
        Question q2 = new Question(u, c, "Question 2", 0).save();
        s.addQuestion(q1);
        s.addQuestion(q2);
        assertEquals(2, s.getQuestions().size());
        assertTrue(s.getQuestions().contains(q1));
        assertEquals(q1.getId(), s.getQuestions().get(0).getId());
        assertTrue(s.getQuestions().contains(q2));
        // make sure can't add same question twice
        exception.expect(Sql2oException.class);
        s.addQuestion(q1);
    }

}
