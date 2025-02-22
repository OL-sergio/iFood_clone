package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityNewItemCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Products;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SysTemUi;

public class NewItemCompanyActivity extends AppCompatActivity {

    private ActivityNewItemCompanyBinding binding;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage firebaseStorage;

    private StorageReference storageReference;
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

        SysTemUi sysTemUi = new SysTemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        setupToolbar();
        components();

        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        storageReference = FirebaseConfiguration.getFirebaseStorage();
        idUserLogged = UserFirebase.getUserId();


        btnSave.setOnClickListener(this::validateCompanyData);


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                                selectedImageUrl  = result.getData().getData();
                                imgLogo.setImageURI(selectedImageUrl);
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

    private void validateCompanyData (View view ){

        Drawable drawable = imgLogo.getDrawable();
        Bitmap bitmap = null;

        String name = edtName.getText().toString();
        String category = edtCategory.getText().toString();
        String totalPrice = edtTotalPrice.getText().toString();

        if (drawable instanceof BitmapDrawable drawableImage) {
            if ( !name.isEmpty() ){
                if ( !category.isEmpty() ){
                        if ( !totalPrice.isEmpty() ){
                            bitmap = drawableImage.getBitmap();
                            uploadImage(bitmap ,name, category, totalPrice);

                        }else {
                            toastMessage("Intreduza o preço total.");
                        }
                }else {
                    toastMessage("Intreduza a categoria.");
                }
            }else {
                toastMessage("Intreduza o nome do produto.");
            }
        } else {
            // Convert VectorDrawable to Bitmap
            snackBarMessage("Selecione uma imagem do utilizador");
        }

    }
    private void uploadImage(Bitmap bitmap , String name, String category, String totalPrice) {



        Products products = new Products();
        storageReference = firebaseStorage.getReference()
                .child(Constants.IMAGES)
                .child(Constants.PRODUCTS)
                .child(idUserLogged)
                .child( products.getIdProduct() + Constants.JPG);






        if (bitmap != null) {
            bitmap = ((BitmapDrawable) imgLogo.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
            byte[] imageInByte = byteArrayOutputStream.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(imageInByte);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            });
            urlTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveProductData(downloadUri, name, category, totalPrice);
                } else {
                    // Handle failures
                    // ...
                    snackBarMessage("Erro ao fazer upload da imagem");
                }
            });
        }
    }


    private void saveProductData( Uri image, String name, String category, String totalPrice) {
        Products products = new Products();
        products.setIdUser(idUserLogged);
        products.setProductName(name);
        products.setProductCategory(category);

        String priceWithoutSymbol = totalPrice
                .replace(Constants.TARGET_STRING, "")
                .trim();

        products.setProductPrice(priceWithoutSymbol);
        products.setImageUrlProduct(image.toString());
        products.saveProductData();
        finish();

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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), message, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.c_red_devil_100))); //Change to your desired color
        snackbar.show();

    }
}