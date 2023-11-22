import java.time.LocalDateTime;

public abstract class StandardTableReservationStrategy implements TableReservationStrategy {
    @Override
    public boolean reserveTable(Table table, LocalDateTime dateTime, int durationInHours) {
        // Логика бронирования стандартного стола
        return false;
    }
}

