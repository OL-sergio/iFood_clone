package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterItemsOrder;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityOrderViewBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.OrdersItems;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Orders;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.ProgressDialog;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SystemUi;

public class OrderViewActivity extends AppCompatActivity {

    private ActivityOrderViewBinding binding;

    private TextView textTotalQuantity;
    private TextView textViewAddress;
    private TextView textViewCustomerName;
    private ImageView imageViewCompany;
    private CurrencyEditText currencyEditTextTotalValue;
    private TextView textViewPhoneNumber;
    private RecyclerView recyclerViewItensOrder;
    private ProgressDialog progressDialog;


    private Orders orders;
    private Double totalValue;
    private int totalQuantity;
    private String companyImageUrl;

    private AdapterItemsOrder adapterItemsOrder;
    private final List<OrdersItems> itemOrdersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SystemUi sysTemUi = new SystemUi(this);
        sysTemUi.hideSystemUIHideNavigation();
        progressDialog = new ProgressDialog(this);

        setupToolbar();
        components();

        orders = new Orders();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            orders = (Orders) bundle.getSerializable(Constants.CART_LIST);
            totalValue = (double) bundle.getSerializable(Constants.TOTAL_ORDER_VALUE);
            totalQuantity = (int) bundle.getSerializable(Constants.TOTAL_ORDER_QUANTITY);
            companyImageUrl = (String) bundle.getSerializable(Constants.COMPANY_IMAGE);

            if (orders != null  && totalValue != null && totalQuantity != 0 && companyImageUrl != null){
                progressDialog.showProgressDialog();
                textViewCustomerName.setText("Cliente: " +  orders.getCustomerName());
                textTotalQuantity.setText("Quant total:  " + totalQuantity  + "  - Preço total: " );

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                currencyEditTextTotalValue.setText(  decimalFormat.format(totalValue)  );

                textViewPhoneNumber.setText("Telefone: " + orders.getPhoneNumber());
                textViewAddress.setText("Endereço: " + orders.getAddress());
                Picasso.get()
                        .load(companyImageUrl)
                        .error(R.drawable.ic_broken_image_24)
                        .into(imageViewCompany);

                for (OrdersItems itemOrders : orders.getItemOrders()){
                    itemOrdersList.add(itemOrders);

                }

            }
            progressDialog.dismissProgressDialog();
        }

    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Carrinho de Compras");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {
        textViewCustomerName = binding.textViewCustomerNameOrder;
        textTotalQuantity = binding.textViewTotalQuantityOrder;
        textViewAddress = binding.textViewAddressOrder;
        imageViewCompany = binding.imageViewCompanyOrder;
        currencyEditTextTotalValue = binding.textViewTotalOrderValue;
        textViewPhoneNumber = binding.textViewPhoneOrder;
        currencyEditTextTotalValue.configureViewForLocale(Locale.GERMANY);


        recyclerViewItensOrder = binding.recyclerViewViewItemsOrder;
        recyclerViewItensOrder.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItensOrder.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.dw_divisor))); // Set custom drawable
        recyclerViewItensOrder.addItemDecoration(dividerItemDecoration);

      /* recyclerViewItensOrder.addItemDecoration(
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        );*/
        adapterItemsOrder = new AdapterItemsOrder(itemOrdersList);
        recyclerViewItensOrder.setAdapter(adapterItemsOrder);
    }

}