package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerDashboard extends Application {
    private ObservableList<Appointment> appointments;

    private BorderPane borderPane;
    private VBox buttonContainer;
    private Connection connection;
    private String username;
    public int userId;
    private Stage primaryStage;

    public CustomerDashboard(String username, int userId) {
        this.username = username;
        this.userId = userId;

    }
    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
    
        borderPane = new BorderPane();

        buttonContainer = new VBox(20); 
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-background-color: #2b2b2b;"); 
        buttonContainer.setPadding(new Insets(20)); 

        Button serviceRequestsButton = createStyledButton("Service Requests");
        Button appointmentManagementButton = createStyledButton("Appointment Management");
        Button serviceHistoryButton = createStyledButton("Service History");
        Button profileManagementButton = createStyledButton("Profile Management");
        Button performanceMetricsButton = createStyledButton("Performance Metrics");

        buttonContainer.getChildren().addAll(
                serviceRequestsButton, appointmentManagementButton,
                serviceHistoryButton, profileManagementButton,
                performanceMetricsButton
        );

        borderPane.setLeft(buttonContainer);

        Label welcomeLabel = new Label("Welcome, " + username); 
        welcomeLabel.setStyle("-fx-text-fill: white;");
        welcomeLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        buttonContainer.getChildren().add(0, welcomeLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #222; -fx-text-fill: white;");
        logoutButton.setCursor(javafx.scene.Cursor.HAND); 
        logoutButton.setMaxWidth(Double.MAX_VALUE); 
        buttonContainer.getChildren().add(logoutButton);

        Label welcomeLabelInitial = new Label("Welcome to the Customer Dashboard, " + username + "!");
        welcomeLabelInitial.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
        welcomeLabelInitial.setTextFill(Color.web("#1B4965")); 
        welcomeLabelInitial.setPadding(new Insets(10)); 

        Label functionalitiesLabel = new Label("Explore the Features Available of Service Management System:\n" +
                "- Manage Service Requests\n" +
                "- Manage Appointments\n" +
                "- View Service History\n" +
                "- Update Profile Information\n" +
                "- Check Performance Metrics");
        functionalitiesLabel.setFont(Font.font("Comic Sans MS", 18));
        functionalitiesLabel.setTextFill(Color.web("#1B4965")); 
        functionalitiesLabel.setPadding(new Insets(10)); 

        VBox layout = new VBox(10, welcomeLabelInitial, functionalitiesLabel);
        layout.setAlignment(Pos.CENTER);
        borderPane.setStyle("-fx-background-color: #D2D6EF;"); 
        borderPane.setCenter(layout);


        Scene scene = new Scene(borderPane, 1000, 600); 

        primaryStage.setScene(scene);
        primaryStage.setTitle("Customer Dashboard");
        primaryStage.show();

        serviceRequestsButton.setOnAction(e -> displayServiceRequests(primaryStage));
        appointmentManagementButton.setOnAction(e -> displayAppointmentManagement(primaryStage));
        serviceHistoryButton.setOnAction(e -> displayServiceHistory(primaryStage));
        profileManagementButton.setOnAction(e -> displayProfileManagement());
        performanceMetricsButton.setOnAction(e -> {
            PerformanceMetricsC performanceMetricsC = new PerformanceMetricsC(borderPane, userId);
            performanceMetricsC.displayPerformanceMetrics(userId);
        });                
        logoutButton.setOnAction(e -> handleLogout());
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
        button.setMaxWidth(Double.MAX_VALUE); 
        button.setCursor(javafx.scene.Cursor.HAND); 
        DropShadow shadow = new DropShadow();
        button.setOnMouseEntered(e -> button.setEffect(shadow)); 
        button.setOnMouseExited(e -> button.setEffect(null)); 
        return button;
    }

    private void handleLogout() {
    	if (primaryStage != null) { 
    		primaryStage.close(); 
    		// Show the login page again
    		LoginPage loginScreen = new LoginPage();
    		Stage loginStage = new Stage();
    		loginScreen.start(loginStage);
    	}
    }    

    private void displayServiceRequests(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: #7BCCAF;"); 

        List<Service> services = getAvailableServices();

        Label serviceLabel = new Label("Select Service:");
        ComboBox<Service> serviceComboBox = new ComboBox<>();
        serviceComboBox.getItems().addAll(services);
        serviceComboBox.setPromptText("Select Service");
        serviceComboBox.setConverter(new StringConverter<Service>() {
            @Override
            public String toString(Service service) {
                return service.getServiceName();
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        Label dateLabel = new Label("Select Date:");
        DatePicker datePicker = new DatePicker();

	    datePicker.setDayCellFactory(picker -> new DateCell() {

            @Override

            public void updateItem(LocalDate date, boolean empty) {

                super.updateItem(date, empty);

                if (date.isBefore(LocalDate.now())) {

                    setDisable(true);

                    setStyle("-fx-background-color: #ffc0cb;"); 

                }

            }

        });
        datePicker.setPromptText("Select Date");

        Label timeLabel = new Label("Select Time:");
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.getItems().addAll(
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM"
        );
        timeComboBox.setPromptText("Select Time");

        Label estimatedDurationLabel = new Label("Estimated Duration:");
        Label estimatedDurationValueLabel = new Label(); 
        Label estimatedCostLabel = new Label("Estimated Cost:");
        Label estimatedCostValueLabel = new Label(); 
        Label serviceDetailsLabel = new Label("Service Details:");
        Label serviceDetailsValueLabel = new Label(); 
        serviceDetailsValueLabel.setWrapText(true); 

        gridPane.add(serviceLabel, 0, 0);
        gridPane.add(serviceComboBox, 1, 0);
        gridPane.add(dateLabel, 0, 1);
        gridPane.add(datePicker, 1, 1);
        gridPane.add(timeLabel, 0, 2);
        gridPane.add(timeComboBox, 1, 2);
        gridPane.add(estimatedDurationLabel, 0, 3);
        gridPane.add(estimatedDurationValueLabel, 1, 3);
        gridPane.add(estimatedCostLabel, 0, 4);
        gridPane.add(estimatedCostValueLabel, 1, 4);
        gridPane.add(serviceDetailsLabel, 0, 5); 
        gridPane.add(serviceDetailsValueLabel, 1, 5);

        serviceComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int durationInMinutes = newValue.getEstimatedDuration();
                int hours = durationInMinutes / 60;
                int minutes = durationInMinutes % 60;
                String formattedDuration = String.format("%d hrs %d mins", hours, minutes);
                double cost = newValue.getCost();
                String formattedCost = String.format("$%.2f", cost); 
                estimatedDurationValueLabel.setText(formattedDuration);
                estimatedCostValueLabel.setText(formattedCost);
                String serviceDescription = newValue.getServiceDescription();  
                serviceDetailsValueLabel.setText(serviceDescription); 


                gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 5);

     
                Label descriptionLabel = new Label("Service details:");
                Label descriptionValueLabel = new Label(serviceDescription);
                gridPane.add(descriptionLabel, 0, 5);
                gridPane.add(descriptionValueLabel, 1, 5);
            }
        });

        // Create book service button
        Button bookServiceButton = new Button("Book Service");
        bookServiceButton.setOnAction(e -> {
            // Get selected service, date, and time
            Service selectedService = serviceComboBox.getValue();
            LocalDate selectedDate = datePicker.getValue();
            String selectedTime = timeComboBox.getValue();

            if (selectedService == null || selectedDate == null || selectedTime == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a service, date, and time.");
            } else {
                // Get customer ID (assuming you have a logged-in customer)
                int customerId = 1; // Replace with actual customer ID

                // Insert appointment data into the database
                if (saveAppointment(customerId, selectedService, selectedDate, selectedTime)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Service booked successfully for " + selectedDate.toString());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to book service. Please try again.");
                }
            }
        });

        GridPane.setHalignment(bookServiceButton, HPos.CENTER);

        gridPane.add(bookServiceButton, 0, 6, 2, 1); 

        borderPane.setCenter(gridPane);
    }
 // Method to display appointment management UI
    private void displayAppointmentManagement(Stage primaryStage) {
        borderPane.setCenter(createAppointmentManagementPane());
    }
    private boolean saveAppointment(int customerId, Service selectedService, LocalDate selectedDate, String selectedTime) {
        // Database connection
        
        PreparedStatement statement = null;

        try {
            // Establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");

            // SQL query to insert appointment data
            String query = "INSERT INTO Appointments (customer_id, service_name, appointment_date, appointment_time, estimated_duration, estimated_cost, appointment_status, customer_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
//            statement.setInt(2, -1); // Set staff_id to -1 to indicate it's not assigned yet
            statement.setString(2, selectedService.getServiceName());
            statement.setDate(3, Date.valueOf(selectedDate));
            LocalTime localTime = LocalTime.parse(selectedTime, DateTimeFormatter.ofPattern("hh:mm a"));
            String formattedTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            statement.setTime(4, Time.valueOf(formattedTime));
            statement.setInt(5, selectedService.getEstimatedDuration());
            statement.setDouble(6, selectedService.getCost());
            statement.setString(7, "Pending"); // Assuming the initial status is "Pending"
            statement.setString(8, username);

            // Execute query
            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to book service.");
            return false;
        } finally {
            // Close the connection and resources
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private VBox createAppointmentManagementPane() {
        VBox appointmentPane = new VBox(20);
        appointmentPane.setAlignment(Pos.CENTER);
        appointmentPane.setPadding(new Insets(20));

        // Add title
        Label titleLabel = new Label("Appointment Management");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.TEAL);
        appointmentPane.getChildren().add(titleLabel);

        // Create appointment table
        TableView<Appointment> appointmentTable = new TableView<>();
        appointmentTable.setPrefWidth(800);
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
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

        // Adding columns to the table
        appointmentTable.getColumns().addAll(appointmentIdCol, customerIdCol, staffCol, serviceCol, dateCol, timeCol, staffIdCol, statusCol);

        // Populate the table with data
        appointments = getAppointmentData();
        appointmentTable.setItems(appointments);

        // Create a cancel button
        Button cancelRequestButton = new Button("Cancel Request");
        cancelRequestButton.setStyle("-fx-background-color: #333; -fx-text-fill: white;");
        cancelRequestButton.setMaxWidth(Double.MAX_VALUE); 
        cancelRequestButton.setCursor(javafx.scene.Cursor.HAND); 

        // Add the button to the pane
        appointmentPane.getChildren().add(cancelRequestButton);

        // Handle cancel button click
        cancelRequestButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            cancelAppointment(selectedAppointment, appointments);
        });

        // Add the appointment table to the center of the borderPane
        appointmentPane.getChildren().add(appointmentTable);
        return appointmentPane;
    }

    private void cancelAppointment(Appointment selectedAppointment, ObservableList<Appointment> appointments) {
        if (selectedAppointment != null) {
            int appointmentId = selectedAppointment.getAppointmentId();
            // Update the status of the appointment to "Cancelled" in the database
            if (updateAppointmentStatus(appointmentId, "Cancelled")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment request cancelled successfully.");
                // Update the status of the selected appointment locally
                selectedAppointment.setStatus("Cancelled");
                appointments.set(appointments.indexOf(selectedAppointment), selectedAppointment);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel appointment request. Please try again.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an appointment to cancel.");
        }
    }

    private boolean updateAppointmentStatus(int appointmentId, String status) {
        PreparedStatement statement = null;
        try {
            // Establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");

            // SQL query to update appointment status
            String query = "UPDATE Appointments SET appointment_status = ? WHERE appointment_id = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, status);
            statement.setInt(2, appointmentId);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update appointment status.");
            return false;
        } finally {
            // Close the resources
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to fetch appointment data from the database
    private ObservableList<Appointment> getAppointmentData() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");
            String query = "SELECT appointment_id, customer_id, staff_name, service_name, appointment_date, appointment_time, staff_id, appointment_status FROM Appointments WHERE appointment_status IN ('Pending', 'Accepted') AND customer_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId); // Set the userId as the customer_id parameter
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");
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

    // Appointment class
    public static class Appointment {
        private final int appointmentId;
        private final int customerId;
        private final String staff;
        private final String service;
        private final String date;
        private final String time;
        private final int staffId;
        private String status;

        public Appointment(int appointmentId, int customerId, String staff, String service, String date, String time, int staffId, String status) {
            this.appointmentId = appointmentId;
            this.customerId = customerId;
            this.staff = staff;
            this.service = service;
            this.date = date;
            this.time = time;
            this.staffId = staffId;
            this.status = status;
        }

        // Getters for appointment properties
        public int getAppointmentId() {
            return appointmentId;
        }

        public int getCustomerId() {
            return customerId;
        }

        public String getStaff() {
            return staff;
        }

        public String getService() {
            return service;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public int getStaffId() {
            return staffId;
        }

        public String getStatus() {
            return status;
        }

		public void setStatus(String status) {
			this.status = status;
		}
        
    }
    // Method to fetch available services from the database
    private List<Service> getAvailableServices() {
        List<Service> services = new ArrayList<>();

        // Database connection
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // Establish connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");

            // SQL query to fetch services
            String query = "SELECT * FROM Services";
            statement = connection.prepareStatement(query);

            // Execute query
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int serviceId = resultSet.getInt("service_id");
                String serviceName = resultSet.getString("service_name");
                String serviceDetails = resultSet.getString("service_details");
                int estimatedDuration = resultSet.getInt("estimated_duration");
                double cost = resultSet.getDouble("cost");
                Service service = new Service(serviceId, serviceName, serviceDetails, estimatedDuration, cost);
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch available services.");
        } finally {
            // Close the connection and resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return services;
    }

    // Service class to represent service details
    private static class Service {
        private int serviceId;
        private String serviceName;
        private String serviceDetails;
        private int estimatedDuration;
        private double cost;

        public Service(int serviceId, String serviceName, String serviceDetails, int estimatedDuration, double cost) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.serviceDetails = serviceDetails;
            this.estimatedDuration = estimatedDuration;
            this.cost = cost;
            this.serviceDetails = serviceDetails;
        }

        public String getServiceDescription() {
			return serviceDetails;
		}

		public String getServiceName() {
            return serviceName;
        }

        public int getEstimatedDuration() {
            return estimatedDuration;
        }

        public double getCost() {
            return cost;
        }

        @Override
        public String toString() {
            return serviceName;
        }
    }
    private void displayServiceHistory(Stage primaryStage) {
    	
        borderPane.setCenter(createAppointmentTable());
    }

    private Node createAppointmentTable() {
        return AppointmentTableUICustomer.createAppointmentTable(userId);
    }

    private void displayProfileManagement() {
        ProfileManagement profileManagement = new ProfileManagement(borderPane, userId);

        // Display the profile management UI
        profileManagement.displayProfileManagement();
    }
    // Method to display an alert
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