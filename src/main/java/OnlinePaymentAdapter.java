public class OnlinePaymentAdapter implements PaymentAdapter {
    public void pay(double amount) {

        System.out.println("Online payment of " + amount + " processed.");
    }
}
