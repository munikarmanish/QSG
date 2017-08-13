import java.sql.Timestamp;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.List;
import org.sql2o.*;


/**
 * Represents a category of question.
 *
 * @author Manish Munikar
 * @since 2017-08-12
 */
public class Category extends Timestamped {

    // variables

    private String name;    // Name of category, should be unique

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

    /**
     * Equality comparison method, checks if some other object is equal to this
     * Category instance.
     *
     * @param obj Any java object.
     *
     * @return True, if equal. Otherwise, false.
     */
    @Override
    public boolean equals(Object obj) {
        // If obj is not an instance of Category, directly return false
        if (! (obj instanceof Category)) {
            return false;
        }
        Category category = (Category) obj;
        return this.name.equals(category.getName()) &&
            this.id.equals(category.getId());
    }

    // methods

    /**
     * Inserts the Category instance to the database table 'categories'.
     *
     * @return Saved Category instance.
     */
    public Category save() {
        // If a category exists with the same name, don't re-save a duplicate
        // one. Just gracefully return the existing Category instance with the
        // same name.
        Category category = Category.findByName(this.name);
        if (category != null) {
            return category;
        }

        try (Connection con = DB.sql2o.open();) {
            String query = "INSERT INTO categories (name) VALUES (:name)";
            this.id = con.createQuery(query, true)
                        .bind(this)
                        .executeUpdate()
                        .getKey(Integer.class);
            return Category.findById(this.id);
        }
    }

    /**
     * Deletes the Category instance from the database.
     *
     * Also recursively deletes all the associated questions and their answers.
     */
    public void delete() {
        try (Connection con = DB.sql2o.open();) {
            String query = "DELETE FROM categories WHERE id=:id";
            con.createQuery(query).bind(this).executeUpdate();
            this.setId(null);
        }
    }

    // relations

    /**
     * Gets the list of all the questions in this category.
     *
     * @return List of Question instances falling in his Category.
     */
    public List<Question> getQuestions() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM questions WHERE categoryId=:id";
            return con.createQuery(query)
                    .bind(this)
                    .executeAndFetch(Question.class);
        }
    }

    // static methods

    /**
     * Gets all the categories in the database.
     *
     * @return List of all Category instances in the database.
     */
    public static List<Category> all() {
        try (Connection con = DB.sql2o.open();) {
            String query = "SELECT * FROM categories ORDER BY name ASC";
            return con.createQuery(query).executeAndFetch(Category.class);
        }
    }

    /**
     * Finds Category instance by ID.
     *
     * @param id ID of category to look for.
     *
     * @return Category instance with given ID. Null, if it doesn't exist.
     */
    public static Category findById(Integer id) {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT * FROM categories WHERE id=:id";
            return con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(Category.class);
        }
    }

    /**
     * Finds Category instance by name. This works because the 'name' field of
     * 'categories' table has a UNIQUE constraint.
     *
     * @param name Name of category to look for.
     *
     * @return Category instance with given name. Null, if doesn't exist.
     */
    public static Category findByName(String name) {
        try (Connection con = DB.sql2o.open();) {
            String sql = "SELECT * FROM categories WHERE name LIKE :name";
            return con.createQuery(sql)
                .addParameter("name", name)
                .executeAndFetchFirst(Category.class);
        }
    }
}
