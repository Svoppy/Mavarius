    import java.time.LocalDateTime;


    public abstract class VipTableReservationStrategy implements TableReservationStrategy {
        @Override
        public boolean reserveTable(Table table, LocalDateTime dateTime, int durationInHours) {
            // Логика бронирования VIP стола
            return false;
        }
    }