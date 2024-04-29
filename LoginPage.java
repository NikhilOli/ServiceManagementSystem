package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginPage extends Application {

    private ComboBox<String> userTypeComboBox;
    private TextField usernameField;
    private PasswordField passwordField;
    

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        // Add subtle texture to the background
        root.setBackground(new Background(new BackgroundFill(createTexture(), CornerRadii.EMPTY, Insets.EMPTY)));

        // Create a VBox to stack elements vertically
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        // Create form elements
        userTypeComboBox = new ComboBox<>();
        userTypeComboBox.getItems().addAll("Customer", "Staff");
        userTypeComboBox.setPromptText("Select User Type");
        userTypeComboBox.setPrefHeight(38);

        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        usernameField.setPrefHeight(38);
        passwordField.setPrefHeight(38);
        
        BackgroundFill backgroundFill = new BackgroundFill(Color.web("#BBBDF6"), CornerRadii.EMPTY, Insets.EMPTY);
        userTypeComboBox.setBackground(new Background(backgroundFill));

        // Create other form elements

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #17323A; -fx-text-fill: white; -fx-padding: 5 20 5 20; -fx-background-radius: 20;");


        loginButton.setPrefHeight(38);
        registerButton.setPrefHeight(40);
        registerButton.setCursor(javafx.scene.Cursor.HAND);

        Label loginLabel = new Label("Login");
        loginLabel.setFont(new Font("Arial", 24)); // Set the font size
        container.setAlignment(javafx.geometry.Pos.CENTER);
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setCursor(javafx.scene.Cursor.HAND);
        loginButton.setAlignment(javafx.geometry.Pos.CENTER);

        // Background color for buttons
        loginButton.setBackground(new Background(new BackgroundFill(Color.rgb(76, 175, 80), CornerRadii.EMPTY, Insets.EMPTY)));
        registerButton.setBackground(new Background(new BackgroundFill(Color.rgb(23, 42, 58), CornerRadii.EMPTY, Insets.EMPTY)));

        loginButton.setTextFill(Color.WHITE);
        registerButton.setTextFill(Color.WHITE);

        container.getChildren().addAll(
                loginLabel,
                userTypeComboBox,
                usernameField,
                passwordField,
                loginButton,
                registerButton
        );

        loginButton.setOnAction(e -> loginButtonClicked());
        registerButton.setOnAction(e -> goToRegisterPage(primaryStage));

        root.getChildren().add(container);
        StackPane.setMargin(container, new Insets(50));

        Scene scene = new Scene(root, 400, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }

    private void loginButtonClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if ("admin".equals(username) && "123".equals(password)) {
            openAdminDashboard();
            return;
        }
        
        String userType = userTypeComboBox.getValue();
        if (userType == null || username.isEmpty() || password.isEmpty()) {
            showError("Please enter all required fields.");
            return;
        }

        int userId = DatabaseHelper.getUserId(username);
        if (userId != -1) {
            // Check if the entered password matches the one stored in the database
            if (DatabaseHelper.validateUserPassword(userId, password)) {
                openDashboard(userType, username, userId);
            } else {
                showError("Invalid password.");
            }
        } else {
            showError("Invalid username or user type.");
        }
    
    }
    private void goToRegisterPage(Stage primaryStage) {
        RegistrationPage registerPage = new RegistrationPage();
        registerPage.start(new Stage());
        primaryStage.close();
    }
    private void openDashboard(String userType, String username, int userId) {
        if ("Staff".equals(userType)) {
            StaffDashboard staffDashboard = new StaffDashboard(username, userId);
            staffDashboard.start(new Stage());
        }else if ("Customer".equals(userType)) {
            CustomerDashboard customerDashboard = new CustomerDashboard(username, userId);
            customerDashboard.start(new Stage());
        }
        Stage stage = (Stage) userTypeComboBox.getScene().getWindow();
        stage.close();
    }
    private void openAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(); // Assuming AdminDashboard is your admin dashboard class
        adminDashboard.start(new Stage());

        // Close the current login window
        Stage stage = (Stage) userTypeComboBox.getScene().getWindow();
        stage.close();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private LinearGradient createTexture() {
        Stop[] stops = new Stop[] { new Stop(0, Color.rgb(230, 230, 230)), new Stop(1, Color.rgb(210, 245, 250)) };
        return new LinearGradient(0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE, stops);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
