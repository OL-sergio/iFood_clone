package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;

import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterProducts;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.listener.RecyclerItemClickListener;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Products;


public class CompanyActivity extends AppCompatActivity {

    private ActivityCompanyBinding binding;

    private UserFirebase userFirebase;
    private DatabaseReference databaseReference;
    private StorageReference  storageReference;
    private FirebaseStorage firebaseStorage;
    private Products products;
    private String idUserLogged;
    private RecyclerView recyclerProducts;
    private AdapterProducts adapterProducts;
    private List<Products> productsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        userFirebase = new UserFirebase();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        storageReference = FirebaseConfiguration.getFirebaseStorage();
        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        idUserLogged = UserFirebase.getUserId();
        products = new Products();

        components();


        retrieveProducts();

        recyclerProducts.addOnItemTouchListener( new RecyclerItemClickListener(
                this, recyclerProducts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Products selectedProduct = productsList.get(position);
                Log.d("SELECTEDPROFUCT",selectedProduct.getIdProduct());


                selectedProduct.deleteImageFromDataBase(selectedProduct.getIdProduct());
                selectedProduct.remove();

                adapterProducts.notifyDataSetChanged();

                toastMessage("Produto removido com sucesso");
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));

    }

    private void retrieveProducts() {

        DatabaseReference productsRef = databaseReference
                .child( Constants.PRODUCTS )
                .child( idUserLogged );

        productsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsRef.get().addOnCompleteListener(task -> {
                    productsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        productsList.add(ds.getValue(Products.class));
                    }
                    adapterProducts.notifyDataSetChanged();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_company, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuAddItem) {
            addNewItem();
            return true;
        }

        if (id == R.id.menuSettingsCompany) {
            settingUser();
            return true;
        }

        if (id == R.id.menuLogout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewItem() {
       changeActivity(NewItemCompanyActivity.class);
    }

    private void settingUser() {
        changeActivity(SettingCompanyActivity.class );
    }

    private void changeActivity(Class<? extends Activity> activity) {
        startActivity(new Intent(this, activity));
    }

    private void logoutUser() {
        userFirebase.logoutUser();
        startActivity(new Intent(this, AuthenticationActivity.class));
        finish();
    }


    private void setupToolbar() {
        Toolbar toolbar = binding.toolbar.toolbarCompany;
        toolbar.setTitle("Ifood- company");
        toolbar.setPadding(28, 12, 0, 12);
        setSupportActionBar(toolbar);
    }

    private void components() {

        recyclerProducts =binding.recyclerViewCompany;
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setHasFixedSize(true);
        adapterProducts = new AdapterProducts(productsList, this);
        recyclerProducts.setAdapter(adapterProducts);

    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


