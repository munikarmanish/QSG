import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.*;
import org.sql2o.*;


/**
 * Represents an MCQ Question. Each question has 4 answers of which only one is
 * correct. Also, each question falls under a category and a difficulty level.
 * The difficulty levels are coded as follows:
 *     0: Easy
 *     1: Medium
 *     2: Hard
 *
 * @author Manish Munikar
 * @since 2017-08-12
 *
 * @see Answer
 * @see Category
 */
public class Question extends Timestamped {

    // constants

    public static final Integer DIFFICULTY_EASY = 0;
    public static final Integer DIFFICULTY_MEDIUM = 1;
    public static final Integer DIFFICULTY_HARD = 2;

    public static final Integer DEFAULT_DIFFICULTY = 1;

    // variables

    private Integer userId;
    private Integer categoryId;
    private String text;
    private Integer difficulty;

    // constructors

    public Question(User user, Category category, String text, Integer difficulty) {
        if (user == null) {
            this.setUserId(null);
        } else {
            this.setUserId(user.getId());
        }
        this.setCategoryId(category.getId());
        this.setText(text);
        this.setDifficulty(difficulty);
    }

    public Question(Integer userId, Integer categoryId, String text, Integer difficulty) {
        this.setUserId(userId > 0? userId : null);
        this.setCategoryId(categoryId);
        this.setText(text);
        this.setDifficulty(difficulty);
    }

    // getters and setters

    public Integer getUserId() {
        return this.userId;
    }

    public Question setUserId(Integer id) {
        this.userId = id;
        return this;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }

    public Question setCategoryId(Integer id) {
        this.categoryId = id;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public Question setText(String text) {
        this.text = text;
        return this;
    }

    public Integer getDifficulty() {
        return this.difficulty;
    }

    public Question setDifficulty(Integer difficulty) {
        // check if difficulty value is legal
        if (difficulty < DIFFICULTY_EASY || difficulty > DIFFICULTY_HARD) {
            throw new IllegalArgumentException("Invalid difficulty value");
        }
        this.difficulty = difficulty;
        return this;
    }

    // operators

    /**
     * Checks if this Question instance is equal to some other object.
     *
     * @param obj Any java object.
     *
     * @return True, if equal. False, otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // If obj is not an instance of Question class, directly return false.
        if (! (obj instanceof Question)) {
            return false;
        }
        Question question = (Question) obj;
        return this.id.equals(question.getId()) &&
            this.userId.equals(question.getUserId()) &&
            this.categoryId.equals(question.getCategoryId()) &&
            this.text.equals(question.getText()) &&
            this.difficulty.equals(question.getDifficulty());
    }

    // methods

    /**
     * Saves the Question instance to the database. Updates if already saved.
     *
     * @return Saved or updated instance.
     */
    public Question save() {
        try (Connection con = DB.sql2o.open();) {
            String query;
            if (this.id != null && this.id > 0) {
                query = "UPDATE questions SET "
                    + "userId=:userId, categoryId=:categoryId, text=:text, "
                    + "difficulty=:difficulty WHERE id=:id";
                con.createQuery(query)
                    .bind(this)
                    .executeUpdate();
            } else {
                query = "INSERT INTO questions"
                    + " (userId, categoryId, text, difficulty)"
                    + " VALUES (:userId, :categoryId, :text, :difficulty)";
                this.id = con.createQuery(query, true)
                            .bind(this)
                            .executeUpdate()
                            .getKey(Integer.class);
            }
            return Question.findById(this.id);
        }
    }

    /**
     * Delets the Question instance from the database.
     */
    public void delete() {
        try (Connection con = DB.sql2o.open();) {
            String query = "DELETE FROM questions WHERE id=:id";
            con.createQuery(query)
                .bind(this)
                .executeUpdate();
            this.setId(null);
        }
    }

    // relations lookup

    /**
     * Gets the user who created the question.
     *
     * @return User instance related to this Question instance.
     */
    public User getUser() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM users WHERE id=:userId";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetchFirst(User.class);
        }
    }

    /**
     * Gets the category in which this question belongs.
     *
     * @return Category instance associated with this Question instance.
     */
    public Category getCategory() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM categories WHERE id=:categoryId";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetchFirst(Category.class);
        }
    }

    /**
     * Gets the list of answers for this question.
     *
     * @return List of Answer instances associated with this Question instance.
     */
    public List<Answer> getAnswers() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM answers WHERE questionId=:id";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetch(Answer.class);
        }
    }

    /**
     * Gets the correct answer of this question.
     *
     * @return Correct Answer instance of this Question instance.
     */
    public Answer getCorrectAnswer() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM answers"
                + " WHERE questionId=:id AND isCorrect=TRUE"
                + " ORDER BY id DESC";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetchFirst(Answer.class);
        }
    }

    /**
     * Gets the list of incorrect answers for this question.
     *
     * @return List of incorrect Answer instances of this Question instance.
     */
    public List<Answer> getIncorrectAnswers() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM answers"
                + " WHERE questionId=:id AND isCorrect=FALSE";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetch(Answer.class);
        }
    }

    /**
     * Gets the list of answers of this question, sorted in a particular order
     * in the given question set. The correct index of every question in every
     * set is stored in the database. This method uses that stored correct_index
     * to sort the answers.
     *
     * @param set Question set where to look for order of answers.
     *
     * @return List of ordered Answer instances of this Question instance.
     */
    public List<Answer> getOrderedAnswers(Set set) {
        Integer correctIndex = 0;
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT correctIndex from sets_questions"
                + " WHERE setId=:setId AND questionId=:questionId";
            correctIndex = con.createQuery(query)
                    .addParameter("setId", set.getId())
                    .addParameter("questionId", this.id)
                    .executeAndFetchFirst(Integer.class);
        }

        List<Answer> incorrectAnswers = this.getIncorrectAnswers();
        Answer correctAnswer = this.getCorrectAnswer();

        Answer[] orderedAnswers = new Answer[4];
        orderedAnswers[correctIndex] = correctAnswer;
        orderedAnswers[(correctIndex+1) % 4] = incorrectAnswers.remove(0);
        orderedAnswers[(correctIndex+2) % 4] = incorrectAnswers.remove(0);
        orderedAnswers[(correctIndex+3) % 4] = incorrectAnswers.remove(0);
        return Arrays.asList(orderedAnswers);
    }

    /**
     * Adds an answer for this question.
     *
     * @param text Answer content.
     * @param isCorrect Is this the correct answer?
     *
     * @return this Question instance.
     */
    public Question addAnswer(String text, boolean isCorrect) {
        Answer answer = new Answer(this.id, text, isCorrect).save();
        return this;
    }

    /**
     * Get the sets which contain this question.
     *
     * @return List of Set instances that contain this Question instance.
     */
    public List<Set> getSets() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT sets.id, sets.examId, sets.setNumber,"
                + " sets.createdAt, sets.updatedAt"
                + " FROM sets INNER JOIN sets_questions"
                + " ON sets.id = sets_questions.setId"
                + " WHERE sets_questions.questionId = :id";
            return con.createQuery(query).bind(this).executeAndFetch(Set.class);
        }
    }

    // static methods

    /**
     * Gets all the questions in the database.
     *
     * @return List of all Question instances in the database.
     */
    public static List<Question> all() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM questions ORDER BY id DESC";
            return con.createQuery(query).executeAndFetch(Question.class);
        }
    }

    /**
     * Finds question by ID.
     *
     * @param id Question ID to look for.
     *
     * @return Question instance with given ID. Null, if not found.
     */
    public static Question findById(Integer id) {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM questions WHERE id=:id";
            return con.createQuery(query)
                .addParameter("id", id)
                .executeAndFetchFirst(Question.class);
        }
    }

    /**
     * Gets a slice of list of all questions in the database. This is useful for
     * pagination purposes. Returns questions in the range
     * [startIndex, startIndex + size).
     *
     * @param startIndex Starting index
     * @param size (Max) number of questions to return
     *
     * @return Sliced List of Question instances in the database.
     */
    public static List<Question> limit(Integer startIndex, Integer size) {
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery("SELECT * FROM questions ORDER BY id DESC LIMIT :start, :size")
                .addParameter("start", startIndex)
                .addParameter("size", size)
                .executeAndFetch(Question.class);
        }
    }
}
