import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import org.sql2o.*;


public class Category extends Timestamped {

    // variables

    private String name;

    // constructors

    public Category(String name) {
        this.setName(name);
    }

    // getters and setters

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // operators

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Category)) {
            return false;
        }
        Category category = (Category) obj;
        return this.name.equals(category.getName()) &&
            this.id == category.getId();
    }

    // methods

    public Category save() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "INSERT INTO categories (name) VALUES (:name)";
            this.id = con.createQuery(sql, true)
                .bind(this)
                .executeUpdate()
                .getKey(int.class);
            Category c = Category.findById(this.id);
            this.setCreatedAt(c.getCreatedAt());
            this.setUpdatedAt(c.getUpdatedAt());
            return this;
        }
    }

    public void delete() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "DELETE FROM categories WHERE id=:id";
            con.createQuery(sql).bind(this).executeUpdate();
            this.setId(0);
        }
    }

    // relations lookup

    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM questions WHERE categoryId=:id";
            return con.createQuery(sql).bind(this).executeAndFetch(Question.class);
        }
    }

    // static methods

    public static List<Category> all() {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM categories";
            return con.createQuery(sql).executeAndFetch(Category.class);
        }
    }

    public static Category findById(int id) {
        try (Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM categories WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Category.class);
        }
    }
}
