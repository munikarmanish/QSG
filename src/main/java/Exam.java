import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import java.util.Date;
import org.sql2o.*;


public class Exam extends Timestamped {

    // variables

    private String title;
    private Integer userId;
    private Timestamp time;
    private Integer duration;
    private Integer difficulty;

    // static variables

    public static final int DEFAULT_DURATION = 30;

    // constructors

    public Exam() {
        // empty constructor
    }

    public Exam(User user, String title) {
        this.setUserId(user.getId());
        this.setTitle(title);
        this.setTime(new Timestamp(new Date().getTime()));
        this.setDuration(Exam.DEFAULT_DURATION);
        this.setDifficulty(Question.DEFAULT_DIFFICULTY);
    }

    // getters

    public String getTitle() {
        return this.title;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public Integer getDifficulty() {
        return this.difficulty;
    }

    public String getDifficultyString() {
        if (this.difficulty == Question.DIFFICULTY_EASY) {
            return "Easy";
        } else if (this.difficulty == Question.DIFFICULTY_MEDIUM) {
            return "Medium";
        } else if (this.difficulty == Question.DIFFICULTY_HARD) {
            return "Hard";
        } else {
            throw new IllegalArgumentException("Invalid difficulty number");
        }
    }

    // setters

    public Exam setTitle(String title) {
        this.title = title;
        return this;
    }

    public Exam setUserId(Integer id) {
        this.userId = id;
        return this;
    }

    public Exam setTime(Timestamp time) {
        this.time = time;
        return this;
    }

    public Exam setTime(String timeString) {
        this.time = Timestamp.valueOf(timeString);
        return this;
    }

    public Exam setDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public Exam setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Exam)) {
            return false;
        }
        Exam i = (Exam) obj;
        return this.title.equals(i.getTitle()) &&
            this.id == i.getId() &&
            this.time.equals(i.getTime()) &&
            this.duration == i.getDuration() &&
            this.difficulty == i.getDifficulty();
    }

    // methods

    public Exam save() {
        String sql;
        try (Connection con = DB.sql2o.open()) {
            if (this.id > 0) {
                sql = "UPDATE exams SET userId=:userId, title=:title, time=:time, duration=:duration, difficulty=:difficulty WHERE id=:id";
                con.createQuery(sql).bind(this).executeUpdate();
            } else {
                sql = "INSERT INTO exams (userId, title, time, duration, difficulty) VALUES (:userId, :title, :time, :duration, :difficulty)";
                this.id = con.createQuery(sql, true)
                    .bind(this)
                    .executeUpdate()
                    .getKey(Integer.class);
            }
            return Exam.findById(this.id);
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM exams WHERE id=:id";
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
            return con.createQuery("SELECT * FROM sets WHERE examId=:id ORDER BY `set` ASC")
                .bind(this)
                .executeAndFetch(Set.class);
        }
    }

    // static methods

    public static List<Exam> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM exams ORDER BY id DESC";
            return con.createQuery(sql).executeAndFetch(Exam.class);
        }
    }

    public static Exam findById(int id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM exams WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Exam.class);
        }
    }
}
