public class StandardTable implements Table {
    private static int idCounter = 1; // Счетчик для уникальных идентификаторов
    private int id;

    public StandardTable() {
        this.id = idCounter++;
    }

    @Override
    public void reserve() {
        System.out.println("Standard table reserved.");
    }

    @Override
    public int getId() {
        return id;
    }
}