import java.security.MessageDigest;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;


/**
 * A class that contains utility functions.
 *
 * @author Manish Munikar
 * @since 2017-08-12
 */
public class Utils {

    /**
     * Gets the SHA-256 hash of a text as a Byte array.
     *
     * @param text A string to hash.
     *
     * @return Byte array of hash.
     */
    public static byte[] sha256(String text) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(text.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            return null; // TODO
        } catch (UnsupportedEncodingException e) {
            return null; // TODO
        }
    }

    /** Returns the base-64 string representation of a byte array. */
    public static String bytesToBase64(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    /** Valid hex characters */
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /** Returns the hex string representation of a byte array. */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Returns the base-64 string representation of the SHA-256 hash of the
     * given string.
     */
    public static String sha256Base64(String text) {
        return bytesToBase64(sha256(text));
    }
}
