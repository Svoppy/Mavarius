import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Reservation {
    private Table table;
    private List<ReservationObserver> observers = new ArrayList<>();
    private List<ReservationDecorator> decorators = new ArrayList<>();


    private int durationInHours;
    private List<String> additionalServices;
    private LocalDateTime dateTime;
    private String customerName;


    void notifyObservers(String message) {
        for (ReservationObserver observer : observers) {
            observer.update(message);
        }
    }
    public void addDecorator(ReservationDecorator decorator) {
        decorators.add(decorator);
    }
    public Reservation(Table table, int durationInHours, List<String> additionalServices, LocalDateTime dateTime, String customerName) {
        this.table = table;
        this.durationInHours = durationInHours;
        this.additionalServices = additionalServices;
        this.dateTime = dateTime;
        this.customerName = customerName;
    }


    public void reserve() {
        // Применяем все декораторы
        for (ReservationDecorator decorator : decorators) {
            decorator.decorate(this);
        }

        // Оповещаем наблюдателей
        notifyObservers("Reservation made: " + customerName);

        // ... остальной код
    }
    public Table getTable() {
        return table;
    }

    public int getDurationInHours() {
        return durationInHours;
    }

    public List<String> getAdditionalServices() {
        return additionalServices;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void addReservationObserver(ReservationObserver observer) {
        observers.add(observer);
    }


}