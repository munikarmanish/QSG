import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import org.sql2o.*;


public class Set extends Timestamped {

    // variables

    private Integer examId;
    private Integer set;

    // constructors

    public Set() {
        // empty constructor
    }

    public Set(Integer examId, Integer set) {
        this.setExamId(examId);
        this.setSet(set);
    }

    public Set(Exam exam, Integer set) {
        this.setExamId(exam.getId());
        this.setSet(set);
    }

    // getters

    public Integer getExamId() {
        return this.examId;
    }

    public Integer getSet() {
        return this.set;
    }

    // setters

    public Set setExamId(Integer id) {
        this.examId = id;
        return this;
    }

    public Set setSet(Integer set) {
        this.set = set;
        return this;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Set)) return false;
        Set s = (Set) obj;
        return this.id == s.getId() &&
            this.examId == s.getExamId() &&
            this.set == s.getSet();
    }

    // database methods

    public Set save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sets (examId, `set`)"
                + " VALUES (:examId, :set)";
            this.id = con.createQuery(sql).bind(this).executeUpdate().getKey(Integer.class);
            return Set.findById(this.id);
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM sets WHERE id=:id";
            con.createQuery(sql).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    public Set addQuestion(Question q, Integer questionNumber, Integer correctIndex) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sets_questions (setId, questionId, questionNumber, correctIndex)"
                + "VALUES (:setId, :questionId, :questionNumber, :correctIndex)";
            con.createQuery(sql)
                .addParameter("setId", this.id)
                .addParameter("questionId", q.getId())
                .addParameter("questionNumber", questionNumber)
                .addParameter("correctIndex", correctIndex)
                .executeUpdate();
            return this;
        }
    }

    // relations lookup

    public Exam getExam() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT * FROM exams WHERE id=:examId";
            return con.createQuery(q).bind(this).executeAndFetchFirst(Exam.class);
        }
    }

    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT questions.id, userId, categoryId, text, difficulty,"
                + " questions.createdAt, questions.updatedAt FROM questions"
                + " INNER JOIN sets_questions ON questions.id = sets_questions.questionId"
                + " WHERE sets_questions.setId = :id"
                + " ORDER BY sets_questions.questionNumber ASC";
            return con.createQuery(q).bind(this).executeAndFetch(Question.class);
        }
    }

    public List<Integer> getCorrectIndices() {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT correctIndex FROM sets_questions WHERE setId=:id ORDER BY questionNumber ASC";
            return con.createQuery(sql)
                    .bind(this)
                    .executeAndFetch(Integer.class);
        }
    }

    // static methods

    public static List<Set> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM sets";
            return con.createQuery(sql).executeAndFetch(Set.class);
        }
    }

    public static Set findById(Integer id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM sets WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Set.class);
        }
    }
}
