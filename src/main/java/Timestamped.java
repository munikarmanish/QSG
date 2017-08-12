import java.sql.Timestamp;


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
