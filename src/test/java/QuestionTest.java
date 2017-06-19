import org.junit.*;
import static org.junit.Assert.*;


public class QuestionTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void instantiate_with_user_and_category() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        assertTrue(q instanceof Question);
    }

    @Test
    public void instantiate_with_user_and_category_id() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u.getId(), c.getId(), "Math", 0).save();
        assertTrue(q instanceof Question);
    }

    @Test
    public void get_all_questions() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        assertEquals(1, Question.all().size());
        assertTrue(Question.all().contains(q));
    }

    @Test
    public void delete_question_from_db() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        assertEquals(1, Question.all().size());
        q.delete();
        assertEquals(0, Question.all().size());
    }

    @Test
    public void find_question_by_id() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        int id = q.getId();
        assertEquals(q, Question.findById(id));
    }

}
