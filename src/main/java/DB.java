import org.sql2o.*;


public class DB {

    public static Sql2o sql2o = new Sql2o("jdbc:mysql://localhost:3306/lis", "lis", "lis");

}
