package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ProfileManagement {
    private BorderPane borderPane;
    private int userId;

    public ProfileManagement(BorderPane borderPane, int userId) {
        this.borderPane = borderPane;
        this.userId = userId;
    }
    public void displayProfileManagement() {
        // Create UI elements for profile management
        VBox profilePane = new VBox(10);
        profilePane.setPadding(new Insets(20));
        profilePane.setAlignment(Pos.CENTER);
        profilePane.setStyle("-fx-background-color: #f0f0f0;");

        // Add profile management title
        Label titleLabel = new Label("Profile Management");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setTextFill(Color.BLUE);

        // Create form for displaying and updating user profile information
        GridPane formGrid = new GridPane();	
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(5);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        addressField.setPromptText("Enter address");
        
        populateFields(usernameField, emailField, passwordField, addressField);

        // Update button
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        updateButton.setOnAction(e -> updateUserProfile(usernameField.getText(), emailField.getText(), passwordField.getText(), addressField.getText()));

        // Add elements to the form grid
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 1, 2);
        formGrid.add(addressLabel, 0, 3);
        formGrid.add(addressField, 1, 3);

        // Add elements to the profilePane
        profilePane.getChildren().addAll(titleLabel, formGrid, updateButton);

        // Set the profilePane as the center content of the borderPane
        borderPane.setCenter(profilePane);
    }
    private void populateFields(TextField usernameField, TextField emailField, PasswordField passwordField, TextField addressField) {
        // Fetch user information from the database using DatabaseConnection class
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT username, email, userpassword, address FROM Users WHERE user_id = ?")) {

            // Set parameter for the SQL query
            statement.setInt(1, userId);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // If user information is found, populate the text fields
            if (resultSet.next()) {
                usernameField.setText(resultSet.getString("username"));
                emailField.setText(resultSet.getString("email"));
                passwordField.setText(resultSet.getString("userpassword"));
                addressField.setText(resultSet.getString("address"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database Error: " + e.getMessage());
        }
    }

    private void updateUserProfile(String username, String email, String password, String address) {
        String query = "UPDATE Users SET username = ?, email = ?, userpassword = ?, address = ? WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set parameters for the SQL query
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, address);
            statement.setInt(5, userId);

            // Execute the update query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Profile update successful
                showAlert("Success", "Profile Updated Successfully.");
            } else {
                // No rows affected, profile update failed
                showAlert("Error", "Failed to Update Profile.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database Error: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        // Display an alert with the given title and message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
