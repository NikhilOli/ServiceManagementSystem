package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.CustomerDashboard.Appointment;

public class StaffDashboard extends Application {

    private BorderPane borderPane;
    private ComboBox<String> statusComboBox;
    private Connection connection;
    private String username;
    public int userId;
    private Stage primaryStage;
    
    public StaffDashboard(String username, int userId) {
        this.username = username;
        this.userId = userId;
        

    }

    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            return;
        }

        borderPane = new BorderPane();
        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        welcomeLabel.setTextFill(Color.web("#4B5358")); 
        welcomeLabel.setPadding(new Insets(10)); 
        
        VBox leftContainer = new VBox();
        leftContainer.setAlignment(Pos.TOP_LEFT);
        leftContainer.getChildren().addAll(welcomeLabel);
        borderPane.setLeft(leftContainer);

        HBox buttonContainer = new HBox(15); 
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-background-color: #2b2b2b;"); 
        buttonContainer.setPadding(new Insets(15));

        Button scheduleManagementButton = createStyledButton("Schedule Management");
        Button serviceHistoryButton = createStyledButton("Service History");
        Button profileManagementButton = createStyledButton("Profile Management");
        Button performanceMetricsButton = createStyledButton("Performance Metrics");
        Button logoutButton = createStyledButton("Logout");

        buttonContainer.getChildren().addAll(
                scheduleManagementButton,
                serviceHistoryButton, profileManagementButton,
                performanceMetricsButton, logoutButton
        );

        borderPane.setTop(buttonContainer);

        scheduleManagementButton.setOnAction(e -> displayScheduleManagement());
        serviceHistoryButton.setOnAction(e -> displayServiceHistory(primaryStage));
        profileManagementButton.setOnAction(e -> displayProfileManagement());
        performanceMetricsButton.setOnAction(e -> {
            PerformanceMetrics performanceMetrics = new PerformanceMetrics(borderPane, userId);
            performanceMetrics.displayPerformanceMetrics(userId);
        });        
        logoutButton.setOnAction(e -> handleLogout());

        Label welcomeLabelInitial = new Label("Welcome to the Staff Dashboard, " + username + "!");
        welcomeLabelInitial.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
        welcomeLabelInitial.setTextFill(Color.web("#1B4965")); 
        welcomeLabelInitial.setPadding(new Insets(10)); 

        Label functionalitiesLabel = new Label("Explore the Features Available of Service Mangement System:\n" +
                "- Manage Schedule and Appointments\n" +
                "- View Service History\n" +
                "- Update Profile Information\n" +
                "- Check Performance Metrics");
        functionalitiesLabel.setFont(Font.font("Comic Sans MS", 18));
        functionalitiesLabel.setTextFill(Color.web("#104547")); 
        functionalitiesLabel.setPadding(new Insets(10)); 

        VBox layout = new VBox(10, welcomeLabelInitial, functionalitiesLabel);
        layout.setAlignment(Pos.CENTER);
        borderPane.setStyle("-fx-background-color: #D2D6EF;"); 
        borderPane.setCenter(layout);

        // Create scene
        Scene scene = new Scene(borderPane, 1000, 600); 

        // Set the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Dashboard");
        primaryStage.show();
    }

    private void handleLogout() {
    	if (primaryStage != null) { 
            primaryStage.close(); 

            LoginPage loginScreen = new LoginPage();
            Stage loginStage = new Stage();
            loginScreen.start(loginStage);
        }
    }
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
        button.setPrefWidth(200); 
        button.setPrefHeight(50); 
        button.setCursor(javafx.scene.Cursor.HAND); 
        return button;
    }

    private void displayScheduleManagement() {
        borderPane.setCenter(createAppointmentManagementPane());
    }

    private VBox createAppointmentManagementPane() {
        VBox appointmentPane = new VBox(20);
        appointmentPane.setAlignment(Pos.CENTER);
        appointmentPane.setPadding(new Insets(20));

        Label titleLabel = new Label("Appointment Management");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.TEAL);
        appointmentPane.getChildren().add(titleLabel);

        TableView<Appointment> appointmentTable = new TableView<>();
        appointmentTable.setPrefWidth(800);
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, Integer> appointmentIdCol = new TableColumn<>("Appointment ID");
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

        TableColumn<Appointment, Integer> customerIdCol = new TableColumn<>("Customer ID");
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Appointment, String> staffCol = new TableColumn<>("Staff");
        staffCol.setCellValueFactory(new PropertyValueFactory<>("staff"));

        TableColumn<Appointment, String> serviceCol = new TableColumn<>("Service");
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("service"));

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Appointment, Integer> staffIdCol = new TableColumn<>("Staff ID");
        staffIdCol.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        appointmentTable.getColumns().addAll(appointmentIdCol, customerIdCol, staffCol, serviceCol, dateCol, timeCol, staffIdCol, statusCol);

        appointmentTable.setItems(FXCollections.observableArrayList(getAppointmentData()));
        // Create ComboBox for updating appointment status
        statusComboBox = new ComboBox<>();
        statusComboBox.setPromptText("Select Status");

        // Fetch distinct status values from the database and populate the ComboBox
        ObservableList<String> statusValues = getDistinctStatusValues();
        statusComboBox.setItems(statusValues);

        // Create Update button for status
        Button updateStatusButton = new Button("Update Status");
        updateStatusButton.setOnAction(e -> updateAppointmentStatus(appointmentTable.getSelectionModel().getSelectedItem()));

        appointmentPane.getChildren().addAll(appointmentTable, statusComboBox, updateStatusButton);

        return appointmentPane;
    }

 // Method to fetch distinct status values from the database
            private ObservableList<String> getDistinctStatusValues() {
                ObservableList<String> statusValues = FXCollections.observableArrayList();
                try {
                    // SQL query to fetch enum values
                    String query = "SHOW COLUMNS FROM Appointments LIKE 'appointment_status'";
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    
                    // Get the enum values
                    if (resultSet.next()) {
                        String enumValues = resultSet.getString("Type");
                        // Extract enum values from the result
                        String[] enumArray = enumValues.replaceAll("^enum\\('", "").replaceAll("'\\)$", "").split("','");
                        statusValues.addAll(enumArray);
                    }

                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch appointment status values: " + e.getMessage());
                }
                return statusValues;
            }
 // Method to update appointment status
            private void updateAppointmentStatus(Appointment selectedAppointment) {
                if (selectedAppointment == null) {
                    showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an appointment to update.");
                    return;
                }

                String selectedStatus = statusComboBox.getValue();
                if (selectedStatus == null || selectedStatus.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a status.");
                    return;
                }

                try {
                    // Prepare the update statement
                    String updateQuery = "UPDATE Appointments SET appointment_status = ? WHERE appointment_id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, selectedStatus);
                    updateStatement.setInt(2, selectedAppointment.getAppointmentId());

                    // Execute the update statement
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        // Update the appointment status in the UI
                        selectedAppointment.setStatus(selectedStatus);
                        showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Appointment status updated successfully.");
                        
                        // Refresh the TableView
                        TableView<Appointment> appointmentTable = (TableView<Appointment>) borderPane.getCenter().lookup(".table-view");
                        appointmentTable.setItems(FXCollections.observableArrayList(getAppointmentData()));
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update appointment status.");
                    }

                    // Close the statement
                    updateStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update appointment status: " + e.getMessage());
                }
            }

 // Method to fetch appointment data from the database
    private ObservableList<Appointment> getAppointmentData() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");

            String query = "SELECT appointment_id, customer_id, staff_name, service_name, appointment_date, appointment_time, staff_id, appointment_status FROM Appointments WHERE appointment_status IN ('Pending', 'Accepted') AND staff_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                int customerId = resultSet.getInt("customer_id");
                String service = resultSet.getString("service_name");
                String date = resultSet.getString("appointment_date");
                String time = resultSet.getString("appointment_time");
                int staffId = resultSet.getInt("staff_id");
                String staff = (staffId == 0) ? "To be assigned" : getStaffNameById(staffId);
                String status = resultSet.getString("appointment_status");

                // Create and add appointment object to the list
                appointments.add(new Appointment(appointmentId, customerId, staff, service, date, time, staffId, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch appointment data: " + e.getMessage());
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return appointments;
    }
    private String getStaffNameById(int staffId) {
        String staffName = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT username FROM users WHERE user_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, staffId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                staffName = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch staff name.");
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return staffName;
    }
    
    private void displayServiceHistory(Stage primaryStage) {    	
        borderPane.setCenter(createAppointmentTable());
    }

    private Node createAppointmentTable() {
        return AppointmentTableUIStaff.createAppointmentTable(userId);
    }
    private void displayProfileManagement() {
        ProfileManagement profileManagement = new ProfileManagement(borderPane, userId);
        profileManagement.displayProfileManagement();
    }
    
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
