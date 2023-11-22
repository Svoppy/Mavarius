import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Menu {
    private Map<Integer, MenuItem> menuItems;
    private int nextMenuItemNumber;

    public Menu() {
        menuItems = new HashMap<>();
        nextMenuItemNumber = 1;

        // Load menu items from the database
        loadMenuFromDatabase();
    }


    private void loadMenuFromDatabase() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String selectAllMenuItemsSQL = "SELECT * FROM menu_items";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectAllMenuItemsSQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");

                    MenuItem item = new MenuItem(name, price);
                    menuItems.put(id, item);

                    // Update nextMenuItemNumber to avoid conflicts with existing menu items
                    nextMenuItemNumber = Math.max(nextMenuItemNumber, id + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading menu from the database.");
        }
    }

    public void displayMenu() {
        System.out.println("Menu:");
        for (int number : menuItems.keySet()) {
            MenuItem item = menuItems.get(number);
            System.out.println(number + ". " + item.getName() + " - $" + item.getPrice());
        }
    }

    public MenuItem getItem(int number) {
        return menuItems.get(number);
    }

    public void addMenuItem(String name, double price) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String insertMenuItemSQL = "INSERT INTO menu_items (name, price) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertMenuItemSQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, name);
                preparedStatement.setDouble(2, price);
                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    MenuItem newItem = new MenuItem(name, price);
                    menuItems.put(id, newItem);
                    System.out.println("Item added to the menu!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding menu item to the database.");
        }
    }

    public void removeMenuItem(int number) {
        MenuItem removedItem = menuItems.remove(number);

        if (removedItem != null) {
            String jdbcUrl = "jdbc:mysql://localhost:3306/mysql";
            String user = "root";
            String password = "root";

            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                String deleteMenuItemSQL = "DELETE FROM menu_items WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteMenuItemSQL)) {
                    preparedStatement.setInt(1, number);
                    preparedStatement.executeUpdate();
                    System.out.println("Item removed from the menu!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error removing menu item from the database.");
            }
        }
    }
}
