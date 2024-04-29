package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private static final String HOST ="localhost";
	private static final int PORT =3306;
	private static final String DATABASE="serviceMS";
    private static final String URL = "jdbc:mysql://"+HOST+":"+PORT+"/"+DATABASE;
    private static final String USER = "root";
    private static final String PASSWORD = "Nikhil@2003";

    private static Connection connection;

    // Method to establish database connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Method to close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
