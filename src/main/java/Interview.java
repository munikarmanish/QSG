import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import java.util.Date;
import org.sql2o.*;


public class Interview extends Timestamped {

    // variables

    private String title;
    private int userId;
    private Timestamp time;
    private int duration;

    // static variables

    public static final int DEFAULT_DURATION = 30;

    // constructors

    public Interview(int userId, String title, Timestamp ts, int duration) {
        if (userId == 0) {
            this.setUserId(0);
        } else {
            this.setUserId(userId);
        }
        this.setTitle(title);
        this.setTime(ts);
        this.setDuration(duration);
    }

    // getters

    public String getTitle() {
        return this.title;
    }

    public int getUserId() {
        return this.userId;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public int getDuration() {
        return this.duration;
    }

    // setters

    public Interview setTitle(String title) {
        this.title = title;
        return this;
    }

    public Interview setUserId(int id) {
        this.userId = id;
        return this;
    }

    public Interview setTime(Timestamp time) {
        this.time = time;
        return this;
    }

    public Interview setTime(String timeString) {
        this.time = Timestamp.valueOf(timeString);
        return this;
    }

    public Interview setDuration(int duration) {
        this.duration = duration;
        return this;
    }
    public static Integer getinterviewId() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT MAX(id) FROM interviews";
            return con.createQuery(sql)
            .executeScalar(Integer.class);     
        }
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Interview)) {
            return false;
        }
        Interview i = (Interview) obj;
        return this.title.equals(i.getTitle()) &&
            this.id == i.getId() &&
            this.time.equals(i.getTime()) &&
            this.duration == i.getDuration();
    }

    // methods

    public Interview save() {
        try (Connection con = DB.sql2o.open()) {
             Integer userId = null;
            if (this.userId > 0) {
                userId = this.userId;
            }
            String sqlpre="SET FOREIGN_KEY_CHECKS = 0";
            con.createQuery(sqlpre).executeUpdate();
            String sql = "INSERT INTO interviews (userId, title, time, duration) VALUES (:userId, :title, :time, :duration)";
            
                this.id = con.createQuery(sql, true)
                .addParameter("userId", userId)
                .addParameter("title", this.title)
                .addParameter("time", this.time)
                .addParameter("duration", this.duration)
                .executeUpdate()
                .getKey(int.class);
            return Interview.findById(this.id);
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM interviews WHERE id=:id";
            con.createQuery(sql).bind(this).executeUpdate();
            this.setId(0);
        }
    }
    

    // relations lookup

    public User getUser() {
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery("SELECT * FROM users WHERE id=:userId")
                .bind(this)
                .executeAndFetchFirst(User.class);
        }
    }

    public List<Set> getSets() {
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery("SELECT * FROM sets WHERE interviewId=:id")
                .bind(this)
                .executeAndFetch(Set.class);
        }
    }

    // static methods

    public static List<Interview> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM interviews ORDER BY id DESC";
            return con.createQuery(sql).executeAndFetch(Interview.class);
        }
    }
    public static List<Interview> dated_all(String startdate, String enddate) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM interviews WHERE time BETWEEN :startdate AND :enddate";//2019-07-09 01:54:00 //yyyy-mm-dd
            return con.createQuery(sql)
            .addParameter("startdate",startdate)
            .addParameter("enddate",enddate)
            .executeAndFetch(Interview.class);
        }
    }
    public static Interview findById(int id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM interviews WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Interview.class);
        }
    }
    }
