import org.junit.*;
import static org.junit.Assert.*;


public class SetTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


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

}
