import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import org.sql2o.*;


public class Set extends Timestamped {

    // variables

    private int interviewId;
    private int set;

    // constructors

    public Set() {
        // empty constructor
    }

    public Set(int interviewId, int set) {
        this.setInterviewId(interviewId);
        this.setSet(set);
    }

    public Set(Interview interview, int set) {
        this.setInterviewId(interview.getId());
        this.setSet(set);
    }

    // getters

    public int getInterviewId() {
        return this.interviewId;
    }

    public int getSet() {
        return this.set;
    }

    // setters

    public Set setInterviewId(int id) {
        this.interviewId = id;
        return this;
    }

    public Set setSet(int set) {
        this.set = set;
        return this;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Set)) return false;
        Set s = (Set) obj;
        return this.id == s.getId() &&
            this.interviewId == s.getInterviewId() &&
            this.set == s.getSet();
    }

    // database methods

    public Set save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sets (interviewId, `set`)"
                + " VALUES (:interviewId, :set)";
            this.id = con.createQuery(sql).bind(this).executeUpdate().getKey(int.class);
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

    public Set addQuestion(Question q, int questionNumber, int correctIndex) {
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

    public Interview getInterview() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT * FROM interviews WHERE id=:interviewId";
            return con.createQuery(q).bind(this).executeAndFetchFirst(Interview.class);
        }
    }

    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT questions.id, userId, categoryId, text, difficulty, questions.createdAt, questions.updatedAt FROM questions"
                + " INNER JOIN sets_questions ON questions.id = sets_questions.questionId"
                + " WHERE sets_questions.setId = :id";
            return con.createQuery(q).bind(this).executeAndFetch(Question.class);
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
