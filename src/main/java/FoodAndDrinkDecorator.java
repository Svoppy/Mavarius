public abstract class FoodAndDrinkDecorator implements ReservationDecorator {
    private Table table;

    public FoodAndDrinkDecorator(Table table) {
        this.table = table;
    }

    @Override
    public void reserve() {
        table.reserve();
        decorate();
    }

    @Override
    public void decorate() {
        // Logic for adding food and drink orders to the reservation
        System.out.println("Food and drinks ordered.");
    }
}
