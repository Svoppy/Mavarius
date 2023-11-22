import java.time.LocalDateTime;

public interface TableReservationStrategy {
    boolean reserveTable(Table table, LocalDateTime dateTime, int durationInHours);
}
