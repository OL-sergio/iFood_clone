package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
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
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivitySettingCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Companies;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.BitmapConverter;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SysTemUi;

public class SettingCompanyActivity extends AppCompatActivity {

    private ActivitySettingCompanyBinding binding;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private String idUserLogged;
    private String selectedImageUrl;
    private Companies company;

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

        SysTemUi sysTemUi = new SysTemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        setupToolbar();
        components();

        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        idUserLogged = UserFirebase.getUserId();
        company = new Companies();


        btnSave.setOnClickListener(this::validateCompanyData);

        retrieveCompanyData();


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUrl  = String.valueOf(result.getData().getData());
                            imgLogo.setImageURI(Uri.parse(selectedImageUrl));
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

    private void retrieveCompanyData() {

        DatabaseReference companyRef = databaseReference
                .child( Constants.COMPANY )
                .child(idUserLogged);

        companyRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null){
                    company = snapshot.getValue(Companies.class);
                    edtName.setText(company.getName());
                    edtCategory.setText(company.getCategory());
                    edtTimeEstimate.setText(company.getEstimatedTime());
                    edtTotalPrice.setText( company.getTotalPrice() );

                    Picasso.get()
                            .load(company.getCompanyImageUrl())
                            .error(R.drawable.ic_broken_image_24)
                            .into(imgLogo); // Load the image into ImageView

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
               toastMessage( error + " " + "Erro o recuperar dados da Empresa");
            }
        });
    }

    private void validateCompanyData (View view ){

        Drawable drawable = imgLogo.getDrawable();
        Bitmap bitmap;
        String name = edtName.getText().toString();
        String category = edtCategory.getText().toString();
        String timeEstimate = edtTimeEstimate.getText().toString();
        String totalPrice = edtTotalPrice.getText().toString();

        if (drawable instanceof BitmapDrawable drawableImage) {
            if ( !name.isEmpty() ){
                if ( !category.isEmpty() ){
                    if ( !timeEstimate.isEmpty() ){
                        if ( !totalPrice.isEmpty() ){

                            bitmap =  drawableImage.getBitmap();
                            //Save com company data
                            uploadImage( bitmap, name, category, timeEstimate, totalPrice );

                        }else {
                            snackBarMessage("Intreduza o preço médio da sua Empresa");
                        }
                    }else {
                        snackBarMessage("Intreduza a estimativa estrega de tempo médio da sua Empresa");
                    }
                }else {
                    snackBarMessage("Intreduza a sua Categoria da sua Empresa");
                }
            }else {
                snackBarMessage("Intreduza o Nome da sua Empresa");
            }
        } else {
            // Convert VectorDrawable to Bitmap
            snackBarMessage("Intreduza uma imagem da sua Empresa");
        }
    }


    private void uploadImage(Bitmap bitmap,String name, String category, String timeEstimate, String totalPrice) {


        StorageReference storageReference = firebaseStorage.getReference()
                .child(Constants.IMAGES)
                .child(Constants.COMPANY)
                .child(idUserLogged)
                .child(idUserLogged + Constants.JPG);


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
                    saveCompanyData( downloadUri ,name , category, timeEstimate, totalPrice);
                } else {
                    // Handle failures
                    // ...
                    snackBarMessage("Erro ao fazer upload da imagem");
                }
            });
        }
    }
    private void saveCompanyData(Uri selectedImageUrl,String name, String category, String timeEstimate, String totalPrice) {
        company.setIdCompany(idUserLogged);
        company.setName(name);
        company.setFilterName(name);
        company.setCategory(category);
        company.setEstimatedTime(timeEstimate);
        company.setTotalPrice(totalPrice);
        company.setCompanyImageUrl(selectedImageUrl.toString());
        company.saveCompanyData();
        company.updateUserCompany();
        finish();

    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Configurações da Empresa");
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
}