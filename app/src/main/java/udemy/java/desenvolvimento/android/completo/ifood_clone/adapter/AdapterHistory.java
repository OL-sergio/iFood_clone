package udemy.java.desenvolvimento.android.completo.ifood_clone.adapter;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowItemsOrderBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowPurchaseHistoryBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.History;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.OrdersItems;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.MyViewHolder> {

    private List<History> historyList;
    private final Context context;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public AdapterHistory(List<History> listHistory, Context context) {
        this.historyList = listHistory != null ? listHistory : new ArrayList<>();
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowPurchaseHistoryBinding binding = RowPurchaseHistoryBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        History listHistory = historyList.get(position);
        if (listHistory != null && listHistory.getOrdersItems() != null) {

            List<OrdersItems> ordersItems = listHistory.getOrdersItems();
            holder.name.setText(listHistory.getName());


            //solve bug with currencyEditText cant convert string to double "50.1"

            holder.totalPrice.setText(listHistory.getTotalPrice());


            holder.totalQuantity.setText(listHistory.getTotalQuantity());
            holder.address.setText(listHistory.getAddress());
            holder.paymentMethod.setText(listHistory.getPaymentMethod());

            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    holder.recyclerItemsHistory.getContext(),
                    LinearLayoutManager.VERTICAL, false
            );

            layoutManager.setInitialPrefetchItemCount(listHistory.getOrdersItems().size());
            AdapterItemsHistory adapterItemsHistory = new AdapterItemsHistory(ordersItems, context);
            holder.recyclerItemsHistory.setLayoutManager(layoutManager);
            holder.recyclerItemsHistory.setHasFixedSize(true);
            holder.recyclerItemsHistory.setAdapter(adapterItemsHistory);
            holder.recyclerItemsHistory.setRecycledViewPool(viewPool);

        } else {
           holder.recyclerItemsHistory.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView address;
        private TextView paymentMethod;
        private CurrencyEditText totalPrice;
        private TextView totalQuantity;

        private final RecyclerView recyclerItemsHistory;

         public MyViewHolder(RowPurchaseHistoryBinding binding) {
            super(binding.getRoot());
            name = binding.textViewCostumerName;
            address = binding.textViewAddressOrder;
            totalPrice = binding.currencyEditTextProductPrice;
            paymentMethod = binding.textViewPaymentMethod;
            totalQuantity = binding.textViewProductTotalQuantity;
            totalPrice.configureViewForLocale(Locale.GERMANY);

            recyclerItemsHistory = binding.recyclerViewRowItemsPurchaseHistory;



        }
    }
}
