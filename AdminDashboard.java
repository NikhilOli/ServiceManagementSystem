package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboard extends Application {

    private BorderPane borderPane;
    private VBox buttonContainer;
    private TableView<Appointment> appointmentTable;
    private ComboBox<String> staffComboBox;
    private ComboBox<String> statusComboBox;
    private Stage primaryStage;

    // Database connection
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
        // Establish database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
            return;
        }

        // Create main layout pane
        borderPane = new BorderPane();

        // Create button container on the left side
        buttonContainer = new VBox(20); 
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-background-color: #2b2b2b;"); 
        buttonContainer.setPadding(new Insets(20)); 

        // Create buttons
        Button manageCustomersButton = createStyledButton("Manage Customers");
        Button manageStaffsButton = createStyledButton("Manage Staffs");
        Button scheduleAppointmentsButton = createStyledButton("Schedule Appointments");
        Button serviceHistoryButton = createStyledButton("Service History");
        Button viewPerformanceButton = createStyledButton("View Overall Performance Metrics");

        // Add buttons to the container with padding
        buttonContainer.getChildren().addAll(
                manageCustomersButton, manageStaffsButton,
                scheduleAppointmentsButton, serviceHistoryButton,
                viewPerformanceButton
        );

        // Add button container to the left side
        borderPane.setLeft(buttonContainer);

        Label usernameLabel = new Label("Welcome Admin"); 
        usernameLabel.setStyle("-fx-text-fill: white;");
        buttonContainer.getChildren().add(0, usernameLabel);

        // Add logout button at the bottom
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #222; -fx-text-fill: white;");
        logoutButton.setCursor(javafx.scene.Cursor.HAND); 
        logoutButton.setMaxWidth(Double.MAX_VALUE); 
        buttonContainer.getChildren().add(logoutButton);

        // Create initial label on the right side
        Label welcomeLabel = new Label("Welcome to the Admin Dashboard!");
        welcomeLabel.setStyle("-fx-font-size: 24px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-text-fill: #0077B6;");  
        Label secondaryLabel = new Label("Explore the Capabilities of Our System:\n\n");
        secondaryLabel.setStyle("-fx-font-size: 20px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-text-fill: #4B5358;"); 

        Label functionalitiesLabel = new Label(
                                               "- View and Manage Customer Accounts\n" +
                                                "- Handle Staff Information Efficiently\n" +
                                                "- Monitor Appointment Details and Assignments\n" +
                                                "Begin Exploring through the Menu Options!");
        functionalitiesLabel.setStyle("-fx-font-size: 18px; " +
                                      "-fx-text-fill: #104547;");  
        functionalitiesLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        welcomeLabel.setFont(Font.font("Trebuchet MS", FontWeight.NORMAL, 18));
        secondaryLabel.setFont(Font.font("Comic Sans MS", FontWeight.NORMAL, 18));

        VBox layout = new VBox(10, welcomeLabel, secondaryLabel, functionalitiesLabel);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        borderPane.setStyle("-fx-background-color: #D2D6EF;"); 

        borderPane.setCenter(layout);


        // Create scene
        Scene scene = new Scene(borderPane, 1000, 600); 


        // Set the scene
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();

        // Set button event handlers
        scheduleAppointmentsButton.setOnAction(e -> displayAppointments());
        serviceHistoryButton.setOnAction(e -> displayServiceHistory(primaryStage));
        manageCustomersButton.setOnAction(e -> displayCustomers());
        manageStaffsButton.setOnAction(e -> displayStaffs());
        viewPerformanceButton.setOnAction(e -> {
            PerformanceMetricsA performanceMetricsA = new PerformanceMetricsA(borderPane);
            performanceMetricsA.displayPerformanceMetrics();
        });        
        logoutButton.setOnAction(e -> handleLogout());
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
        button.setMaxWidth(Double.MAX_VALUE); 
        button.setCursor(javafx.scene.Cursor.HAND); 
        DropShadow shadow = new DropShadow();
        button.setOnMouseEntered(e -> button.setEffect(shadow)); 
        button.setOnMouseExited(e -> button.setEffect(null)); 
        button.setOnAction(e -> updateFunctionalityPage(text + " Functionality Page"));
        return button;
    }

    // Method to update functionality page
    private void updateFunctionalityPage(String pageContent) {
        // Update the content on the right side
        Label pageLabel = new Label(pageContent);
        pageLabel.setAlignment(Pos.CENTER);
        borderPane.setCenter(pageLabel);
    }

 // Method to display appointments in the schedule appointments section
    private void displayAppointments() {
        // Fetch appointments from the database
        ObservableList<Appointment> appointments = getAppointmentData();

        // Create a table view to display appointments
        appointmentTable = new TableView<>();

        // Set the items to the class-level appointmentTable
        appointmentTable.setItems(appointments);
        
        // Add event handler for row selection
        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Get the selected appointment's service role
                String serviceRole = newSelection.getService();

                // Fetch staff names based on service role and populate the ComboBox
                ObservableList<String> filteredStaffNames = getStaffNamesByRole(serviceRole);
                staffComboBox.setItems(filteredStaffNames);
            }
        });
        Label viewAppointmentsLabel = new Label("View Appointments");
        viewAppointmentsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        StackPane stackPane = new StackPane(viewAppointmentsLabel);
        stackPane.setAlignment(Pos.CENTER);
 
        // Create ComboBox for selecting available staff
        staffComboBox = new ComboBox<>();
        staffComboBox.setPromptText("Select Staff");
     // Add event handler for staff combobox click
        staffComboBox.setOnMouseClicked(e -> {
            // Check if no row is selected in the appointment table
            if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an appointment from the table.");
                staffComboBox.getSelectionModel().clearSelection(); // Clear staff combobox selection
            } else {
                // Fetch data if a row is selected
                // Get the selected appointment's service role
                String serviceRole = appointmentTable.getSelectionModel().getSelectedItem().getService();

                // Fetch staff names based on service role and populate the ComboBox
                ObservableList<String> filteredStaffNames = getStaffNamesByRole(serviceRole);
                staffComboBox.setItems(filteredStaffNames);
            }
        });

        
        // Fetch available staff from the database and populate the ComboBox
        ObservableList<String> availableStaff = getAvailableStaff();
        staffComboBox.setItems(availableStaff);
        
        // Create ComboBox for updating appointment status
        statusComboBox = new ComboBox<>();
        statusComboBox.setPromptText("Select Status");
        
        // Fetch distinct status values from the database and populate the ComboBox
        ObservableList<String> statusValues = getDistinctStatusValues(); 
        statusComboBox.setItems(statusValues);
        
        // Create Update buttons
        Button updateStaffButton = new Button("Assign Staff");
        Button updateStatusButton = new Button("Update Status");
        
        // Add event handlers to the update buttons
        updateStaffButton.setOnAction(e -> updateStaff());
        updateStatusButton.setOnAction(e -> updateStatus());
        
        // Create HBox to hold the ComboBoxes and Update buttons
        HBox comboBoxesAndButtonsBox = new HBox(10, staffComboBox, updateStaffButton, statusComboBox, updateStatusButton);
        comboBoxesAndButtonsBox.setAlignment(Pos.CENTER);
        comboBoxesAndButtonsBox.setPadding(new Insets(10));

        // Create table columns
        TableColumn<Appointment, Integer> appointmentIdCol = new TableColumn<>("Appointment ID");
        appointmentIdCol.setCellValueFactory(data -> data.getValue().appointmentIdProperty().asObject());

        TableColumn<Appointment, Integer> customerIdCol = new TableColumn<>("Customer ID");
        customerIdCol.setCellValueFactory(data -> data.getValue().customerIdProperty().asObject());
        
        TableColumn<Appointment, String> staffCol = new TableColumn<>("Staff");
        staffCol.setCellValueFactory(data -> data.getValue().staffProperty());


        TableColumn<Appointment, String> serviceCol = new TableColumn<>("Service");
        serviceCol.setCellValueFactory(data -> data.getValue().serviceProperty());

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());

        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> data.getValue().timeProperty());

        TableColumn<Appointment, Integer> staffIdCol = new TableColumn<>("Staff ID");
        staffIdCol.setCellValueFactory(data -> data.getValue().staffIdProperty().asObject());

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        
        TableColumn<Appointment, String> customerNameCol = new TableColumn<>("Customer Name");
        customerNameCol.setCellValueFactory(data -> data.getValue().customerNameProperty());

        // Add columns to the table view
        appointmentTable.getColumns().addAll(appointmentIdCol, customerIdCol, staffCol, serviceCol, dateCol, timeCol, staffIdCol, statusCol, customerNameCol);

        // Create VBox to hold the table view and the ComboBox/Button HBox
        VBox scheduleAppointmentLayout = new VBox(10, stackPane, comboBoxesAndButtonsBox, appointmentTable);
        scheduleAppointmentLayout.setPadding(new Insets(10));

        // Update the content on the right side of the BorderPane
        borderPane.setCenter(scheduleAppointmentLayout);
    }

 // Method to update staff
    private void updateStaff() {
        // Get the selected appointment
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an appointment to update.");
            return;
        }

        // Get the selected staff from the ComboBox
        String selectedStaff = staffComboBox.getValue();

        if (selectedStaff == null || selectedStaff.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a staff to assign.");
            return;
        }

        try {
            // Fetch the staff ID corresponding to the selected staff name
            int staffId = getStaffIdByName(selectedStaff);

            // Prepare the update statement
            String updateQuery = "UPDATE Appointments SET staff_id = ?, staff_name = ? WHERE appointment_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, staffId);
            updateStatement.setString(2, selectedStaff);
            updateStatement.setInt(3, selectedAppointment.getAppointmentId());

            // Execute the update statement
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Update the appointment's staff information in the UI
                selectedAppointment.setStaff(selectedStaff);
                selectedAppointment.staffIdProperty().set(staffId);
                appointmentTable.refresh(); // Refresh the table view
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Staff assigned successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to assign staff to the appointment.");
            }

            // Close the statement
            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to assign staff: " + e.getMessage());
        }
    }

 // Method to fetch staff ID by name from the database
    private int getStaffIdByName(String staffName) throws SQLException {
        int staffId = 0;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT user_id FROM users WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, staffName);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                staffId = resultSet.getInt("user_id");
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return staffId;
    }


	// Method to update status
    private void updateStatus() {
        // Get the selected appointment
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an appointment to update.");
            return;
        }

        // Get the selected status from the ComboBox
        String selectedStatus = statusComboBox.getValue();

        if (selectedStatus == null) {
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
                appointmentTable.refresh(); // Refresh the table view
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Appointment status updated successfully.");
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
 // Method to fetch staff names based on service role
    private ObservableList<String> getStaffNamesByRole(String serviceRole) {
        ObservableList<String> staffNames = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // SQL query to fetch staff names based on service role
            String query = "SELECT username FROM users WHERE staff_role = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, serviceRole);
            resultSet = statement.executeQuery();
            // Add retrieved staff names to the list
            while (resultSet.next()) {
                staffNames.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch staff names.");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return staffNames;
    }

 // Method to fetch distinct status values from the database enum
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

    
    // Method to fetch available staff from the database
    private ObservableList<String> getAvailableStaff() {
        
        ObservableList<String> availableStaff = FXCollections.observableArrayList();
        availableStaff.addAll("Staff 1", "Staff 2", "Staff 3"); 
        return availableStaff;
    }

 

    // Method to fetch appointments from the database
    private ObservableList<Appointment> getAppointmentData() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // SQL query to fetch appointments
            String query = "SELECT * FROM Appointments WHERE appointment_status IN ('Pending', 'Accepted')";
            statement = connection.prepareStatement(query);

            // Execute query
            resultSet = statement.executeQuery();

            // Iterate through result set and add appointments to list
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                int customerId = resultSet.getInt("customer_id");
                int staffId = resultSet.getInt("staff_id");
                String service = resultSet.getString("service_name");
                String date = resultSet.getString("appointment_date");
                String time = resultSet.getString("appointment_time");
                String staff = (staffId == 0) ? "To be assigned" : getStaffNameById(staffId);
                String status = resultSet.getString("appointment_status");
                String customerName = resultSet.getString("customer_name");
                appointments.add(new Appointment(appointmentId, service, date, time, staff, status, customerName, customerId, staffId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch appointments: " + e.getMessage());
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return appointments;
    }

    // Method to fetch staff name by ID from the database
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
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return staffName;
    }

    // Method to display an alert
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Appointment class
    public static class Appointment {
        private final StringProperty service;
        private final StringProperty date;
        private final StringProperty time;
        private final StringProperty staff;
        private final StringProperty status;
        private final IntegerProperty staffId; 
        private final IntegerProperty appointmentId;
        private final IntegerProperty customerId;
        private final StringProperty customerName;


        public Appointment(int appointmentId, String service, String date, String time, String staff, String status, String customerName, int customerId, int staffId) {
            this.service = new SimpleStringProperty(service);
            this.date = new SimpleStringProperty(date);
            this.time = new SimpleStringProperty(time);
            this.staff = new SimpleStringProperty(staff);
            this.status = new SimpleStringProperty(status);
            this.customerName = new SimpleStringProperty(customerName);
            this.staffId = new SimpleIntegerProperty(staffId); 
            this.appointmentId = new SimpleIntegerProperty(appointmentId);
            this.customerId = new SimpleIntegerProperty(customerId);
            
        }

        public int getAppointmentId() {
            return appointmentId.get();
        }
        
        // Getters and setters for properties
        public String getService() {
            return service.get();
        }

        public void setService(String service) {
            this.service.set(service);
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public String getTime() {
            return time.get();
        }

        public void setTime(String time) {
            this.time.set(time);
        }

        public String getStaff() {
            return staff.get();
        }

        public void setStaff(String staff) {
            this.staff.set(staff);
        }

        public String getStatus() {
            return status.get();
        }
        public String getCustomerName() {
            return customerName.get();
        }
        public void setCustomerName(String customerName) {
            this.customerName.set(customerName);
        }

        public void setStatus(String status) {
            this.status.set(status);
        }

        public IntegerProperty staffIdProperty() {
            return staffId;
        }

        public IntegerProperty appointmentIdProperty() {
            return appointmentId;
        }

        public IntegerProperty customerIdProperty() {
            return customerId;
        }

        public StringProperty serviceProperty() {
            return service;
        }

        public StringProperty dateProperty() {
            return date;
        }

        public StringProperty timeProperty() {
            return time;
        }

        public StringProperty staffProperty() {
            return staff;
        }

        public StringProperty statusProperty() {
            return status;
        }
        public StringProperty customerNameProperty() {
            return customerName;
        }
    }
    private void displayServiceHistory(Stage primaryStage) {
        // Replace the center content with the appointment table
        borderPane.setCenter(createAppointmentTable());
    }

    private Node createAppointmentTable() {
        // Create and return the appointment table
        return AppointmentTableUI.createAppointmentTable();
    }
 
    private ObservableList<ObservableList<String>> getCustomerData() {
        ObservableList<ObservableList<String>> customers = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // SQL query to fetch customers where usertype is "customer"
            String query = "SELECT user_id, username, email, address, gender FROM users WHERE user_type = 'customer'";
            statement = connection.prepareStatement(query);

            // Execute query
            resultSet = statement.executeQuery();

            // Iterate through result set and add customers to list
            while (resultSet.next()) {
                ObservableList<String> customer = FXCollections.observableArrayList();
                customer.add(resultSet.getString("user_id"));
                customer.add(resultSet.getString("username"));
                customer.add(resultSet.getString("email"));
                customer.add(resultSet.getString("address"));
                customer.add(resultSet.getString("gender"));

                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch customers: " + e.getMessage());
        } finally {
            // Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return customers;
    }

    private TableView<ObservableList<String>> customerTable; 
    // Method to display customer data

    private void displayCustomers() {
    	customerTable = new TableView<>();

    	Label customersInfo = new Label("Customers Information");
        customersInfo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Button deleteButton = new Button("Delete Customer");
        deleteButton.setStyle("-fx-background-color: #ff6347; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;");
        deleteButton.setOnAction(event -> deleteCustomer());
        HBox headerBox = new HBox(10, deleteButton);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));


        StackPane stackPane = new StackPane(customersInfo);
        stackPane.setAlignment(Pos.CENTER);
 
        // Fetch customer data from the database
        ObservableList<ObservableList<String>> customers = getCustomerData();

        // Set the items to the table
        customerTable.setItems(customers);

        // Define table columns
        TableColumn<ObservableList<String>, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        userIdCol.setPrefWidth(100); 

        TableColumn<ObservableList<String>, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        usernameCol.setPrefWidth(150); 

        TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        emailCol.setPrefWidth(200); 

        TableColumn<ObservableList<String>, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        addressCol.setPrefWidth(250); 

        TableColumn<ObservableList<String>, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
        genderCol.setPrefWidth(100); 
        // Add columns to the table view
        customerTable.getColumns().addAll(userIdCol, usernameCol, emailCol, addressCol, genderCol);

        // Set table to resize columns to fit content
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add the table to the layout
        VBox layout = new VBox(10, stackPane, headerBox, customerTable);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Update the content on the right side of the BorderPane
        borderPane.setCenter(layout);
    }


    // Method to delete customer from the database
    private void deleteCustomer() {
        // Get the selected row from the TableView
        ObservableList<String> selectedRow = customerTable.getSelectionModel().getSelectedItem();
        
        if (selectedRow != null) {
            int userId = Integer.parseInt(selectedRow.get(0)); 

            PreparedStatement statement = null;
            try {
                // SQL query to delete customer
                String query = "DELETE FROM users WHERE user_id = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, userId);
                
                // Execute the delete statement
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted successfully.");

                    // Remove the selected row from the TableView
                    customerTable.getItems().remove(selectedRow);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete customer.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete customer: " + e.getMessage());
            } finally {
                // Close the statement
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer to delete.");
        }
    }
    //Staff managing
    private ObservableList<ObservableList<String>> getStaffData() {
        ObservableList<ObservableList<String>> staffs = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT user_id, username, email, address, gender, staff_role FROM users WHERE user_type = 'staff'";
            statement = connection.prepareStatement(query);

            // Execute query
            resultSet = statement.executeQuery();

            // Iterate through result set and add customers to list
            while (resultSet.next()) {
                ObservableList<String> staff = FXCollections.observableArrayList();
                staff.add(resultSet.getString("user_id"));
                staff.add(resultSet.getString("username"));
                staff.add(resultSet.getString("email"));
                staff.add(resultSet.getString("address"));
                staff.add(resultSet.getString("gender"));
                staff.add(resultSet.getString("staff_role"));

                staffs.add(staff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch customers: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return staffs;
    }

 // Method to display staff data
    private TableView<ObservableList<String>> staffTable; 

    private void displayStaffs() {
    	staffTable = new TableView<>();

    	Label staffsInfo = new Label("Staffs Information");
        staffsInfo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Button deleteButton = new Button("Delete Staff");
        deleteButton.setStyle("-fx-background-color: #ff6347; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5; -fx-cursor: hand;");
        deleteButton.setOnAction(event -> deleteStaff());
        HBox headerBox = new HBox(10, deleteButton);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));


        StackPane stackPane = new StackPane(staffsInfo);
        stackPane.setAlignment(Pos.CENTER);
 
        // Fetch customer data from the database
        ObservableList<ObservableList<String>> staffs = getStaffData();

        // Set the items to the table
        staffTable.setItems(staffs);

        // Define table columns
        TableColumn<ObservableList<String>, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        userIdCol.setPrefWidth(100); 

        TableColumn<ObservableList<String>, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        usernameCol.setPrefWidth(150); 

        TableColumn<ObservableList<String>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        emailCol.setPrefWidth(200); 

        TableColumn<ObservableList<String>, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        addressCol.setPrefWidth(250); 

        TableColumn<ObservableList<String>, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
        genderCol.setPrefWidth(100); 
        TableColumn<ObservableList<String>, String> roleCol = new TableColumn<>("Staff Role");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(5)));
        roleCol.setPrefWidth(100); 

        // Add columns to the table view
        staffTable.getColumns().addAll(userIdCol, usernameCol, emailCol, addressCol, genderCol, roleCol);

        // Set table to resize columns to fit content
        staffTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add the table to the layout
        VBox layout = new VBox(10, stackPane, headerBox, staffTable);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Update the content on the right side of the BorderPane
        borderPane.setCenter(layout);
    }


    // Method to delete customer from the database
    private void deleteStaff() {
        // Get the selected row from the TableView
        ObservableList<String> selectedRow = staffTable.getSelectionModel().getSelectedItem();
        
        if (selectedRow != null) {
            int userId = Integer.parseInt(selectedRow.get(0)); 

            PreparedStatement statement = null;
            try {
                // SQL query to delete customer
                String query = "DELETE FROM users WHERE user_id = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, userId);
                
                // Execute the delete statement
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Staff deleted successfully.");

                    // Remove the selected row from the TableView
                    staffTable.getItems().remove(selectedRow);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete staff.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete staff: " + e.getMessage());
            } finally {
                // Close the statement
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a staff to delete.");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
