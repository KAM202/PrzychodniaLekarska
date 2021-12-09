package przychodnialekarska;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static String databasePath = "jdbc:sqlite:przychodnia.db"; //production
    //private static String databasePath = "jdbc:sqlite:C:\\Users\\kam\\IdeaProjects\\PrzychodniaLekarska\\out\\artifacts\\PrzychodniaLekarska_jar3\\przychodnia.db";

    private static Connection connection;

    public static Connection getConnection() throws SQLException{
        if(connection == null){
            connection = DriverManager.getConnection(databasePath);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException{
        if(connection != null){
            connection.close();
            connection = null;
        }
    }
}
