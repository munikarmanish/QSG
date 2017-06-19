import org.junit.*;
import static org.junit.Assert.*;


public class CategoryTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void get_all_users() {
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

}
