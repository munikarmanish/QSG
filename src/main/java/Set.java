import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import org.sql2o.*;


public class Set extends Timestamped {

    // variables

    private int userId;
    private int totalMarks;
    private Timestamp examTime;
    private int examDuration;       // in minutes

    // constructors
    
    public Set(int userId, int totalMarks) {
        this.setUserId(userId);
        this.setTotalMarks(totalMarks);
        this.setExamTime(new Timestamp(new Date().getTime()));
        this.setExamDuration(60); // 60 mins
    }

    public Set(User user, int totalMarks) {
        this.setUserId(user.getId());
        this.setTotalMarks(totalMarks);
        this.setExamTime(new Timestamp(new Date().getTime()));
        this.setExamDuration(60); // 60 mins
    }

    public Set(int userId, int totalMarks, Timestamp examTime, int examDuration) {
        this.setUserId(userId);
        this.setTotalMarks(totalMarks);
        this.setExamTime(examTime);
        this.setExamDuration(examDuration);
    }

    public Set(User user, int totalMarks, Timestamp examTime, int examDuration) {
        this.setUserId(user.getId());
        this.setTotalMarks(totalMarks);
        this.setExamTime(examTime);
        this.setExamDuration(examDuration);
    }

    // getters
    
    public int getUserId() {
        return this.userId;
    }

    public int getTotalMarks() {
        return this.totalMarks;
    }

    public Timestamp getExamTime() {
        return this.examTime;
    }

    public int getExamDuration() {
        return this.examDuration;
    }

    // setters

    public Set setUserId(int id) {
        this.userId = id;
        return this;
    }

    public Set setTotalMarks(int marks) {
        this.totalMarks = marks;
        return this;
    }

    public Set setExamTime(Timestamp timestamp) {
        this.examTime = timestamp;
        return this;
    }

    public Set setExamTime(String timestampString) {
        this.examTime = Timestamp.valueOf(timestampString);
        return this;
    }

    public Set setExamDuration(int minutes) {
        this.examDuration = minutes;
        return this;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Set)) return false;
        Set s = (Set) obj;
        return this.id == s.getId() &&
            this.userId == s.getUserId() &&
            this.totalMarks == s.getTotalMarks() &&
            this.createdAt.equals(s.getCreatedAt());
    }

    // database methods
    
    public Set save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sets (userId, totalMarks, examTime, examDuration)"
                + "VALUES (:userId, :totalMarks, :examTime, :examDuration)";
            this.id = con.createQuery(sql).bind(this).executeUpdate().getKey(int.class);
            Set s = Set.findById(this.id);
            this.setCreatedAt(s.getCreatedAt());
            this.setUpdatedAt(s.getUpdatedAt());
            return this;
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM sets WHERE id=:id";
            con.createQuery(sql).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    // static methods
    
    public static List<Set> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM sets";
            return con.createQuery(sql).executeAndFetch(Set.class);
        }
    }

    public static Set findById(int id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM sets WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Set.class);
        }
    }
}
