package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterCompanyOrders;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityOrdersCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.listener.RecyclerItemClickListener;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Orders;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.ProgressDialog;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SystemUi;

public class OrdersCompanyActivity extends AppCompatActivity {
    private ActivityOrdersCompanyBinding binding;
    private RecyclerView recyclerCompanyOrders;
    private AdapterCompanyOrders adapterCompanyOrders;
    private final List<Orders> companyOrders = new ArrayList<>();

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private String idLoggedCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrdersCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SystemUi sysTemUi = new SystemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        idLoggedCompany = UserFirebase.getUserId();

        progressDialog = new ProgressDialog(this);

        // Apagar o nó orders no firebase.
        setupToolbar();
        components();
        
        retrieveOrders();

        recyclerCompanyOrders.addOnItemTouchListener( new RecyclerItemClickListener(
                this,
                recyclerCompanyOrders,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }

                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Orders orders = companyOrders.get(position);
                        orders.setOrderStatus(Constants.FINALIZED);
                        orders.updateOrderStatus();

                    }
                }
        ));

    }

    private void retrieveOrders() {
        progressDialog.showProgressDialog();
        DatabaseReference ordersReference = databaseReference
                .child(Constants.ORDERS)
                .child(idLoggedCompany);

        Query queryOrderSearch = ordersReference.orderByChild(Constants.STATUS_ORDER)
                .equalTo(Constants.CONFIRMED);

        queryOrderSearch.addValueEventListener( new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                companyOrders.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Orders orders = data.getValue(Orders.class);
                        companyOrders.add(orders);
                    }
                    adapterCompanyOrders.notifyDataSetChanged();
                    progressDialog.dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Pedidos dos Clientes");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {

        recyclerCompanyOrders = binding.recyclerViewOrdersCustomers;
        recyclerCompanyOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompanyOrders.setHasFixedSize(true);
        adapterCompanyOrders = new AdapterCompanyOrders(companyOrders);
        recyclerCompanyOrders.setAdapter(adapterCompanyOrders);

    }

}