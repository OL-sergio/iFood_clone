package udemy.java.desenvolvimento.android.completo.ifood_clone.model;

import static udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants.COMPANY;
import static udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants.CUSTOMERS;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class Customer {
    private String idCustomer;
    private String customerImageUrl;
    private String name;
    private String address;

    public Customer() {
    }


    public void saveCustomerData() {
        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference customersRef = firebaseDatabase.child(CUSTOMERS)
                .child(getIdCustomer());
        customersRef.setValue(this);
    }

    public void updateUserCustomer() {
        DatabaseReference firebaseReference = FirebaseConfiguration.getFirebaseDatabase();
       /* DatabaseReference usersReference = firebaseReference
                .child("users")
                .child(getUID());*/

        Map<String, Object> updateUsersChild = new HashMap<>();
        updateUsersChild.put("/" + Constants.USERS + "/" + getIdCustomer() + "/name", getName() );
        updateUsersChild.put("/" + Constants.USERS + "/" + getIdCustomer() + "/companyImageUrl", getCustomerImageUrl() );
        firebaseReference.updateChildren( updateUsersChild ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("INFO", "User data updated successfully");
            }else {
                Log.d("INFO", "Error updating user data");
            }
        });

    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getCustomerImageUrl() {
        return customerImageUrl;
    }

    public void setCustomerImageUrl(String customerImageUrl) {
        this.customerImageUrl = customerImageUrl;
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


}
