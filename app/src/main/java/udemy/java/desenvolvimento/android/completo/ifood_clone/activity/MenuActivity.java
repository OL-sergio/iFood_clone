package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterProducts;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityMenuBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Companies;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Products;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SysTemUi;

public class MenuActivity extends AppCompatActivity {

    private ActivityMenuBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String idUserLogged;

    private CircleImageView circleImageViewCompany;
    private TextView textViewCompanyName;
    private Companies selectedCompany;
    private RecyclerView recyclerCompanyMenu;
    private AdapterProducts adapterProducts;
    private final List<Products> productsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SysTemUi sysTemUi = new SysTemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        setupToolbar();
        components();

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            selectedCompany = (Companies) bundle.getSerializable(Constants.COMPANY);
            if (selectedCompany != null) {
                textViewCompanyName.setText(selectedCompany.getName());
                idUserLogged = selectedCompany.getIdCompany();

                String url = selectedCompany.getCompanyImageUrl();
                Picasso.get().load(url)
                        .placeholder(R.drawable.img_profile)
                        .error(R.drawable.ic_broken_image_24)
                        .into(circleImageViewCompany);

            }
        }
        retrieveProducts();
    }

    private void retrieveProducts() {

        DatabaseReference productsRef = databaseReference
                .child( Constants.PRODUCTS )
                .child( idUserLogged );

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Products product = ds.getValue(Products.class);
                    if(product != null){
                        productsList.add(product);
                    }
                }
                adapterProducts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("Error retrieving user name: " + error.getMessage());
            }
        });


    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Menu");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {

        circleImageViewCompany = binding.circleImageViewCompanyMenu;
        textViewCompanyName = binding.textViewCompaniesNameMenu;
        recyclerCompanyMenu = binding.recyclerViewCompanyMenu;

        recyclerCompanyMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompanyMenu.setHasFixedSize(true);
        adapterProducts = new AdapterProducts(productsList, this);
        recyclerCompanyMenu.setAdapter(adapterProducts);

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