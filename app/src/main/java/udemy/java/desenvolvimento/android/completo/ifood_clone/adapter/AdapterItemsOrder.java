package udemy.java.desenvolvimento.android.completo.ifood_clone.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;

import java.util.List;
import java.util.Locale;

import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowItemsOrderBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.ItemOrders;

public class AdapterItemsOrder extends RecyclerView.Adapter<AdapterItemsOrder.MyViewHolder> {
    private final List<ItemOrders> itemsOrder;


    public AdapterItemsOrder(List<ItemOrders> itemOrders ) {
        this.itemsOrder = itemOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowItemsOrderBinding binding = RowItemsOrderBinding.inflate(inflater, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_products, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        ItemOrders itemOrders = itemsOrder.get(i);
        holder.name.setText(itemOrders.getNameProduct());

        String quantity = String.valueOf(itemOrders.getQuantity());
        holder.quantity.setText(quantity);

        String price = String.valueOf(itemOrders.getPrice());
        holder.price.setText(price);


    }

    @Override
    public int getItemCount() {
        return itemsOrder.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        CurrencyEditText price;

        public MyViewHolder(RowItemsOrderBinding binding) {
            super(binding.getRoot());
            name = binding.textViewProductName;
            quantity = binding.textViewProductTotalQuantity;
            price = binding.textViewProductPrice;

            price.configureViewForLocale(Locale.GERMANY);
        }
    }
}
