import org.junit.*;
import static org.junit.Assert.*;


/**
 * Unit tests for the Question class.
 */
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
        Question q = new Question(0, c.getId(), "Math", 0).save();
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
        Integer id = q.getId();
        assertEquals(q, Question.findById(id));
    }

    @Test
    public void get_user() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        assertEquals(u, q.getUser());
    }

    @Test
    public void get_category() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        assertEquals(c, q.getCategory());
    }

    @Test
    public void getAnswers() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        Answer a1 = new Answer(q, "Answer 1", true).save();
        Answer a2 = new Answer(q, "Answer 2", false).save();
        Answer a3 = new Answer(q, "Answer 3", false).save();
        Answer a4 = new Answer(q, "Answer 4", false).save();
        assertEquals(4, q.getAnswers().size());
        assertTrue(q.getAnswers().contains(a1));
        assertTrue(q.getAnswers().contains(a2));
        assertTrue(q.getAnswers().contains(a3));
        assertTrue(q.getAnswers().contains(a4));
    }

    @Test
    public void getSets() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Category c = new Category("test").save();
        Set s1 = new Set(i, 1).save();
        Set s2 = new Set(i, 2).save();
        Question q = new Question(u, c, "Math", 0).save();
        s1.addQuestion(q, 1, 0);
        s2.addQuestion(q, 1, 0);
        assertEquals(2, q.getSets().size());
        assertTrue(q.getSets().contains(s1));
        assertTrue(q.getSets().contains(s2));
    }


    @Test
    public void add_an_answer_to_question() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "Math", 0).save();
        q.addAnswer("Answer", false);
        assertEquals("Answer", q.getAnswers().get(0).getText());
        assertEquals(false, q.getAnswers().get(0).getIsCorrect());
    }

    @Test
    public void limit() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q1 = new Question(u, c, "Math", 0).save();
        Question q2 = new Question(u, c, "Math", 0).save();
        Question q3 = new Question(u, c, "Math", 0).save();
        Question q4 = new Question(u, c, "Math", 0).save();
        assertEquals(2, Question.limit(0, 2).size());
        assertTrue(Question.limit(0, 2).contains(q4));
        assertTrue(Question.limit(0, 2).contains(q3));
        assertEquals(1, Question.limit(3, 2).size());
        assertTrue(Question.limit(3, 2).contains(q1));
    }

    @Test
    public void get_correct_answer() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "test", 0).save();
        q.addAnswer("a1", false);
        q.addAnswer("a1", false);
        q.addAnswer("a3", true);
        q.addAnswer("a1", false);

        assertEquals("a3", q.getCorrectAnswer().getText());
    }

    @Test
    public void get_incorrect_answers() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q = new Question(u, c, "test", 0).save();
        q.addAnswer("a1", false);
        q.addAnswer("a1", false);
        q.addAnswer("a3", true);
        q.addAnswer("a1", false);

        assertEquals(3, q.getIncorrectAnswers().size());
    }

}
