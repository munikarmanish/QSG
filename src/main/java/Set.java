import java.util.Date;
import java.util.List;
import java.sql.Timestamp;
import org.sql2o.*;


/**
 * Represents a question set that contains a certain number of questions in a
 * specified order where each question has answers in a specified order.
 *
 * There are 3 Set instances associated with one Exam instance.
 *
 * @author Manish Munikar
 * @since 2017-08-13
 */
public class Set extends Timestamped {

    // variables

    private Integer examId;     // ID of associated Exam

    private Integer setNumber;  // Set number to distinguish sets of same exam.
                                // For example, one Exam instance is associate
                                // with 3 Set instances whose `setNumber` could
                                // is 1,2,3 respectively.

    // constructors

    public Set() {
        // empty constructor
    }

    public Set(Integer examId, Integer setNumber) {
        this.setExamId(examId);
        this.setSetNumber(setNumber);
    }

    public Set(Exam exam, Integer setNumber) {
        this.setExamId(exam.getId());
        this.setSetNumber(setNumber);
    }

    // getters

    public Integer getExamId() {
        return this.examId;
    }

    public Integer getSetNumber() {
        return this.setNumber;
    }

    // setters

    public Set setExamId(Integer id) {
        this.examId = id;
        return this;
    }

    public Set setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
        return this;
    }

    // operators

    /**
     * Equality comparator.
     *
     * @param obj Any java object.
     *
     * @return True, if equal. False, otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // If obj is not an Set instance, directly return false.
        if (! (obj instanceof Set)) return false;
        Set set = (Set) obj;
        return this.id.equals(set.getId()) &&
            this.examId.equals(set.getExamId()) &&
            this.setNumber.equals(set.getSetNumber());
    }

    // database methods

    /**
     * Inserts the Set instance in the database.
     *
     * @return Saved instance.
     */
    public Set save() {
        try (Connection con = DB.sql2o.open();) {
            String query = "INSERT INTO sets (examId, setNumber)"
                + " VALUES (:examId, :setNumber)";
            this.id = con.createQuery(query)
                        .bind(this)
                        .executeUpdate()
                        .getKey(Integer.class);
            return Set.findById(this.id);
        }
    }

    /**
     * Deletes the Set instance from the database. Also deletes the Set-Question
     * relations.
     */
    public void delete() {
        try (Connection con = DB.sql2o.open();) {
            String query = "DELETE FROM sets WHERE id=:id";
            con.createQuery(query).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    /**
     * Adds the given question to the set at the given question number and with
     * the given order of answers.
     *
     * @param question Question to add.
     * @param questionNumber Position of the question in the set.
     * @param correctIndex Index of correct answer for this question in the set
     *
     * @return This Set instance.
     */
    public Set addQuestion(Question question, Integer questionNumber, Integer correctIndex) {
        try (Connection con = DB.sql2o.open();) {
            String query = "INSERT INTO sets_questions "
                + "(setId, questionId, questionNumber, correctIndex) "
                + "VALUES (:setId, :questionId, :questionNumber, :correctIndex)";
            con.createQuery(query)
                .addParameter("setId", this.id)
                .addParameter("questionId", question.getId())
                .addParameter("questionNumber", questionNumber)
                .addParameter("correctIndex", correctIndex)
                .executeUpdate();
            return this;
        }
    }

    // relations lookup

    /**
     * Gets the associated Exam instance.
     *
     * @return Associated Exam instance.
     */
    public Exam getExam() {
        try (Connection con = DB.sql2o.open();) {
            String q = "SELECT * FROM exams WHERE id=:examId";
            return con.createQuery(q)
                    .bind(this)
                    .executeAndFetchFirst(Exam.class);
        }
    }

    /**
     * Gets the list of questions in this set ordered by question number. This
     * is useful to display the question set.
     *
     * @return List of Question instances added to this Set, sorted by ascending
     *         order of question number.
     */
    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open();) {
            String q = "SELECT questions.id, userId, categoryId, text, difficulty,"
                + " questions.createdAt, questions.updatedAt FROM questions"
                + " INNER JOIN sets_questions ON questions.id = sets_questions.questionId"
                + " WHERE sets_questions.setId = :id"
                + " ORDER BY sets_questions.questionNumber ASC";
            return con.createQuery(q).bind(this).executeAndFetch(Question.class);
        }
    }

    /**
     * Gets the list of indices of correct answers of question ordered by
     * ascending question number. This is useful for displaying the answer
     * sheet.
     *
     * @return List of correct answer indices.
     */
    public List<Integer> getCorrectIndices() {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT correctIndex FROM sets_questions WHERE setId=:id ORDER BY questionNumber ASC";
            return con.createQuery(sql)
                    .bind(this)
                    .executeAndFetch(Integer.class);
        }
    }

    // static methods

    /**
     * Gets all the Set instances in the database.
     *
     * @return List of all Set instances in the database.
     */
    public static List<Set> all() {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT * FROM sets";
            return con.createQuery(sql).executeAndFetch(Set.class);
        }
    }

    /**
     * Finds Set instance by ID.
     *
     * @param id Set ID to look for.
     *
     * @return Set instance with given ID. Null, if not found.
     */
    public static Set findById(Integer id) {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT * FROM sets WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Set.class);
        }
    }
}
