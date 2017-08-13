import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the Answer class.
 */
public class AnswerTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void get_all_answers() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "What?", 0).save();
        Answer a = new Answer(q, "That", true).save();
        assertEquals(1, Answer.all().size());
        assertTrue(Answer.all().contains(a));
    }

    @Test
    public void delete_answer_from_db() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "What?", 0).save();
        Answer a = new Answer(q, "That", true).save();
        assertEquals(1, Answer.all().size());
        a.delete();
        assertEquals(0, Answer.all().size());
    }

    @Test
    public void find_answer_by_id() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "What?", 0).save();
        Answer a = new Answer(q, "That", true).save();
        Integer id = a.getId();
        assertEquals(a, Answer.findById(id));
    }

    @Test
    public void get_question() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "What?", 0).save();
        Answer a = new Answer(q, "That", true).save();
        assertEquals(q, a.getQuestion());
    }

}
