import java.sql.Timestamp;


/**
 * This is a base class for database model which implements methods to get
 * and set ID and created/updated timestamps.
 *
 * Timestamped instances are not directly saved in the database, but they are
 * inherited by other derived classes which can be saved directly to the
 * database.
 *
 * @author Manish Munikar
 * @since 2017-08-12
 */
public class Timestamped {

    // variables

    protected Integer id;
    protected Timestamp createdAt;
    protected Timestamp updatedAt;

    // getters and setters

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp timestamp) {
        this.createdAt = timestamp;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Timestamp timestamp) {
        this.updatedAt = timestamp;
    }
}
