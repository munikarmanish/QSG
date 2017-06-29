import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import org.sql2o.*;


public class Set extends Timestamped {

    // variables

    // constructors

    // getters

    // setters

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Set)) return false;
        Set s = (Set) obj;
        return this.id == s.getId();
    }

    // database methods

    public Set save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sets (userId, totalMarks, examTime, examDuration)"
                + "VALUES (:userId, :totalMarks, :examTime, :examDuration)";
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

    public Set addQuestion(Question q) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO sets_questions (setId, questionId)"
                + "VALUES (:setId, :questionId)";
            con.createQuery(sql)
                .addParameter("setId", this.id)
                .addParameter("questionId", q.getId())
                .executeUpdate();
            return this;
        }
    }

    // relations lookup

    public User getUser() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT * FROM users WHERE id=:userId";
            return con.createQuery(q).bind(this).executeAndFetchFirst(User.class);
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
