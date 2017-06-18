import org.junit.rules.ExternalResource;
import org.sql2o.*;


public class DatabaseRule extends ExternalResource {

    @Override
    protected void before() {
        DB.sql2o = new Sql2o("jdbc:mysql://localhost:3306/lis_test", "lis", "lis");
    }

    @Override
    protected void after() {
        try (Connection con = DB.sql2o.open()) {
            String[] tables = {"sets_questions", "sets", "answers", "questions", "categories", "users"};
            for (String table : tables) {
                String sql = String.format("DELETE FROM %s;", table);
                con.createQuery(sql).executeUpdate();
            }
        }
    }
}
