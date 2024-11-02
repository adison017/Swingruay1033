package org.example.swing;

public class Order {
    private int id; // Primary key
    private String orderNumber;
    private String username;
    private String status;
    private double totalAmount;
    private String DateTime;

    public Order() {
    }

    // Constructor
    public Order(int id, String orderNumber, String username, String status, double totalAmount,String DateTime) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.username = username;
        this.status = status;
        this.totalAmount = totalAmount;
        this.DateTime = DateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String DateTime) {
        this.DateTime = Order.this.DateTime;
    }
}
