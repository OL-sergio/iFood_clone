package udemy.java.desenvolvimento.android.completo.ifood_clone.model;

import static udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants.COMPANY;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class Companies implements Serializable {

    private String idCompany;
    private String companyImageUrl;
    private String name;
    private String filterName;
    private String category;
    private String estimatedTime;
    private String  totalPrice;

    public Companies() {}

    public void saveCompanyData(){
        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference companyRef = firebaseDatabase.child(COMPANY)
                .child(getIdCompany());
        companyRef.setValue(this);
    }
    public void updateUserCompany(){

        DatabaseReference firebaseReference = FirebaseConfiguration.getFirebaseDatabase();
       /* DatabaseReference usersReference = firebaseReference
                .child("users")
                .child(getUID());*/


        Map<String, Object> updateUsersChild = new HashMap<>();
        updateUsersChild.put("/" + Constants.USERS + "/" + getIdCompany() + "/name", getName() );
        updateUsersChild.put("/" + Constants.USERS + "/" + getIdCompany() + "/companyImageUrl", getCompanyImageUrl() );
        firebaseReference.updateChildren( updateUsersChild ).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("INFO", "User data updated successfully");
            }else {
                Log.d("INFO", "Error updating user data");
            }
        });


    }


    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }

    public String getCompanyImageUrl() {
        return companyImageUrl;
    }

    public void setCompanyImageUrl(String companyImageUrl) {
        this.companyImageUrl = companyImageUrl;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName.toLowerCase();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String  getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
