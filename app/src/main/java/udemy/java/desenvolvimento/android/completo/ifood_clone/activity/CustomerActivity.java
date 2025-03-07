package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterCompanies;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityClientBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.listener.RecyclerItemClickListener;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Companies;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Users;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.AnimationsSearch;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SystemUi;


public class CustomerActivity extends AppCompatActivity {

    private ActivityClientBinding binding;

    private UserFirebase userFirebase;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private String idUserLogged;
    private Companies company;
    private Users user;

    private AnimationsSearch animationsSearch;
    private SearchView searchView;
    private RecyclerView recyclerCompanies;
    private AdapterCompanies adapterCompanies;
    private final List<Companies> companyList = new ArrayList<>();

    private boolean isSearchViewExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SystemUi sysTemUi = new SystemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        setupToolbar();
        components();

        userFirebase = new UserFirebase();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        storageReference = FirebaseConfiguration.getFirebaseStorage();
        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        idUserLogged = UserFirebase.getUserId();
        animationsSearch = new AnimationsSearch();
        company = new Companies();

        retrieveCompanies();

        animationsSearch.animateSearchViewExpand(isSearchViewExpanded, searchView);

        // Set up the search view listener
       /* searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                animateSearchView(hasFocus);
                animateSearchViewExpand(hasFocus);
            }
        });*/
        searchView.setQueryHint("Search companies");
        // Handle search query submission
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query submission
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change in the search view
                searchForCompany(newText);

                return true;
            }
        });

        recyclerCompanies.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerCompanies,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }

                    @Override
                    public void onItemClick(View view, int position) {
                        company = companyList.get(position);
                        Intent intent = new Intent(CustomerActivity.this, MenuActivity.class);
                        intent.putExtra(Constants.COMPANY, company);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }
        ));
    }

    private void searchForCompany(String newText) {
        DatabaseReference companiesRef = databaseReference
                .child( Constants.COMPANY );
        Query query = companiesRef.orderByChild(Constants.FILTER_NAME)
                .startAt(newText)
                .endAt(newText + Constants.STRING_CODE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                companyList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    company = ds.getValue(Companies.class);
                    if (company != null){
                        companyList.add(company);
                    }
                }
                adapterCompanies.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveCompanies() {

        DatabaseReference companiesRef = databaseReference
                .child( Constants.COMPANY );
        companiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                companyList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    company = ds.getValue(Companies.class);
                    if (company != null){
                        companyList.add(company);
                    }
                }
                adapterCompanies.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    binding.toolbar.toolbarClient.setTitle( user.getName() );
                }else {
                    binding.toolbar.toolbarClient.setTitle( R.string.ifood_client );
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
        inflater.inflate(R.menu.menu_client, menu);

        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        MenuItem settingsItem = menu.findItem(R.id.menuSettingsUser);
        MenuItem logoutItem = menu.findItem(R.id.menuLogout);

        if (isSearchViewExpanded == true) {
            searchView.setVisibility(View.VISIBLE);
            menuItem.setIcon(R.drawable.ic_close_24);
            settingsItem.setVisible(false);
            logoutItem.setVisible(false);


        } else if (isSearchViewExpanded == false) {
            searchView.setVisibility(View.GONE);
            menuItem.setIcon(R.drawable.ic_search_24);

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuSearch) {
            startSearchView();
            return true;
        }

        if (id == R.id.menuSettingsUser) {
            settingUser();
            return true;
        }

        if (id == R.id.menuLogout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSearchView() {

        if (isSearchViewExpanded) {
            animationsSearch.animateSearchView( false, searchView );
            isSearchViewExpanded = false;
            animationsSearch.animateSearchViewExpand( isSearchViewExpanded, searchView );
            invalidateOptionsMenu();

        } else if (!isSearchViewExpanded) {
            animationsSearch.animateSearchView(true, searchView );
            isSearchViewExpanded = true;
            animationsSearch.animateSearchViewExpand( isSearchViewExpanded, searchView );
            invalidateOptionsMenu();

        }
    }

    private void settingUser() {
        startActivity(new Intent(this, SettingCustomerActivity.class));
    }

    private void logoutUser() {
        userFirebase.logoutUser();
        startActivity(new Intent(this, AuthenticationActivity.class));
        finish();
    }

    private void  setupToolbar(){

        Toolbar toolbar = binding.toolbar.toolbarClient;
        setSupportActionBar(toolbar);
    }
    private void components() {

        userFirebase = new UserFirebase();
        searchView = binding.toolbar.searchView;// Collapse the search view

        recyclerCompanies = binding.recyclerViewCompanies;
        recyclerCompanies.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompanies.setHasFixedSize(true);
        adapterCompanies = new AdapterCompanies(companyList, this);
        recyclerCompanies.setAdapter(adapterCompanies);

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