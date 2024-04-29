package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PerformanceMetricsA {
    private BorderPane borderPane;
    private Connection connection;
    public int userId; // New field to store the userId


    public PerformanceMetricsA(BorderPane borderPane) {
        this.borderPane = borderPane;

        // Initialize database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/serviceMS", "root", "Nikhil@2003");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }

    public void displayPerformanceMetrics() {
        // Create container for performance metrics UI
        VBox performanceMetricsPane = new VBox(20);
        performanceMetricsPane.setAlignment(Pos.CENTER);
        performanceMetricsPane.setPadding(new Insets(20));

        // Add title
        Label titleLabel = new Label("Performance Metrics");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.TEAL);
        performanceMetricsPane.getChildren().add(titleLabel);

        ObservableList<String> metricsData = getPerformanceMetricsData();
        for (String data : metricsData) {
            Label metricLabel = new Label(data);
            metricLabel.setFont(Font.font("Arial", 14));
            performanceMetricsPane.getChildren().add(metricLabel);
        }

        borderPane.setCenter(performanceMetricsPane);
    }

    private ObservableList<String> getPerformanceMetricsData() {
        ObservableList<String> metricsData = FXCollections.observableArrayList();
        try {
            // Execute SQL query to fetch performance metrics data
            // Replace this with your actual SQL query to fetch performance metrics data from the database
        	String query = "SELECT COUNT(*) AS completed_requests, " +
                    "(SELECT COUNT(*) FROM Appointments WHERE appointment_status = 'Cancelled') AS cancelled_requests " +
                    "FROM Appointments WHERE appointment_status = 'Completed'";
        	PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int completedRequests = resultSet.getInt("completed_requests");
                int cancelledRequests = resultSet.getInt("cancelled_requests");
                metricsData.add("Completed Requests: " + completedRequests);
                metricsData.add("Cancelled Requests: " + cancelledRequests);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch performance metrics data: " + e.getMessage());
        }
        return metricsData;
    }

    // Method to display an alert
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
