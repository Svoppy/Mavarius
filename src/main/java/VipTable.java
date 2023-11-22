public class VipTable implements Table {
    private static int idCounter = 1; // Счетчик для уникальных идентификаторов
    private int id;

    public VipTable() {
        this.id = idCounter++;
    }

    @Override
    public void reserve() {
        System.out.println("VIP table reserved.");
    }

    @Override
    public int getId() {
        return id;
    }
}