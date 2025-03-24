package udemy.java.desenvolvimento.android.completo.ifood_clone.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class History {
    private String idHistory;
    private String idOrder;
    private String name;
    private String address;
    private String phoneNumber;
    private String totalPrice;
    private String totalQuantity;
    private String paymentMethod;
    private List<OrdersItems> ordersItems;

    public History() {

    }

    public History(String name, String totalValue, String address, String phoneNumber, String totalQuantity, String paymentMethod, List<OrdersItems> ordersItems) {

        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.totalQuantity = totalQuantity;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalValue;
        this.ordersItems = ordersItems;

    }

    public void saveCustomerHistoryOrders( String userId, String getCompany , String idOrder) {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference ordersRef = databaseReference
                .child(Constants.CUSTOMERS_HISTORY)
                .child(userId)
                .child(getCompany)
                .child(idOrder);
        ordersRef.setValue(this);
    }


    public String getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(String idHistory) {
        this.idHistory = idHistory;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrdersItems> getOrdersItems() {
        return ordersItems;
    }

    public void setOrdersItems(List<OrdersItems> ordersItems) {
        this.ordersItems = ordersItems;
    }
}
