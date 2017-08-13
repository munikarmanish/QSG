import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;
import org.sql2o.*;


/**
 * Unit tests for the Set class.
 */
public class SetTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Test
    public void get_all_sets() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Set s = new Set(i, 1).save();
        assertEquals(1, Set.all().size());
        assertTrue(Set.all().contains(s));
    }

    @Test
    public void delete_set_from_db() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Set s = new Set(i, 1).save();
        assertEquals(1, Set.all().size());
        s.delete();
        assertEquals(0, Set.all().size());
    }

    @Test
    public void find_set_by_id() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Set s = new Set(i, 1).save();
        Integer id = s.getId();
        assertEquals(s, Set.findById(id));
    }

    // relations

    @Test
    public void get_exam() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Set s = new Set(i, 1).save();
        assertEquals(i, s.getExam());
    }

    @Test
    public void getQuestions() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Set s = new Set(i, 1).save();
        Category c = new Category("test").save();
        Question q1 = new Question(u, c, "Question 1", 0).save();
        Question q2 = new Question(u, c, "Question 2", 0).save();
        s.addQuestion(q1, 2, 0);
        s.addQuestion(q2, 1, 1);
        assertEquals(2, s.getQuestions().size());
        assertEquals(q2, s.getQuestions().get(0));
        assertEquals(q1, s.getQuestions().get(1));
        // make sure can't add same question twice
        exception.expect(Sql2oException.class);
        s.addQuestion(q1, 1, 0);
        s.addQuestion(q1, 2, 0);
    }

}
