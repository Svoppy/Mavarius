public class Customer implements ReservationObserver {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received a notification: " + message);
    }
}
