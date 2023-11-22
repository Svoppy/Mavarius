import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TeaHouseCLI {
    private static TeaHouseCLI instance;
    public static TeaHouseCLI getInstance() {
        if (instance == null) {
            instance = new TeaHouseCLI();
        }
        return instance;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Are you an administrator or a visitor? (admin/visitor):");
        String userType = scanner.nextLine();

        if (userType.equalsIgnoreCase("admin")) {
            adminMenu(scanner);
        } else if (userType.equalsIgnoreCase("visitor")) {
            visitorMenu(scanner);
        } else {
            System.out.println("Invalid user type. Exiting...");
        }
    }


    private static void adminMenu(Scanner scanner) {
        System.out.println("Welcome, Administrator!");

        while (true) {
            System.out.println("Choose an action:");
            System.out.println("1. Modify menu");
            System.out.println("2. View all reservations");
            System.out.println("3. Exit");

            int adminChoice = scanner.nextInt();
            scanner.nextLine(); // Read the newline character after the number

            switch (adminChoice) {
                case 1:
                    modifyMenu(scanner);
                    break;
                case 2:
                    viewAllReservations();
                    break;
                case 3:
                    System.out.println("Exiting administrator menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void modifyMenu(Scanner scanner) {
        Menu menu = new Menu();
        menu.displayMenu();

        System.out.println("Choose an action:");
        System.out.println("1. Add item to the menu");
        System.out.println("2. Remove item from the menu");
        System.out.println("3. Exit");

        int modifyChoice = scanner.nextInt();
        scanner.nextLine(); // Read the newline character after the number

        switch (modifyChoice) {
            case 1:
                System.out.println("Enter the name of the new item:");
                String itemName = scanner.nextLine();

                System.out.println("Enter the price of the new item:");
                double itemPrice = scanner.nextDouble();

                menu.addMenuItem(itemName, itemPrice);
                System.out.println("Item added to the menu!");
                break;
            case 2:
                System.out.println("Enter the number of the item to remove:");
                int itemNumber = scanner.nextInt();
                menu.removeMenuItem(itemNumber);
                break;
            case 3:
                System.out.println("Exiting modify menu.");
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
        }
    }

    private static void visitorMenu(Scanner scanner) {
        System.out.println("Welcome to Mavar Tea House Reservation System!");
        System.out.println("Choose an option:");
        System.out.println("1. Reserve a table");
        System.out.println("2. Order from the menu");

        int option = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (option == 1) {
            reserveTable(scanner);
        } else if (option == 2) {
            orderFromMenu(scanner);
        } else {
            System.out.println("Invalid option. Exiting...");
        }
    }

    private static void reserveTable(Scanner scanner) {
        System.out.println("Please select the table type (standard/vip):");
        String tableType = scanner.nextLine();
        TableFactory tableFactory = new TableFactory();
        Table table = tableFactory.createTable(tableType);

        if (table != null) {
            System.out.println("Enter the date and time of your reservation (YYYY-MM-DD HH:mm):");
            String dateTimeString = scanner.nextLine();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            System.out.println("Enter the duration of your reservation in hours:");
            int durationInHours = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check table availability
            ReservationManager reservationManager = ReservationManager.getInstance();
            if (reservationManager.isTableAvailable(table, dateTime, durationInHours)) {
                // Table is available, proceed with the reservation
                Reservation reservation = new Reservation(table, durationInHours, null, dateTime, "Visitor");
                reservationManager.addReservation(table, reservation);
                System.out.println("Table reserved successfully!");
            } else {
                System.out.println("Sorry, the selected table is not available at the specified time.");
            }
        } else {
            System.out.println("Sorry, tables of the selected type are not available.");
        }
    }

    private static void orderFromMenu(Scanner scanner) {
        // Create a menu
        System.out.println("Please select the table type (standard/vip):");
        String tableType = scanner.nextLine();
        TableFactory tableFactory = new TableFactory();
        Table table = tableFactory.createTable(tableType);

        if (table != null) {
            ReservationManager reservationManager = ReservationManager.getInstance();
            Reservation reservation = new Reservation(table, 0, null, null, "Visitor");
            reservationManager.addReservation(table, reservation);
            System.out.println("Table reserved successfully!");
        } else {
            System.out.println("Invalid table type. Exiting reservation...");
        }
        Menu menu = new Menu();
        menu.displayMenu();

        System.out.println("Enter the numbers of items to order (comma-separated):");
        String orderInput = scanner.nextLine();
        String[] orderItems = orderInput.split(",");

        Map<Integer, Integer> orders = new HashMap<>();
        for (String orderItem : orderItems) {
            int itemNumber = Integer.parseInt(orderItem.trim());
            MenuItem menuItem = menu.getItem(itemNumber);
            if (menuItem != null) {
                int quantity = orders.getOrDefault(itemNumber, 0) + 1;
                orders.put(itemNumber, quantity);
            }
        }

        // Calculate the total amount
        double totalAmount = 0;
        for (Map.Entry<Integer, Integer> order : orders.entrySet()) {
            int itemNumber = order.getKey();
            int quantity = order.getValue();
            MenuItem menuItem = menu.getItem(itemNumber);
            if (menuItem != null) {
                totalAmount += menuItem.getPrice() * quantity;
            }
        }

        System.out.println("Total amount: $" + totalAmount);

        // Select payment method
        System.out.println("Select payment method (online/cash):");
        String paymentMethod = scanner.nextLine();

        PaymentAdapter paymentAdapter;
        if (paymentMethod.equalsIgnoreCase("online")) {
            paymentAdapter = new OnlinePaymentAdapter();
        } else {
            paymentAdapter = null;
        }

        if (paymentAdapter != null) {
            // Process payment
            paymentAdapter.pay(totalAmount);
            System.out.println("Payment successful!");
        } else {
            System.out.println("Cash payment is not supported.");
        }
        saveReservationDetails(table, totalAmount);

        System.out.println("Thank you for your order!");
    }

    private static void saveReservationDetails(Table table, double totalAmount) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String insertReservationSQL = "INSERT INTO reservations (table_type, total_amount) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertReservationSQL)) {
                preparedStatement.setString(1, table.getClass().getSimpleName());  // Use class name as table type
                preparedStatement.setDouble(2, totalAmount);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving reservation details to the database.");
        }
    }

    private static void viewAllReservations() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String selectAllReservationsSQL = "SELECT * FROM reservations";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectAllReservationsSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("All Reservations:");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String tableType = resultSet.getString("table_type");
                    double totalAmount = resultSet.getDouble("total_amount");
                    Timestamp reservationDate = resultSet.getTimestamp("reservation_date");

                    System.out.println("ID: " + id + ", Table Type: " + tableType +
                            ", Total Amount: $" + totalAmount + ", Reservation Date: " + reservationDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error viewing reservations from the database.");
        }
    }
}
