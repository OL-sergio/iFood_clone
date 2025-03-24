package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Users;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SystemUi;


public class CompanyActivity extends AppCompatActivity {

    private ActivityCompanyBinding binding;

    private UserFirebase userFirebase;
    private DatabaseReference databaseReference;
    private Products products;
    private Users user;
    private String idUserLogged;
    private RecyclerView recyclerProducts;
    private AdapterProducts adapterProducts;
    private final List<Products> productsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SystemUi sysTemUi = new SystemUi(this);
        sysTemUi.hideSystemUIHideNavigation();
        Window window = getWindow();
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.c_red_devil_100));


        setupToolbar();
        components();

        userFirebase = new UserFirebase();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        idUserLogged = UserFirebase.getUserId();

        products = new Products();
        user = new Users();


        retrieveProducts();

        recyclerProducts.addOnItemTouchListener( new RecyclerItemClickListener(
                this, recyclerProducts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                products = productsList.get(position);
                Log.d("SELECTEDPROFUCT",products.getIdProduct());
                products.remove();
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


        DatabaseReference userRef = databaseReference
                .child( Constants.USERS )
                .child( idUserLogged );
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                if (user != null){
                    binding.toolbar.toolbarCompany.setTitle( user.getName() );
                }else {
                    binding.toolbar.toolbarCompany.setTitle( R.string.ifood_company );
                }
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
        toolbar.setPadding(28, 12, 0, 12);
        setSupportActionBar(toolbar);

    }

    private void components() {

        recyclerProducts = binding.recyclerViewCompany;
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setHasFixedSize(true);
        adapterProducts = new AdapterProducts(productsList, this);
        recyclerProducts.setAdapter(adapterProducts);

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


