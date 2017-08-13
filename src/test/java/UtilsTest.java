import org.junit.*;
import static org.junit.Assert.*;


/**
 * Unit tests for the Utils class.
 */
public class UtilsTest {

    @Rule
    public DatabaseRule database = new DatabaseRule();


    @Test
    public void test_sha256_length_base64() {
        assertEquals(44, Utils.bytesToBase64(Utils.sha256("Hello world.")).length());
    }

    @Test
    public void test_sha256_length_hex() {
        assertEquals(64, Utils.bytesToHex(Utils.sha256("Hello world.")).length());
    }
}
