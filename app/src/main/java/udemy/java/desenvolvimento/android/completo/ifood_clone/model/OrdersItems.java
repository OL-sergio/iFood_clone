package udemy.java.desenvolvimento.android.completo.ifood_clone.model;

import java.io.Serializable;

public class OrdersItems implements Serializable {

    private String idProduct;
    private String nameProduct;
    private int quantity;
    private String price;

    public OrdersItems() {
    }
    public OrdersItems( String idProduct, String nameProduct, int quantity, String price) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
        this.price = price;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
