package ahmfps;

import java.util.Date;

public class PaymentData {
    private Date date;
    private double amount;
    private int durationInMonths; 

    public PaymentData(Date date, double amount, int durationInMonths) {
        this.date = date;
        this.amount = amount;
        this.durationInMonths = durationInMonths;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
    
    public int getDurationInMonths() {
        return durationInMonths;
    }
}

