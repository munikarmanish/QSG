import org.junit.*;
import static org.junit.Assert.*;


public class SetTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


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

}
