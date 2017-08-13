import org.junit.*;
import static org.junit.Assert.*;


/**
 * Unit tests for the Category class.
 */
public class CategoryTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void get_all_categories() {
        Category c = new Category("Math");
        c.save();
        assertEquals(1, Category.all().size());
        assertTrue(Category.all().contains(c));
    }

    @Test
    public void delete_category_from_db() {
        Category c = new Category("test");
        c.save();
        assertEquals(1, Category.all().size());
        c.delete();
        assertEquals(0, Category.all().size());
    }

    @Test
    public void find_category_by_id() {
        Category c = new Category("test").save();
        Integer id = c.getId();
        assertEquals(c, Category.findById(id));
    }

    @Test
    public void find_category_by_name() {
        Category c = new Category("Test").save();
        assertEquals(c, Category.findByName("test"));
    }

    @Test
    public void get_all_questions() {
        User u = new User("test").save();
        Category c = new Category("test").save();
        Question q1 = new Question(u, c, "Math", 0).save();
        Question q2 = new Question(u, c, "Science", 0).save();

        assertEquals(2, c.getQuestions().size());
        assertTrue(c.getQuestions().contains(q1));
        assertTrue(c.getQuestions().contains(q2));
    }

}
