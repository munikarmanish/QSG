import org.junit.*;
import static org.junit.Assert.*;


public class InterviewTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void get_all_interviews() {
        User u = new User("admin").save();
        Interview i = new Interview(u, "Interview 1").save();
        assertEquals(1, Interview.all().size());
        assertTrue(Interview.all().contains(i));
    }

    @Test
    public void delete_interview_from_db() {
        User u = new User("admin").save();
        Interview c = new Interview(u, "test").save();
        assertEquals(1, Interview.all().size());
        c.delete();
        assertEquals(0, Interview.all().size());
    }

    @Test
    public void find_interview_by_id() {
        User u = new User("admin").save();
        Interview c = new Interview(u, "test").save();
        int id = c.getId();
        assertEquals(c, Interview.findById(id));
    }

}
