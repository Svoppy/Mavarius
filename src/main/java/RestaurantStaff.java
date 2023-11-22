public class RestaurantStaff implements ReservationObserver {
    private String role;

    public RestaurantStaff(String role) {
        this.role = role;
    }

    @Override
    public void update(String message) {
        System.out.println("Staff (" + role + ") received a notification: " + message);
    }
}