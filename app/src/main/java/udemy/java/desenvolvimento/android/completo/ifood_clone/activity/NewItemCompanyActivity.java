package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityNewItemCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Company;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Products;

public class NewItemCompanyActivity extends AppCompatActivity {

    private ActivityNewItemCompanyBinding binding;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage storageReference;
    private DatabaseReference databaseReference;
    private StorageReference imageCompanyRef;
    private String idUserLogged;
    private Uri selectedImageUrl;

    private CircleImageView imgLogo;
    private EditText edtName;
    private EditText edtCategory;
    private CurrencyEditText edtTotalPrice;
    private Button btnSave;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewItemCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        components();

        storageReference = FirebaseConfiguration.getFirebaseStorage().getStorage();
        idUserLogged = UserFirebase.getUserId();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        btnSave.setOnClickListener(this::validateCompanyData);

        imageCompanyRef = storageReference.getReference()
                .child(Constants.IMAGES)
                .child(Constants.PRODUCTS)
                .child(idUserLogged + Constants.JPG);

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

                                UploadTask uploadTask = imageCompanyRef.putBytes(imageInByte);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        snackBarMessage("Error uploading image");
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        snackBarMessage("");
                                    }
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                                snackBarMessage("Erro ao carregar imagem!");
                            }

                        }
                    }
                });

        imgLogo.setOnClickListener(v -> pickImage());

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
        String totalPrice = edtTotalPrice.getText().toString();
        if ( !name.isEmpty() ){
            if ( !category.isEmpty() ){

                    if ( !totalPrice.isEmpty() ){
                        //Save com pany data
                        Products products = new Products();
                        products.setIdProduct(idUserLogged);
                        products.setProductName(name);
                        products.setProductCategory(category);
                        products.setProductPrice(totalPrice);
                        products.setImageUrlProduct(selectedImageUrl.toString());
                        products.saveProductData();
                        finish();

                        toastMessage("Produto criado com sucesso!");

                    }else {
                        toastMessage("Intreduza o preço total.");
                    }
            }else {
                toastMessage("Intreduza a categoria.");
            }
        }else {
            toastMessage("Intreduza o nome do produto.");
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
            imgLogo = binding.circleImageViewNewItemCompany;
            edtName = binding.editTextCompanyNewItemName;
            edtCategory = binding.editTextCompanyNewItemType;
            edtTotalPrice = binding.editTextCompanyNewItemPrice;
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