import java.sql.Timestamp;


public interface Timestamped {

  // getters and setters

  public int getId();
  public void setId(int id);

  public Timestamp getCreatedAt();
  public void setCreatedAt(Timestamp timestamp);

  public Timestamp getUpdatedAt();
  public void setUpdatedAt(Timestamp timestamp);

}
