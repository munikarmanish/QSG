import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import java.util.Date;
import org.sql2o.*;


/**
 * Represents an MCQ exam. Each exam has a title, timestamp, examiner, duration
 * and difficulty.
 *
 * @author Manish Munikar
 * @since 2017-08-12
 *
 * @see Question
 */
public class Exam extends Timestamped {

    // variables

    private String title;           // Title of exam
    private Integer userId;         // ID of examiner who generated this exam
    private Timestamp time;         // Timestamp of exam (date and time)
    private Integer duration;       // Duration of exam (in minutes)
    private Integer difficulty;     // Difficulty (see Question.java)

    // static variables

    public static final Integer DEFAULT_DURATION = 60;

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

    /**
     * Gets the string representation of difficulty level.
     *
     * @return Human-friendly string representation of difficulty level.
     */
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

    /** Checks if this Exam instance is equal to some other object.
     *
     * @param obj Any java object.
     *
     * @return True, if equal. False, otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // If obj is not an instance of Exam class, directly return false.
        if (! (obj instanceof Exam)) {
            return false;
        }
        Exam exam = (Exam) obj;
        return this.title.equals(exam.getTitle()) &&
            this.id.equals(exam.getId()) &&
            this.time.equals(exam.getTime()) &&
            this.duration.equals(exam.getDuration()) &&
            this.difficulty.equals(exam.getDifficulty());
    }

    // methods

    /**
     * Saves the instance in the database. Updates if already saved.
     *
     * @return Saved or updated instance.
     */
    public Exam save() {
        String query;
        try (Connection con = DB.sql2o.open();) {
            if (this.id != null && this.id > 0) {
                query = "UPDATE exams SET userId=:userId, title=:title,"
                    + " time=:time, duration=:duration, difficulty=:difficulty"
                    + " WHERE id=:id";
                con.createQuery(query).bind(this).executeUpdate();
            } else {
                query = "INSERT INTO exams"
                    + " (userId, title, time, duration, difficulty) VALUES"
                    + " (:userId, :title, :time, :duration, :difficulty)";
                this.id = con.createQuery(query, true)
                            .bind(this)
                            .executeUpdate()
                            .getKey(Integer.class);
            }
            return Exam.findById(this.id);
        }
    }

    /**
     * Deletes the Exam instance from the database.
     */
    public void delete() {
        try (Connection con = DB.sql2o.open();) {
            String sql = "DELETE FROM exams WHERE id=:id";
            con.createQuery(sql).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    // relations lookup

    /**
     * Gets the user (examiner) who created this Exam instance.
     *
     * @return User associated with this Exam instance.
     */
    public User getUser() {
        try (Connection con = DB.sql2o.open();) {
            return con.createQuery("SELECT * FROM users WHERE id=:userId")
                    .bind(this)
                    .executeAndFetchFirst(User.class);
        }
    }

    /**
     * Gets the 3 Question Sets for this Exam.
     *
     * @return List of 3 Set instances associated with this Exam instance.
     */
    public List<Set> getSets() {
        try (Connection con = DB.sql2o.open();) {
            return con.createQuery("SELECT * FROM sets WHERE examId=:id"
                                   + " ORDER BY setNumber ASC")
                .bind(this)
                .executeAndFetch(Set.class);
        }
    }

    // static methods

    /**
     * Gets all the Exam instances in the database.
     *
     * @return List of all Exam instances in the database.
     */
    public static List<Exam> all() {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT * FROM exams ORDER BY id DESC";
            return con.createQuery(sql).executeAndFetch(Exam.class);
        }
    }

    /**
     * Finds Exam instance by ID.
     *
     * @param id Exam ID to look for.
     *
     * @return Exam instance with given ID. Null, if not found.
     */
    public static Exam findById(Integer id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM exams WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Exam.class);
        }
    }
}
