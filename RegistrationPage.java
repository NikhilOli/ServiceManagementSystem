package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegistrationPage extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;


        Label userTypeLabel = new Label("User Type:");
        ComboBox<String> userTypeComboBox = new ComboBox<>();
        userTypeComboBox.setPromptText("User Type"); 
        userTypeComboBox.getItems().addAll("Staff", "Customer");
        userTypeComboBox.setStyle("-fx-font-size: 14px;"); 

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameLabel.setStyle("-fx-font-size: 14px;"); 
        usernameField.setStyle("-fx-font-size: 14px;"); 

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordLabel.setStyle("-fx-font-size: 14px;"); 
        passwordField.setStyle("-fx-font-size: 14px;"); 

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.setPromptText("Staff Role");
        roleLabel.setStyle("-fx-font-size: 14px;"); 
        roleComboBox.setStyle("-fx-font-size: 14px;");
        populateRoles(roleComboBox); 


        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        addressLabel.setStyle("-fx-font-size: 14px;"); 
        addressField.setStyle("-fx-font-size: 14px;"); 

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailLabel.setStyle("-fx-font-size: 14px;"); 
        emailField.setStyle("-fx-font-size: 14px;"); 

        Label genderLabel = new Label("Gender:");
        RadioButton maleRadioButton = new RadioButton("Male");
        RadioButton femaleRadioButton = new RadioButton("Female");
        RadioButton otherRadioButton = new RadioButton("Other");
        ToggleGroup genderToggleGroup = new ToggleGroup();
        maleRadioButton.setToggleGroup(genderToggleGroup);
        femaleRadioButton.setToggleGroup(genderToggleGroup);
        otherRadioButton.setToggleGroup(genderToggleGroup);
        GridPane genderPane = new GridPane();
        genderPane.setHgap(10);
        genderPane.add(maleRadioButton, 0, 0);
        genderPane.add(femaleRadioButton, 1, 0);
        genderPane.add(otherRadioButton, 2, 0);
        genderPane.setAlignment(Pos.CENTER);
        genderLabel.setStyle("-fx-font-size: 14px;"); 

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-font-size: 14px; -fx-background-color: #648381; -fx-text-fill: white; -fx-font-weight: bold;"); 

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 14px;");

        // Apply styling
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setBackground(new Background(new BackgroundFill(Color.web("#B9FAB2"), CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setAlignment(Pos.CENTER); // Center all elements in the grid

        // Add elements to the grid
        gridPane.add(userTypeLabel, 0, 0);
        gridPane.add(userTypeComboBox, 1, 0);
        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(passwordLabel, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(roleLabel, 0, 3);
        gridPane.add(roleComboBox, 1, 3);
        gridPane.add(addressLabel, 0, 4);
        gridPane.add(addressField, 1, 4);
        gridPane.add(emailLabel, 0, 5);
        gridPane.add(emailField, 1, 5);
        gridPane.add(genderLabel, 0, 6);
        gridPane.add(genderPane, 1, 6);
        gridPane.add(registerButton, 0, 7);
        gridPane.add(loginButton, 1, 7);

        // Create scene
        Scene scene = new Scene(gridPane, 500, 400);

        loginButton.setOnAction(e -> goToLoginPage(primaryStage));

        // Set button event handler
        registerButton.setOnAction(e -> registerUser(usernameField.getText(), passwordField.getText(), userTypeComboBox.getValue(), roleComboBox.getValue(), addressField.getText(), emailField.getText(), getSelectedGender(genderToggleGroup)));

        // Set stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Registration");
        primaryStage.show();
    }

    private void goToLoginPage(Stage primaryStage) {
        System.out.println("Register button clicked");
        // Open the registration page
        LoginPage loginPage = new LoginPage();
        loginPage.start(new Stage());
        // Close the login page
        primaryStage.close();
    }

    private void registerUser(String username, String password, String userType, String role, String address, String email, String gender) {
        // Validation: Check if userType is null
        if (userType == null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please select a user type.");
            return;
        }

        // Validation: Check if username, password, email, and gender are not empty
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || gender.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all required fields (Username, Password, Email, Gender).");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please enter a valid email address.");
            return;
        }

        // Database connection
        Connection connection = DatabaseConnection.getConnection();
        if (connection == null) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            return;
        }

        try {
            // Check if the user already exists in the database
            if (userExists(connection, username, email)) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "User with the same username or email already exists.");
                return;
            }

            // SQL INSERT statement
            String sql = "INSERT INTO users (user_type, username, userpassword, staff_role, address, email, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";

            // Prepare the statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userType);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, role);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, email);
            preparedStatement.setString(7, gender);

            // Execute the statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
                goToLoginPage();
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Failed to register user.");
            }

            // Close the statement
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while registering user.");
        } finally {
            // Close the connection
            DatabaseConnection.closeConnection();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void populateRoles(ComboBox<String> roleComboBox) {
        // Fetch staff roles from the database and populate the ComboBox

        // Database connection
        Connection connection = DatabaseConnection.getConnection();
        if (connection == null) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            return;
        }

        // SQL SELECT statement
        String sql = "SELECT service_name FROM services";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the results
            while (resultSet.next()) {
                String roleName = resultSet.getString("service_name");
                roleComboBox.getItems().add(roleName);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while fetching roles.");
        } finally {
            // Close the connection
            DatabaseConnection.closeConnection();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    private boolean userExists(Connection connection, String username, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, email);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }
    private void goToLoginPage() {
        // Open the login page
    	primaryStage.close();
        LoginPage loginPage = new LoginPage();
        loginPage.start(new Stage());
        // Close the registration window
    }

    private String getSelectedGender(ToggleGroup toggleGroup) {
        RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
        return selectedRadioButton != null ? selectedRadioButton.getText() : "";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
