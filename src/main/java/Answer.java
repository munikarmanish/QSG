import java.util.List;
import org.sql2o.*;


/**
 * This class represents an answer in the MCQ database.
 *
 * An answer is associated with a particular question and can be an incorrect
 * answer as well.
 *
 * @author Manish Munikar
 * @since 2017-08-12
 */
public class Answer extends Timestamped {

    // attributes

    private Integer questionId;     // ID of associated Question
    private String text;            // Content of answer
    private boolean isCorrect;      // Is this a correct answer?

    // constructors

    /**
     * Creates an Answer instance with Question ID.
     */
    public Answer(Integer questionId, String text, boolean isCorrect) {
        this.setQuestionId(questionId);
        this.setText(text);
        this.setIsCorrect(isCorrect);
    }

    /**
     * Creates an Answer instance with Question object.
     */
    public Answer(Question question, String text, boolean isCorrect) {
        this.setQuestionId(question.getId());
        this.setText(text);
        this.setIsCorrect(isCorrect);
    }

    // getters & setters

    public Integer getQuestionId() {
        return this.questionId;
    }

    public Answer setQuestionId(Integer id) {
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

    /**
     * Checks if this Answer object is the same as an another object.
     *
     * @param obj Any java object to compare with.
     *
     * @return True, if equal. Otherwise, False.
     */
    @Override
    public boolean equals(Object obj) {
        // If the given object is not an instance of Answer, simply return
        // false
        if (! (obj instanceof Answer)) {
            return false;
        }
        Answer answer = (Answer) obj;
        return this.questionId.equals(answer.getQuestionId()) &&
            this.id.equals(answer.getId()) &&
            this.text.equals(answer.getText()) &&
            this.isCorrect == answer.getIsCorrect();
    }

    // database methods

    /**
     * Inserts the instance in the database.
     *
     * @return Saved instance
     */
    public Answer save() {
        try (Connection con = DB.sql2o.open();) {
            String query = "INSERT INTO answers (questionId, text, isCorrect)"
                + " VALUES (:questionId, :text, :isCorrect)";
            this.id = con.createQuery(query, true)
                        .bind(this)
                        .executeUpdate()
                        .getKey(Integer.class);
            return Answer.findById(this.id);
        }
    }

    /**
     * Deletes the instance from the database.
     */
    public void delete() {
        try (Connection con = DB.sql2o.open();) {
            String query = "DELETE FROM answers WHERE id=:id";
            con.createQuery(query).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    // relations lookup

    /**
     * Gets the Question instance associated with this Answer object.
     *
     * @return Question instance associated with this Answer object.
     */
    public Question getQuestion() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM questions WHERE id=:questionId";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetchFirst(Question.class);
        }
    }

    // static methods

    /**
     * Gets the list of all answers of all questions in the database.
     *
     * @return List of all saved Answer objects.
     */
    public static List<Answer> all() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM answers";
            return con.createQuery(query).executeAndFetch(Answer.class);
        }
    }

    /**
     * Finds an answer in the database with the given ID.
     *
     * @param id Answer ID to look up.
     *
     * @return Answer instance with given ID. Null, if it doesn't exist.
     */
    public static Answer findById(Integer id) {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM answers WHERE id=:id";
            return con.createQuery(query)
                .addParameter("id", id)
                .executeAndFetchFirst(Answer.class);
        }
    }
}
