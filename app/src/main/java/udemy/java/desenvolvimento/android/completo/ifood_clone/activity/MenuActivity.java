package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.adapter.AdapterProducts;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityMenuBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.DialogAmountOrderBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.listener.RecyclerItemClickListener;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Companies;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Customer;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.History;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.OrdersItems;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Orders;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Products;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.ProgressDialog;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SystemUi;

public class MenuActivity extends AppCompatActivity {

    private ActivityMenuBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private String idUserLogged;
    private String idCompany;
    private String idOrder;
    private int totalOrderQuantity;
    private Double totalOrderValue;
    private String paymentMethod = "";

    private CircleImageView circleImageViewCompany;
    private TextView textViewCompanyName;
    private TextView textViewTotalOrder;
    private TextView textViewViewCartList;
    private CurrencyEditText currencyEditTextTotalOrderValue;
    private LinearLayout linearLayoutPurchaseHistory;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerCompanyMenu;
    private AdapterProducts adapterProducts;
    private DecimalFormat decimalFormat;

    private Customer customers;
    private Orders customerOrder;
    private History historyOrder;
    private String companyImageUrl;
    private final List<Products> comapnyProductsList = new ArrayList<>();
    private List<OrdersItems> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SystemUi sysTemUi = new SystemUi(this);
        sysTemUi.hideSystemUIHideNavigation();

        setupToolbar();
        components();

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        firebaseStorage = FirebaseConfiguration.getFirebaseStorage().getStorage();
        idUserLogged = UserFirebase.getUserId();
        decimalFormat = new DecimalFormat("0.00");


        customers = new Customer();
        historyOrder = new History();

        progressDialog = new ProgressDialog(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Companies selectCompany = (Companies) bundle.getSerializable(Constants.COMPANY);
            if (selectCompany != null) {
                textViewCompanyName.setText(selectCompany.getName());
                idCompany = selectCompany.getIdCompany();
                companyImageUrl = selectCompany.getCompanyImageUrl();
                Picasso.get().load(companyImageUrl)
                        .placeholder(R.drawable.img_profile)
                        .error(R.drawable.ic_broken_image_24)
                        .into(circleImageViewCompany);

            }
        }

        retrieveProducts();
        retrieveUserData();

        textViewViewCartList.setOnClickListener(v -> {
            if (cartList.size() > 0){

                Intent intent = new Intent(MenuActivity.this, OrderViewActivity.class);
                intent.putExtra(Constants.CART_LIST, customerOrder);
                intent.putExtra(Constants.TOTAL_ORDER_VALUE, totalOrderValue);
                intent.putExtra(Constants.TOTAL_ORDER_QUANTITY, totalOrderQuantity);
                intent.putExtra(Constants.COMPANY_IMAGE, companyImageUrl);
                startActivity(intent);

            } else {
                snackBarMessage("Seu carrinho está vazio");
            }
        });

        recyclerCompanyMenu.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerCompanyMenu,
                new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }

                @Override
                public void onItemClick(View view, int position) {
                    confirmationAmountOrder(position);

                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
             })
        );


        linearLayoutPurchaseHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, PurchaseHistoryActivity.class);
            intent.putExtra(Constants.COMPANY_ID, idCompany);
            startActivity(intent);
        });


    }

    private void confirmationAmountOrder(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quantidade");
            builder.setMessage("Digite a quantidade desejada");

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_amount_order, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    DialogAmountOrderBinding binding = DialogAmountOrderBinding.bind(dialogView);

                    String inputAmount = binding.editTextDialogAmountOrder.getText().toString();
                    if (inputAmount.isEmpty()){
                        snackBarMessage("Digite a quantidade desejada");
                        return;
                    }

                    int quantity = Integer.parseInt(inputAmount);
                    if (quantity <= 0){
                        snackBarMessage("Digite valor acima de 0");
                        return;
                    }

                    Products selectedProduct = comapnyProductsList.get(position);
                    OrdersItems itemOrders = new OrdersItems();
                    itemOrders.setIdProduct(selectedProduct.getIdProduct());
                    itemOrders.setNameProduct(selectedProduct.getProductName());

                    String totalPrice = selectedProduct.getProductPrice();
                    String priceWithoutSymbol = totalPrice
                            .replace(Constants.TARGET_STRING, "") // Remove currency symbol if applicable
                            //.replace(",", ".") // Replace comma with period if needed
                            .replaceAll("\\s+", "") // Remove all whitespace characters, including non-breaking spaces
                            .trim();
                    itemOrders.setPrice(priceWithoutSymbol);
                    itemOrders.setQuantity(quantity);
                    updateCart(itemOrders , selectedProduct);

                    if (customerOrder == null){
                        customerOrder = new Orders( idUserLogged, idCompany);
                    }

                    customerOrder.setPhoneNumber( customers.getPhoneNumber() );
                    customerOrder.setCustomerName( customers.getName() );
                    customerOrder.setAddress( customers.getAddress() );
                    customerOrder.setItemOrders( cartList );
                    customerOrder.saveCustomerOrder( );

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            positiveButton.setPadding(100,25,150,150);
            negativeButton.setPadding(100,25,100,150);

            positiveButton.setTextColor(ContextCompat.getColor(this,R.color.c_irish_green_100));
            negativeButton.setTextColor(ContextCompat.getColor(this,R.color.c_red_devil_100));

    }

    private void updateCart(OrdersItems itemOrders , Products selectedProduct) {
        boolean productExists = false;
        for (OrdersItems itemOrder : cartList) {
            if (itemOrder.getIdProduct().equals(selectedProduct.getIdProduct())){
                itemOrder.setQuantity(itemOrder.getQuantity() + itemOrders.getQuantity());
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            cartList.add(itemOrders);
        }
    }

    private void retrieveUserData() {
        progressDialog.showProgressDialog();

        DatabaseReference usersReference = databaseReference
                .child( Constants.CUSTOMERS )
                .child( idUserLogged );
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    customers = snapshot.getValue(Customer.class);
                }
                retrieveOrder();
            }

            private void retrieveOrder() {

                DatabaseReference orderReference = databaseReference
                        .child( Constants.CUSTOMERS_ORDERS )
                        .child( idCompany )
                        .child( idUserLogged );
                orderReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        cartList = new ArrayList<>();
                        totalOrderQuantity = 0;
                        totalOrderValue = 0.0;

                        if (snapshot.getValue() != null){
                            customerOrder = snapshot.getValue(Orders.class);
                                if (customerOrder != null){
                                    cartList = customerOrder.getItemOrders();
                                    idOrder = customerOrder.getOrderId();

                                    double price = 0.0;
                                    for (OrdersItems itemOrders : cartList) {
                                        int quantity = itemOrders.getQuantity();

                                        String replaceString = itemOrders.getPrice()
                                                .replace(".", "")
                                                .replace(",", ".");
                                        price = Double.parseDouble(replaceString);

                                        totalOrderValue += ( quantity * price);
                                        totalOrderQuantity += quantity;

                                    }
                                }
                            }

                        decimalFormat = new DecimalFormat("0.00");
                        textViewTotalOrder.setText( "Qtd:  " + totalOrderQuantity + "     Total: " );
                        currencyEditTextTotalOrderValue.setText( decimalFormat.format( totalOrderValue ) );
                        progressDialog.dismissProgressDialog();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d( "Database Error","Error retrieving cart list data: " + error.getMessage());
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d( "Database Error","Error retrieving costumer data: " + error.getMessage());
            }
        });
    }

    private void retrieveProducts() {
        progressDialog.showProgressDialog();

        DatabaseReference productsRef = databaseReference
                .child( Constants.PRODUCTS )
                .child( idCompany );

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comapnyProductsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Products product = ds.getValue(Products.class);
                    if(product != null){
                        comapnyProductsList.add(product);
                    }
                }
                adapterProducts.notifyDataSetChanged();
                progressDialog.dismissProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              Log.d( "Database Error","Error retrieving user name: " + error.getMessage());
            }
        });
    }

    private void confirmationOrder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecionar metedo de pagamento");
        CharSequence[] items = new CharSequence[]{
                "Dinheiro",
                "Cartão de Crédito",
                "Cartão de Débito"
        };
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                paymentMethod = "";
                switch (which){
                    case 0:
                        paymentMethod = "Dinheiro";
                        break;
                    case 1:
                        paymentMethod = "Cartão de Crédito";
                        break;
                    case 2:
                        paymentMethod = "Cartão de Débito";
                        break;
                }

                if (paymentMethod != null) {
                    // Optionally show a Toast or update UI
                    snackBarMessage( "Selected: " + paymentMethod );
                }
            }
        });

        EditText editTextObservation = new EditText(this);
        editTextObservation.setHint("Envie uma messagem para o entregador");
        builder.setView(editTextObservation);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String observation = editTextObservation.getText().toString();
                if (observation.isEmpty()){
                    snackBarMessage("Envie uma mensagem para o entregador");
                    return;
                }

                customerOrder.setPaymentMethod(paymentMethod);
                customerOrder.setObservation(observation);
                customerOrder.setTotalValue(decimalFormat.format(totalOrderValue));
                customerOrder.setOrderStatus(Constants.CONFIRMED);
                customerOrder.saveConfirmationOrder();

                historyOrder.setName(customers.getName());
                historyOrder.setAddress(customers.getAddress());
                historyOrder.setTotalPrice( decimalFormat.format(totalOrderValue) );
                historyOrder.setPaymentMethod(paymentMethod);
                historyOrder.setTotalQuantity(String.valueOf(totalOrderQuantity));
                historyOrder.setOrdersItems(cartList);
                historyOrder.saveCustomerHistoryOrders( idUserLogged, idCompany, customerOrder.getOrderId() );
                customerOrder.removeOrder( );

                finish();
                toastMessage("Pedido realizado com sucesso");
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        positiveButton.setPadding(100,25,150,50);
        negativeButton.setPadding(100,25,150,50);

        positiveButton.setTextColor(ContextCompat.getColor(this,R.color.c_irish_green_100));
        negativeButton.setTextColor(ContextCompat.getColor(this,R.color.c_red_devil_100));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            if (id == R.id.menuConfirmationOrder) {
                confirmationOrder();
                return true;
            }
        return super.onOptionsItemSelected(item);
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

        linearLayoutPurchaseHistory = binding.linearLayoutPurchaseHistory;
        circleImageViewCompany = binding.circleImageViewCompanyMenu;
        textViewCompanyName = binding.textViewCompaniesNameMenu;
        recyclerCompanyMenu = binding.recyclerViewCompanyMenu;
        textViewViewCartList = binding.textViewViewCart;
        textViewTotalOrder = binding.textViewNumberSelectedItems;
        currencyEditTextTotalOrderValue = binding.currencyEditTextTotalPriceSelectedItems;
        currencyEditTextTotalOrderValue.configureViewForLocale(Locale.GERMANY);

        recyclerCompanyMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompanyMenu.setHasFixedSize(true);
        adapterProducts = new AdapterProducts(comapnyProductsList, this);
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