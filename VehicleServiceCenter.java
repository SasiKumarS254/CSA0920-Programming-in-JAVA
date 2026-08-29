import java.sql.*;
import java.util.Scanner;

public class VehicleServiceCenter {

    static final String URL = "jdbc:mysql://localhost:3306/vehicle_service_db";
    static final String USER = "root";
    static final String PASSWORD = "123456";

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.println("======================================");
            System.out.println(" VEHICLE SERVICE CENTER MANAGEMENT");
            System.out.println("======================================");
            System.out.println("Database connected successfully.");

            int choice;

            do {
                System.out.println("\n1. Book Service");
                System.out.println("2. Update Job Status");
                System.out.println("3. Generate Bill");
                System.out.println("4. Search Service History");
                System.out.println("5. Pending Services Report");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        bookService(connection);
                        break;
                    case 2:
                        updateJobStatus(connection);
                        break;
                    case 3:
                        generateBill(connection);
                        break;
                    case 4:
                        searchServiceHistory(connection);
                        break;
                    case 5:
                        pendingServicesReport(connection);
                        break;
                    case 6:
                        System.out.println("Thank you.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }

            } while (choice != 6);

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }

        scanner.close();
    }

    static void bookService(Connection connection) {
        try {
            System.out.print("Enter vehicle number: ");
            String vehicleNo = scanner.nextLine();

            System.out.print("Enter customer name: ");
            String customerName = scanner.nextLine();

            System.out.print("Enter service type: ");
            String serviceType = scanner.nextLine();

            System.out.print("Enter service cost: ");
            double cost = scanner.nextDouble();
            scanner.nextLine();

            String query = "INSERT INTO service_bookings " +
                    "(vehicle_no, customer_name, service_type, status, cost) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, vehicleNo);
            statement.setString(2, customerName);
            statement.setString(3, serviceType);
            statement.setString(4, "Booked");
            statement.setDouble(5, cost);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Service booked successfully.");
            }

            statement.close();

        } catch (SQLException e) {
            System.out.println("Error while booking service: " + e.getMessage());
        }
    }

    static void updateJobStatus(Connection connection) {
        try {
            System.out.print("Enter booking ID: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("1. Booked");
            System.out.println("2. In Progress");
            System.out.println("3. Completed");
            System.out.print("Select status: ");

            int statusChoice = scanner.nextInt();
            scanner.nextLine();

            String status;

            if (statusChoice == 1) {
                status = "Booked";
            } else if (statusChoice == 2) {
                status = "In Progress";
            } else if (statusChoice == 3) {
                status = "Completed";
            } else {
                System.out.println("Invalid status.");
                return;
            }

            String query = "UPDATE service_bookings SET status = ? WHERE booking_id = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, status);
            statement.setInt(2, bookingId);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Job status updated to " + status);
            } else {
                System.out.println("Booking ID not found.");
            }

            statement.close();

        } catch (SQLException e) {
            System.out.println("Error while updating status: " + e.getMessage());
        }
    }

    static void generateBill(Connection connection) {
        try {
            System.out.print("Enter booking ID: ");
            int bookingId = scanner.nextInt();
            scanner.nextLine();

            String query = "SELECT * FROM service_bookings WHERE booking_id = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, bookingId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                String status = resultSet.getString("status");

                if (!status.equalsIgnoreCase("Completed")) {
                    System.out.println("Bill can be generated only after service completion.");
                } else {
                    System.out.println("\n---------- SERVICE BILL ----------");
                    System.out.println("Booking ID: " + resultSet.getInt("booking_id"));
                    System.out.println("Vehicle No: " + resultSet.getString("vehicle_no"));
                    System.out.println("Customer: " + resultSet.getString("customer_name"));
                    System.out.println("Service: " + resultSet.getString("service_type"));
                    System.out.println("Status: " + status);
                    System.out.println("Total Cost: Rs. " + resultSet.getDouble("cost"));
                    System.out.println("----------------------------------");
                }

            } else {
                System.out.println("Booking ID not found.");
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error while generating bill: " + e.getMessage());
        }
    }

    static void searchServiceHistory(Connection connection) {
        try {
            System.out.print("Enter vehicle number: ");
            String vehicleNo = scanner.nextLine();

            String query = "SELECT * FROM service_bookings WHERE vehicle_no = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, vehicleNo);

            ResultSet resultSet = statement.executeQuery();

            boolean found = false;

            System.out.println("\n---------- SERVICE HISTORY ----------");

            while (resultSet.next()) {
                found = true;

                System.out.println("Booking ID: " + resultSet.getInt("booking_id"));
                System.out.println("Customer: " + resultSet.getString("customer_name"));
                System.out.println("Service: " + resultSet.getString("service_type"));
                System.out.println("Status: " + resultSet.getString("status"));
                System.out.println("Cost: Rs. " + resultSet.getDouble("cost"));
                System.out.println();
            }

            if (!found) {
                throw new SQLException("Invalid or unregistered vehicle number.");
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }

    static void pendingServicesReport(Connection connection) {
        try {
            String query = "SELECT * FROM service_bookings WHERE status != ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "Completed");

            ResultSet resultSet = statement.executeQuery();

            boolean found = false;

            System.out.println("\n------- PENDING SERVICES REPORT -------");

            while (resultSet.next()) {
                found = true;

                System.out.println(
                        "Booking ID: " + resultSet.getInt("booking_id") +
                        " | Vehicle: " + resultSet.getString("vehicle_no") +
                        " | Status: " + resultSet.getString("status")
                );
            }

            if (!found) {
                System.out.println("No pending services.");
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.out.println("Error while generating report: " + e.getMessage());
        }
    }
}