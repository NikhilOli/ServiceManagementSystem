package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
	 private static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");
	    }

    // Method to get user ID from username
    public static int getUserId(String username) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int userId = -1; // Default value if user not found

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");

            // SQL query to fetch user ID
            String query = "SELECT user_id FROM Users WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);

            // Execute query
            resultSet = statement.executeQuery();

            // Check if user exists and retrieve the user ID
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle closing errors
            }
        }

        return userId;
    }
    public static boolean validateUserPassword(int userId, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isValid = false;

        try {
            // Establish database connection
            connection = getConnection();

            // SQL query to fetch user password
            String query = "SELECT userpassword FROM Users WHERE user_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            // Execute query
            resultSet = statement.executeQuery();

            // Check if user exists and retrieve the user password
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("userpassword");
                isValid = storedPassword.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle closing errors
            }
        }

        return isValid;
    }
}
    


