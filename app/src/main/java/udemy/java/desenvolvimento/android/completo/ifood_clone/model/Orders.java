package udemy.java.desenvolvimento.android.completo.ifood_clone.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class Orders implements Serializable {

    private String orderId;
    private String userId;
    private String companyId;
    private String customerName;
    private String phoneNumber;
    private String address;
    private List<ItemOrders> itemOrders;
    private Double totalValue;
    private String orderStatus;
    private int paymentMethod;
    private String observation;

    public Orders() {

    }

    public Orders ( String userId, String companyId) {
        setCompanyId(companyId);
        setUserId(userId);

        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference ordersRef = databaseReference
                .child(Constants.CUSTOMERS_ORDERS)
                .child(companyId)
                .child(userId);
        setOrderId(ordersRef.push().getKey());
    }


    public void saveCustomerOrder() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference ordersRef = databaseReference
                .child(Constants.CUSTOMERS_ORDERS)
                .child(getCompanyId())
                .child(getUserId());
       ordersRef.setValue(this);

    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ItemOrders> getItemOrders() {
        return itemOrders;
    }

    public void setItemOrders(List<ItemOrders> itemOrders) {
        this.itemOrders = itemOrders;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
