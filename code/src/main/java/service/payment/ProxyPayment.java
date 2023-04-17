package service.payment;

public class ProxyPayment implements PaymentAdapter {

    private PaymentAdapter real = null;

    //TODO: need to add a list of reals and check when to change to each one
    public void setRealPayment(PaymentAdapter paymentAdapter){
        if(real == null)
            real = paymentAdapter;
    }

    public void pay(){}
}
