import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationManager {
    private static ReservationManager instance;
    private TableReservationStrategy tableReservationStrategy;

    private Map<Table, Reservation> reservationsMap = new HashMap<>();
    private List<Table> availableTables = new ArrayList<>();

    private ReservationManager() {
        // Инициализация доступных столов
        availableTables.add(new StandardTable());
        availableTables.add(new VipTable());
        // Инициализация таблицы доступности столов из базы данных
    }

    public static ReservationManager getInstance() {
        if (instance == null) {
            instance = new ReservationManager();
        }
        return instance;
    }
    public void setTableReservationStrategy(TableReservationStrategy tableReservationStrategy) {
        this.tableReservationStrategy = tableReservationStrategy;
    }

    public boolean reserveTable(Table table, LocalDateTime dateTime, int durationInHours) {
        if (tableReservationStrategy != null) {
            return tableReservationStrategy.reserveTable(table, dateTime, durationInHours);
        }
        return false;
    }

// Проверка доступности стола в определенное время
    public boolean isTableAvailable(Table table, LocalDateTime dateTime, int durationInHours) {

        // Проверка доступности стола в базе данных
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String selectAvailabilitySQL = "SELECT is_available FROM table_availability WHERE table_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectAvailabilitySQL)) {
                preparedStatement.setInt(1, table.getId());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean isAvailable = resultSet.getBoolean("is_available");
                        return isAvailable;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking table availability from the database.");
        }

        return false; // В случае ошибки, считаем, что стол недоступен
    }



    // Добавление бронирования с проверкой доступности стола
    public void addReservation(Table table, Reservation reservation) {
        if (isTableAvailable(table, reservation.getDateTime(), reservation.getDurationInHours())) {
            reservationsMap.put(table, reservation);

            // Обновление таблицы доступности столов в базе данных
            updateTableAvailability(table, true);

            System.out.println("Reservation for " + reservation.getCustomerName() + " made successfully!");

            // Оповестить наблюдателей о новой резервации
            reservation.notifyObservers("New reservation made: " + reservation.getCustomerName());
        } else {
            System.out.println("Table is not available for the selected date and time.");
        }
        Customer customer = new Customer("John");
        reservation.addReservationObserver(customer);

        RestaurantStaff staff = new RestaurantStaff("Waiter");
        reservation.addReservationObserver(staff);
    }

    // Обновление таблицы доступности столов в базе данных
    private void updateTableAvailability(Table table, boolean isAvailable) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            // Проверяем, есть ли запись для данного стола в таблице
            String checkAvailabilitySQL = "SELECT * FROM table_availability WHERE table_id = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkAvailabilitySQL)) {
                checkStatement.setInt(1, table.getId());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    boolean exists = resultSet.next();
                    if (!exists) {
                        // Если запись не существует, создаем новую
                        String insertAvailabilitySQL = "INSERT INTO table_availability (table_id, is_available) VALUES (?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertAvailabilitySQL)) {
                            insertStatement.setInt(1, table.getId());
                            insertStatement.setBoolean(2, isAvailable);
                            insertStatement.executeUpdate();
                        }
                        return; // Выходим, так как обновление не требуется
                    }
                }
            }

            // Если запись существует, обновляем
            String updateAvailabilitySQL = "UPDATE table_availability SET is_available = ? WHERE table_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateAvailabilitySQL)) {
                preparedStatement.setBoolean(1, isAvailable);
                preparedStatement.setInt(2, table.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating table availability in the database. Error message: " + e.getMessage());
        }
    }






































































    TeaHouseCLI teaHouseCLI = TeaHouseCLI.getInstance();}