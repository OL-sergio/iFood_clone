package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivitySettingUserBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Companies;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Customer;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SysTemUi;

public class SettingUserActivity extends AppCompatActivity {

    private ActivitySettingUserBinding binding;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private String idUserLogged;
    private Uri selectedImageUrl;
    private Customer customer;

    private CircleImageView userImage;
    private EditText userName;
    private EditText userAddress;
    private Button buttonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SysTemUi sysTemUi = new SysTemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        setupToolbar();
        components();

        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        idUserLogged = UserFirebase.getUserId();
        customer = new Customer();

        buttonSave.setOnClickListener(this::validateCompanyData);

        recoverUserData();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUrl  = result.getData().getData();
                            userImage.setImageURI(selectedImageUrl);
                        }

                });
        userImage.setOnClickListener(v -> pickImage());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        }
    }

    private void recoverUserData() {
        DatabaseReference companyRef = databaseReference
                .child( Constants.CUSTOMERS )
                .child(idUserLogged);

        companyRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    customer = snapshot.getValue(Customer.class);
                    userName.setText(customer.getName());
                    userAddress.setText(customer.getAddress());
                    selectedImageUrl = Uri.parse(customer.getCustomerImageUrl());

                        Picasso.get()
                                .load(selectedImageUrl)
                                .error(R.drawable.ic_broken_image_24)
                                .into(userImage);
                   // Load the image into ImageView

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage("Error retrieving user data");
            }
        });
    }

    private void validateCompanyData (View view ){

        Drawable drawable = userImage.getDrawable();
        Bitmap bitmap = null;
        String name = userName.getText().toString();
        String address = userAddress.getText().toString();



        if (drawable instanceof BitmapDrawable drawableImage) {

            if ( !name.isEmpty() ){
                if ( !address.isEmpty() ){
                    bitmap = drawableImage.getBitmap();
                    uploadImage( bitmap, name, address );
                }else {
                    toastMessage("Enter the company category");
                }
            }else {
                toastMessage("Enter the user name");
            }
        } else  {
            // Convert VectorDrawable to Bitmap
            snackBarMessage("Selecione uma imagem do utilizador");
        }
    }


    private void uploadImage( Bitmap bitmap, String name, String address) {

        StorageReference storageReference = firebaseStorage.getReference()
                .child(Constants.IMAGES)
                .child(Constants.CUSTOMERS)
                .child(idUserLogged)
                .child( idUserLogged + Constants.JPG);

        if (bitmap != null) {
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
                    selectedImageUrl = task.getResult();
                    saveCustomerData(selectedImageUrl, name, address);
                } else {
                    // Handle failures
                    // ...
                    snackBarMessage("Erro ao fazer upload da imagem");
                }
            });
        }
    }

    private void saveCustomerData(Uri selectedImageUrl,String name, String address) {
        customer = new Customer();
        customer.setIdCustomer(idUserLogged);
        customer.setName(name);
        customer.setAddress(address);
        customer.setCustomerImageUrl(selectedImageUrl.toString());
        customer.saveCustomerData();
        customer.updateUserCustomer();
        finish();

    }

    private void setupToolbar() {
        MaterialToolbar toolbar  = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Configurações do Utilizador");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {
        userImage = binding.circleImageViewUserImage;
        userName = binding.editTextUserName;
        userAddress = binding.editTextUserAddress;
        buttonSave = binding.buttonSaveUserData;

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