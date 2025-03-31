package udemy.java.desenvolvimento.android.completo.ifood_clone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowItemsHistoryBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.OrdersItems;

public class AdapterItemsHistory extends RecyclerView.Adapter<AdapterItemsHistory.MyViewHolder> {

    private final List<OrdersItems> historyItems;


    public AdapterItemsHistory( List<OrdersItems> historyItems) {
        this.historyItems = historyItems != null ? historyItems : new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowItemsHistoryBinding binding = RowItemsHistoryBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrdersItems history = historyItems.get(position);
        holder.name.setText(history.getNameProduct());

        String quantity = String.valueOf(history.getQuantity());
        holder.quantity.setText(quantity);

        String price = history.getPrice();
        holder.price.setText( price );
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView quantity;
        private final CurrencyEditText price;

        public MyViewHolder(RowItemsHistoryBinding binding) {
            super(binding.getRoot());
            name = binding.textViewProductName;
            quantity = binding.textViewProductTotalQuantity;
            price = binding.currencyEditTextProductPrice;
            price.configureViewForLocale(Locale.GERMANY);

        }
    }
}
