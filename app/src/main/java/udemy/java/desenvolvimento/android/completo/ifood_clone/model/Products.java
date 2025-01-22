package udemy.java.desenvolvimento.android.completo.ifood_clone.model;


import com.google.firebase.database.DatabaseReference;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class Products {
    private String idProduct;
    private String imageUrlProduct;
    private String productName;
    private String productCategory;
    private String  productPrice;

    public Products() {
    }

    public void saveProductData(){
        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference productsRef = firebaseDatabase.child(Constants.PRODUCTS)
                .child(idProduct)
                .push();
        productsRef.setValue(this);
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getImageUrlProduct() {
        return imageUrlProduct;
    }

    public void setImageUrlProduct(String imageUrlProduct) {
        this.imageUrlProduct = imageUrlProduct;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
