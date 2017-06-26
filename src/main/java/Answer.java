import java.util.List;
import org.sql2o.*;


public class Answer extends Timestamped {

    // variables

    private int questionId;
    private String text;
    private boolean isCorrect;

    // constructors

    public Answer(int questionId, String text, boolean isCorrect) {
        this.setQuestionId(questionId);
        this.setText(text);
        this.setIsCorrect(isCorrect);
    }

    public Answer(Question question, String text, boolean isCorrect) {
        this.setQuestionId(question.getId());
        this.setText(text);
        this.setIsCorrect(isCorrect);
    }

    // getters & setters

    public int getQuestionId() {
        return this.questionId;
    }

    public Answer setQuestionId(int id) {
        this.questionId = id;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public Answer setText(String text) {
        this.text = text;
        return this;
    }

    public boolean getIsCorrect() {
        return this.isCorrect;
    }

    public Answer setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
        return this;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Answer)) {
            return false;
        }
        Answer a = (Answer) obj;
        return this.questionId == a.getQuestionId() &&
            this.id == a.getId() &&
            this.text.equals(a.getText()) &&
            this.isCorrect == a.getIsCorrect();
    }

    // database methods

    public Answer save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO answers (questionId, text, isCorrect)"
                + "VALUES (:questionId, :text, :isCorrect)";
            this.id = con.createQuery(sql).bind(this).executeUpdate().getKey(int.class);
            Answer a = Answer.findById(this.id);
            this.setCreatedAt(a.getCreatedAt());
            this.setUpdatedAt(a.getUpdatedAt());
            return this;
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM answers WHERE id=:id";
            con.createQuery(sql).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    // relations lookup

    public Question getQuestion() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM questions WHERE id=:questionId";
            return con.createQuery(sql).bind(this).executeAndFetchFirst(Question.class);
        }
    }

    // static methods

    public static List<Answer> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM answers";
            return con.createQuery(sql).executeAndFetch(Answer.class);
        }
    }

    public static Answer findById(int id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM answers WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Answer.class);
        }
    }
}
