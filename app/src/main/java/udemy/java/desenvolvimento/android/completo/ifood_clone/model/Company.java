package udemy.java.desenvolvimento.android.completo.ifood_clone.model;

import static udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants.COMPANY;

import com.google.firebase.database.DatabaseReference;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class Company {

    private String idCompany;

    private String imageUrlCompany;
    private String name;
    private String category;
    private String timeEstimate;
    private String  totalPrice;

    public Company() {}

    public void saveCompanyData(){
        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference companyRef = firebaseDatabase.child(COMPANY)
                .child(idCompany);
        companyRef.setValue(this);
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        this.idCompany = idCompany;
    }

    public String getImageUrlCompany() {
        return imageUrlCompany;
    }

    public void


    setImageUrlCompany(String imageUrlCompany) {
        this.imageUrlCompany = imageUrlCompany;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(String timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public String  getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
