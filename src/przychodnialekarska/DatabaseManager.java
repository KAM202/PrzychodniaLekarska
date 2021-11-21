package przychodnialekarska;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static String databasePath = "jdbc:sqlite:src/przychodnia.db";

    private static Connection connection;

    public static Connection getConnection() throws SQLException{
        if(connection == null){
            connection = DriverManager.getConnection(databasePath);
        }
        return connection;
    }
}
