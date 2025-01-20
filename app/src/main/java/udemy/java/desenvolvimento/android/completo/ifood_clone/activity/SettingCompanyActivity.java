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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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
    private FirebaseStorage storageReference;
    private DatabaseReference databaseReference;
    private String idUserLogged;
    private Uri selectedImageUrl;

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

        storageReference = FirebaseConfiguration.getFirebaseStorage().getStorage();
        idUserLogged = UserFirebase.getUserId();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        btnSave.setOnClickListener(this::validateCompanyData);


        retrieveCompanyData();


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUrl  = data.getData();
                            imgLogo.setImageURI(selectedImageUrl);

                            try {

                                Bitmap bitmap = getBitmapFromUri(selectedImageUrl);
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                                    byte[] imageInByte = byteArrayOutputStream.toByteArray();

                               final StorageReference imageCompanyRef = storageReference.getReference()
                                        .child("images")
                                        .child("company")
                                        .child(idUserLogged + ".jpeg");

                                UploadTask uploadTask = imageCompanyRef.putBytes(imageInByte);
                                uploadTask.addOnFailureListener(
                                            e -> snackBarMessage("Error uploading image")
                                        ).addOnSuccessListener(
                                                taskSnapshot -> imageCompanyRef.getDownloadUrl().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Uri downloadUri = task.getResult();
                                                snackBarMessage("Image uploaded successfully");
                                            }else {
                                                snackBarMessage("Error getting download URL");
                                            }
                                        }));
                            } catch (Exception e) {
                                e.printStackTrace();
                                snackBarMessage("Error processing image");
                            }

                        }
                    }
                });

        imgLogo.setOnClickListener(v -> pickImage());
    }

    private void retrieveCompanyData() {

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
                    edtTotalPrice.setText( company.getTotalPrice().toString() );
                    selectedImageUrl = Uri.parse(company.getImageUrl());
                    if ( selectedImageUrl != null ){
                        Picasso.get()
                                .load(selectedImageUrl)
                                .placeholder(R.drawable.img_perfil)
                                .error(R.drawable.ic_broken_image_24)
                                .into(imgLogo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        //Save com pany data
                        Company company = new Company();
                        company.setIdCompany(idUserLogged);
                        company.setName(name);
                        company.setCategory(category);
                        company.setTimeEstimate(timeEstimate);
                        company.setTotalPrice(totalPrice);
                        company.setImageUrl(selectedImageUrl.toString());
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_red_devil_100))); //Change to your desired color
        snackbar.show();

    }
}