package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivitySettingCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Company;

public class SettingCompanyActivity extends AppCompatActivity {

    private ActivitySettingCompanyBinding binding;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private String idUserLogged;
    private Uri selectedImageUrl;
    private StorageReference storageReference;

    private CircleImageView imgLogo;
    private EditText edtName;
    private EditText edtCategory;
    private MaskEditText edtTimeEstimate;
    private CurrencyEditText edtTotalPrice;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        components();

        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        idUserLogged = UserFirebase.getUserId();

        btnSave.setOnClickListener(this::validateCompanyData);

        saveAndRetrieveCompanyData();

        storageReference = firebaseStorage.getReference()
                .child(Constants.IMAGES)
                .child(Constants.COMPANY)
                .child(idUserLogged + Constants.JPG);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUrl = data.getData();// Get the URI of the selected image
                            imgLogo.setImageURI(selectedImageUrl);

                            try {
                                Bitmap bitmap = getBitmapFromUri(selectedImageUrl);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                                byte[] imageInByte = byteArrayOutputStream.toByteArray();

                                UploadTask uploadTask = storageReference.putBytes(imageInByte);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        snackBarMessage("Error uploading image");
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        snackBarMessage("Image uploaded successfully");
                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                                snackBarMessage("Error processing image");
                            }
                        }
                    }
                });

        imgLogo.setOnClickListener(v -> pickImage());
    }

    private void loadImageFromFirebase( ) {
       /* imageCompanyRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .error(R.drawable.ic_broken_image_24)
                    .into(imgLogo); // Load the image into ImageView
        }).addOnFailureListener(e -> {
            Log.e("LoadError", "Error loading image: " + e.getMessage());
            snackBarMessage("Error loading image");
        });*/

        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgLogo.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void saveAndRetrieveCompanyData() {

        DatabaseReference companyRef = databaseReference
                .child( Constants.COMPANY )
                .child(idUserLogged);

        companyRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null){
                    Company company = snapshot.getValue(Company.class);
                    edtName.setText(company.getName());
                    edtCategory.setText(company.getCategory());
                    edtTimeEstimate.setText(company.getTimeEstimate());
                    edtTotalPrice.setText( company.getTotalPrice() );
                    loadImageFromFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               toastMessage("Error retrieving company data");
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        }
    }

    private Bitmap getBitmapFromUri(Uri selectedImageUri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(selectedImageUri);
        return BitmapFactory.decodeStream(inputStream);
    }



    private void validateCompanyData (View view ){

        String name = edtName.getText().toString();
        String category = edtCategory.getText().toString();
        String timeEstimate = edtTimeEstimate.getText().toString();
        String totalPrice = edtTotalPrice.getText().toString();

        if ( !name.isEmpty() ){
            if ( !category.isEmpty() ){
                if ( !timeEstimate.isEmpty() ){
                    if ( !totalPrice.isEmpty() ){
                        //Save com company data
                        Company company = new Company();
                        company.setIdCompany(idUserLogged);
                        company.setName(name);
                        company.setCategory(category);
                        company.setTimeEstimate(timeEstimate);
                        company.setTotalPrice(totalPrice);
                        company.setImageUrlCompany(String.valueOf(selectedImageUrl));
                        company.saveCompanyData();
                        finish();

                    }else {
                        toastMessage("Enter the total price");
                    }
                }else {
                    toastMessage("Enter the delivery time");
                }
            }else {
                toastMessage("Enter the company category");
            }
        }else {
            toastMessage("Enter the company name");
        }
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Set Setting Company");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {

        imgLogo = binding.circleImageViewCompany;
        edtName = binding.editTextCompanyName;
        edtCategory = binding.editTextCompanyType;
        edtTimeEstimate = binding.editTextCompanyDeliveryTime;
        edtTotalPrice = binding.editTextCompanyPrice;
        btnSave = binding.buttonSaveCompany;

        edtTotalPrice.configureViewForLocale(Locale.GERMANY);
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void snackBarMessage(String message){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), message, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_red_devil_100))); //Change to your desired color
        snackbar.show();

    }

    /***
     *
     * ///model
     public Bitmap getProductBitmap() {
     return productBitmap;
     }

     public void setProductBitmap(Bitmap productBitmap) {
     this.productBitmap = productBitmap;
     }
     * ///Adapter
     * // Assuming you have a Bitmap in your Products class
     *     Bitmap productBitmap = product.getProductBitmap(); // Add this method in your Products class
     *     if (productBitmap != null) {
     *         holder.image.setImageBitmap(productBitmap);
     *     } else {
     *         String imageUrl = product.getImageUrlProduct();
     *         Picasso.get()
     *                 .load(imageUrl)
     *                 .placeholder(R.drawable.ic_add_a_photo_84)
     *                 .error(R.drawable.ic_broken_image_24)
     *                 .fit()
     *                 .into(holder.image);
     *     }
     *
     * ///Activity
     * private void loadBitmapFromUri(Uri selectedImageUri, Products product) {
     *     try {
     *         Bitmap bitmap = getBitmapFromUri(selectedImageUri);
     *         product.setProductBitmap(bitmap); // Set the Bitmap to the product
     *     } catch (IOException e) {
     *         e.printStackTrace();
     *         // Handle the error
     *     }
     * }
     *
     *
     */
}