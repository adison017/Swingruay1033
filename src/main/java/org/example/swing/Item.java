package org.example.swing;

class Item {
    private int id;
    private String product_Name;
    private int quantity;
    private double price;
    private int order_Id;
    private String ordernumber;

    public int getOrder_Id() {
        return order_Id;
    }

    public void setOrder_Id(int order_Id) {
        this.order_Id = order_Id;
    }

    public String  getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProduct_Name() {
        return product_Name; }
    public void setProduct_Name(String productName) { this.product_Name = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }



}

