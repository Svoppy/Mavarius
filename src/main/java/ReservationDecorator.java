
public interface ReservationDecorator extends Table {
    void decorate();

    void decorate(Reservation reservation);
}

