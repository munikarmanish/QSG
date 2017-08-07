import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.*;
import org.sql2o.*;


public class Question extends Timestamped {

    // constants

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    public static final Integer DEFAULT_DIFFICULTY = 1;

    // variables

    private int userId;
    private int categoryId;
    private String text;
    private int difficulty;

    // constructors

    public Question(User user, Category category, String text, int difficulty) {
        if (user == null) {
            this.setUserId(0);
        } else {
            this.setUserId(user.getId());
        }
        this.setCategoryId(category.getId());
        this.setText(text);
        this.setDifficulty(difficulty);
    }

    public Question(int userId, int categoryId, String text, int difficulty) {
        this.setUserId(userId);
        this.setCategoryId(categoryId);
        this.setText(text);
        this.setDifficulty(difficulty);
    }

    // getters and setters

    public int getUserId() {
        return this.userId;
    }

    public Question setUserId(int id) {
        this.userId = id;
        return this;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public Question setCategoryId(int id) {
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

    public int getDifficulty() {
        return this.difficulty;
    }

    public Question setDifficulty(int difficulty) {
        if (difficulty < DIFFICULTY_EASY || difficulty > DIFFICULTY_HARD) {
            throw new IllegalArgumentException("Invalid difficulty value");
        }
        this.difficulty = difficulty;
        return this;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Question)) {
            return false;
        }
        Question q = (Question) obj;
        return this.id == q.getId() &&
            this.userId == q.getUserId() &&
            this.categoryId == q.getCategoryId() &&
            this.text.equals(q.getText()) &&
            this.difficulty == q.getDifficulty();
    }

    // methods

    public Question save() {
        try (Connection con = DB.sql2o.open()) {
            Integer userId = (this.userId > 0)? this.userId : null;
            String sql;
            if (this.id > 0) {
                sql = "UPDATE questions SET userId=:userId, categoryId=:categoryId, text=:text, difficulty=:difficulty WHERE id=:id";
                con.createQuery(sql)
                        .addParameter("userId", userId)
                        .addParameter("categoryId", this.categoryId)
                        .addParameter("text", this.text)
                        .addParameter("difficulty", this.difficulty)
                        .addParameter("id", this.id)
                        .executeUpdate();
            } else {
                sql = "INSERT INTO questions (userId, categoryId, text, difficulty)"
                        + " VALUES (:userId, :categoryId, :text, :difficulty)";
                this.id = con.createQuery(sql, true)
                        .addParameter("userId", userId)
                        .addParameter("categoryId", this.categoryId)
                        .addParameter("text", this.text)
                        .addParameter("difficulty", this.difficulty)
                        .executeUpdate()
                        .getKey(int.class);
            }
            return Question.findById(this.id);
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM questions WHERE id=:id";
            con.createQuery(sql)
                .bind(this)
                .executeUpdate();
            this.id = 0;
        }
    }

    // relations lookup

    public User getUser() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM users WHERE id=:userId";
            return con.createQuery(sql).bind(this).executeAndFetchFirst(User.class);
        }
    }

    public Category getCategory() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM categories WHERE id=:categoryId";
            return con.createQuery(sql).bind(this).executeAndFetchFirst(Category.class);
        }
    }

    public List<Answer> getAnswers() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM answers WHERE questionId=:id";
            return con.createQuery(sql).bind(this).executeAndFetch(Answer.class);
        }
    }

    public Answer getCorrectAnswer() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT * FROM answers WHERE questionId=:id AND isCorrect=TRUE ORDER BY id DESC";
            return con.createQuery(q).bind(this).executeAndFetchFirst(Answer.class);
        }
    }

    public List<Answer> getIncorrectAnswers() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT * FROM answers WHERE questionId=:id AND isCorrect=FALSE";
            return con.createQuery(q).bind(this).executeAndFetch(Answer.class);
        }
    }

    public List<Answer> getOrderedAnswers(Set set) {
        Integer correctIndex = 0;
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT correctIndex from sets_questions WHERE setId=:setId AND questionId=:questionId";
            correctIndex = con.createQuery(sql)
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

    public Question addAnswer(String text, boolean isCorrect) {
        Answer a = new Answer(this.id, text, isCorrect).save();
        return this;
    }

    public List<Set> getSets() {
        try (Connection con = DB.sql2o.open()) {
            String q = "SELECT sets.id, sets.examId, sets.set,"
                + " sets.createdAt, sets.updatedAt"
                + " FROM sets INNER JOIN sets_questions ON sets.id = sets_questions.setId"
                + " WHERE sets_questions.questionId = :id";
            return con.createQuery(q).bind(this).executeAndFetch(Set.class);
        }
    }

    // static methods

    public static List<Question> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM questions ORDER BY id DESC";
            return con.createQuery(sql).executeAndFetch(Question.class);
        }
    }

    public static Question findById(int id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM questions WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Question.class);
        }
    }

    public static List<Question> limit(int start_index, int size) {
        try (Connection con = DB.sql2o.open()) {
            return con.createQuery("SELECT * FROM questions ORDER BY id DESC LIMIT :start, :size")
                .addParameter("start", start_index)
                .addParameter("size", size)
                .executeAndFetch(Question.class);
        }
    }
}
