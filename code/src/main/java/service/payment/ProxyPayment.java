package service.payment;

public class ProxyPayment implements PaymentAdapter {

    private PaymentAdapter real = null;

    //TODO: need to add a list of reals and check when to change to each one
    public void setRealPayment(PaymentAdapter paymentAdapter){
        if(real == null)
            real = paymentAdapter;
    }

    @Override
    public void makePurchase(String accountNumber , int amount) {
        if (real!=null){
            real.makePurchase(accountNumber,amount);
            System.out.println("real transaction was made");
        }
        else{
            System.out.println("proxy made fake transaction");
        }
    }
}
