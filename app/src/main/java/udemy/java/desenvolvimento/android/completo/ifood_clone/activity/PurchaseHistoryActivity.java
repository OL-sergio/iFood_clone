package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterHistory;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityPurchaseHistoryBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.History;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.ProgressDialog;

public class PurchaseHistoryActivity extends AppCompatActivity {

    private ActivityPurchaseHistoryBinding binding;

    private DatabaseReference databaseReference;
    private String idUserLogged;
    private String idCompany;
    private ProgressDialog progressDialog;

    private RecyclerView recyclerViewHistoryOrders;
    private AdapterHistory adapterHistory;
    private final List<History> historiesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPurchaseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        components();

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        idUserLogged = UserFirebase.getUserId();

        progressDialog = new ProgressDialog(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            idCompany =  bundle.getString(Constants.COMPANY_ID);
            Log.d("Company ID", "Company ID: " + idCompany);
        }else {
            Log.d("Error", "Error retrieving company id");
        }

        retrieveHistory();

    }

    private void retrieveHistory() {
        progressDialog.showProgressDialog();
        DatabaseReference referenceHistory = databaseReference
                .child(Constants.CUSTOMERS_HISTORY)
                .child(idUserLogged)
                .child(idCompany);

        referenceHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historiesList.clear();
                if (snapshot.getValue() != null){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        History history = ds.getValue(History.class);
                        if(history != null){
                            historiesList.add(history);
                            Log.d("History", "History: " + history);
                        }
                    }
                    adapterHistory.notifyDataSetChanged();
                    progressDialog.dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d( "Database Error","Error retrieving hitorical data : " + error.getMessage());
            }
        });

    }


    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Historico de Compras");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {
        /*
        *
        List<OrdersItems> childItemList1 = new ArrayList<>();
        childItemList1.add(new OrdersItems("adadad", "Child 1", 1, "11.0"));
        childItemList1.add(new OrdersItems("darar", "Child 2", 2, "12.0"));
        childItemList1.add(new OrdersItems("darar", "Child 3", 3, "13.0"));

        List<OrdersItems> childItemList2 = new ArrayList<>();
        childItemList2.add(new OrdersItems("adadad", "Child 1", 1, "11.0"));
        childItemList2.add(new OrdersItems("darar", "Child 2", 2, "12.0"));
        childItemList2.add(new OrdersItems("darar", "Child 3", 3, "13.0"));

        List<OrdersItems> childItemList3 = new ArrayList<>();
        childItemList3.add(new OrdersItems("adadad", "Child 1", 1, "11.0"));
        childItemList3.add(new OrdersItems("darar", "Child 2", 2, "12.0"));
        childItemList3.add(new OrdersItems("darar", "Child 3", 3, "13.0"));

        List<History> parentItemList = new ArrayList<>();
        parentItemList.add(new History("Parent 1","323", "childItemList1","","","",childItemList1));
        parentItemList.add(new History("Parent 2", "122","","","","", childItemList2));
        parentItemList.add(new History("Parent 3","3232", "","","","",childItemList1));
        parentItemList.add(new History("Parent 4","121","" ,"","","",childItemList3));
        */

        recyclerViewHistoryOrders = binding.recyclerViewPurchaseHistory;
        recyclerViewHistoryOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistoryOrders.setHasFixedSize(true);
        adapterHistory = new AdapterHistory(historiesList,this);
        recyclerViewHistoryOrders.setAdapter(adapterHistory);

    }
}
