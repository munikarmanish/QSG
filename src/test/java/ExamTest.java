import org.junit.*;
import static org.junit.Assert.*;


/**
 * Unit tests for the Exam class.
 */
public class ExamTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void get_all_Exams() {
        User u = new User("admin").save();
        Exam i = new Exam(u, "Exam 1").save();
        assertEquals(1, Exam.all().size());
        assertTrue(Exam.all().contains(i));
    }

    @Test
    public void delete_Exam_from_db() {
        User u = new User("admin").save();
        Exam c = new Exam(u, "test").save();
        assertEquals(1, Exam.all().size());
        c.delete();
        assertEquals(0, Exam.all().size());
    }

    @Test
    public void find_Exam_by_id() {
        User u = new User("admin").save();
        Exam c = new Exam(u, "test").save();
        Integer id = c.getId();
        assertEquals(c, Exam.findById(id));
    }

    // relations lookup

    @Test
    public void get_user() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        assertEquals(u, i.getUser());
    }

    @Test
    public void get_sets() {
        User u = new User("test").save();
        Exam i = new Exam(u, "test").save();
        Set[] sets = {
            new Set(i, 1).save(),
            new Set(i, 2).save(),
            new Set(i, 3).save(),
        };
        assertEquals(3, i.getSets().size());
        for (Set s : sets) {
            assertTrue(i.getSets().contains(s));
        }
    }

}
