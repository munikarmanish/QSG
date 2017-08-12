import org.sql2o.*;


/**
 * A utility class that provides the sql2o instance to work with the database.
 *
 * @author Manish Munikar
 * @since 2017-08-12
 */
public class DB {

    /**
     * A static sql2o instance to work with database throughout the application
     * life.
     */
    public static Sql2o sql2o = new Sql2o("jdbc:mysql://localhost:3306/lis",
                                          "lis", "lis");
}
