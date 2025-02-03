package udemy.java.desenvolvimento.android.completo.ifood_clone.model;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;

public class Products {
    private String idProduct;
    private String idUser;
    private String imageUrlProduct;
    private String productName;
    private String productCategory;
    private String productPrice;

    public Products() {

        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference productsRef = firebaseDatabase.child(Constants.PRODUCTS);
            setIdProduct(productsRef.push().getKey());
    }

    public void saveProductData(){
        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference productsRef = firebaseDatabase.child(Constants.PRODUCTS)
                .child(getIdUser())
                .child(getIdProduct());
        productsRef.setValue(this);
    }
    public void remove(){
        DatabaseReference firebaseDatabase = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference productsRef = firebaseDatabase.child(Constants.PRODUCTS)
                .child(getIdUser())
                .child(getIdProduct());
        productsRef.removeValue();
    }

    public void deleteImageFromDataBase(String idProduct ){

        StorageReference storageReference = FirebaseConfiguration.getFirebaseStorage()
               .child(Constants.IMAGES)
               .child(Constants.PRODUCTS)
               .child(  idProduct + Constants.JPG );
        storageReference.delete();

    }

    public String sanitizeId(String id) {
        return id.replace(".", "") // Replace '.' with '_'
                .replace("#", "")  // Replace '#' with '_'
                .replace("$", "")  // Replace '$' with '_'
                .replace("[", "")  // Replace '[' with '_'
                .replace("]", ""); // Replace ']' with '_'
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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
