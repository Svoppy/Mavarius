public class TableFactory {
    public Table createTable(String type) {
        if (type.equals("standard")) {
            return new StandardTable();
        } else if (type.equals("vip")) {
            return new VipTable();
        }
        return null;
    }
}
