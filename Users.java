package ahmfps;

import java.util.ArrayList;

public class Users {
    private String name;
    private String email;
    private String password;
    private String status; // e.g. "Approved"
    private String userType; // e.g. "Resident"
    private String paymentStatus; // e.g. "Paid", "Unpaid"
    private String room; // This will store the user's room assignment
    private ArrayList<PaymentData> paymentHistory; // Payment history for the user

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public ArrayList<PaymentData> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(ArrayList<PaymentData> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    public Users() {
        paymentHistory = new ArrayList<>();
        room = ""; // Initialize room as empty string
    }

    // Getter for room (assigned room)
    public String getAssignedRoom() {
        return room;
    }

    // Setter for room (assigned room)
    public void setAssignedRoom(String room) {
        this.room = room;
    }

    // Method to add a payment to the user's payment history
    public void addPayment(PaymentData payment) {
        paymentHistory.add(payment);
    }
}
