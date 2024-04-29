package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;

public class AppointmentTableUI {

    public static VBox createAppointmentTable() {
        // Create a VBox to hold the label and table
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER); 
        container.setSpacing(10); 
        container.setPadding(new Insets(20)); 

        // Create the label for Service History
        Label titleLabel = new Label("Service History");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#336699")); 



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

        // Add columns to table
        appointmentTable.getColumns().addAll(appointmentIdCol, customerIdCol, staffCol, serviceCol, dateCol, timeCol, staffIdCol, statusCol);

        // Fetch appointment data and add it to the table
        appointmentTable.setItems(FXCollections.observableArrayList(getAppointmentData()));

        // Add the label and table to the container
        container.getChildren().addAll(titleLabel, appointmentTable);

        return container;
    }

    // Method to fetch appointment data from the database
    public static ObservableList<Appointment> getAppointmentData() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");

            String query = "SELECT appointment_id, customer_id, staff_name, service_name, appointment_date, appointment_time, staff_id, appointment_status FROM Appointments";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                int customerId = resultSet.getInt("customer_id");
                String staff = resultSet.getString("staff_name");
                String service = resultSet.getString("service_name");
                String date = resultSet.getString("appointment_date");
                String time = resultSet.getString("appointment_time");
                int staffId = resultSet.getInt("staff_id");
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

    // Method to display an alert
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
