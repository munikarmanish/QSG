import org.junit.rules.ExternalResource;
import org.sql2o.*;


/**
 * A rule for all the test cases. All it does is connect to the test database
 * instead of the main database, and clears the database after every test case.
 *
 * @author Manish Munikar
 * @since 2017-08-13
 */
public class DatabaseRule extends ExternalResource {

    /**
     * This method is called before the execution of all the test cases which
     * have added this rule.
     */
    @Override
    protected void before() {
        DB.sql2o = new Sql2o("jdbc:mysql://localhost:3306/lis_test", "lis", "lis");
    }

    /**
     * This method is called after the execution of all the test cases which
     * have added this rule.
     */
    @Override
    protected void after() {
        try (Connection con = DB.sql2o.open()) {
            // List of database tables to empty.
            String[] tables = {
                "sets_questions", "sets", "answers", "questions", "categories",
                "exams", "users"
            };

            for (String table : tables) {
                String query = String.format("DELETE FROM %s;", table);
                con.createQuery(query).executeUpdate();
            }
        }
    }
}
