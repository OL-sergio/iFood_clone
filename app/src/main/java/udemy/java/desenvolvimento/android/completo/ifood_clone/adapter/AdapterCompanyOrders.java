package udemy.java.desenvolvimento.android.completo.ifood_clone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowOrdersBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Orders;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.OrdersItems;

public class AdapterCompanyOrders extends RecyclerView.Adapter<AdapterCompanyOrders.MyViewHolder> {

    private final List<Orders> companyOrders;
    public AdapterCompanyOrders(List<Orders> companyOrders) {
        this.companyOrders = companyOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowOrdersBinding binding = RowOrdersBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCompanyOrders.MyViewHolder holder, int position) {
        Orders order = companyOrders.get(position);
        holder.name.setText(order.getCustomerName());
        holder.address.setText("Morada: " + order.getAddress());
        holder.paymentMethod.setText("Método de pagamento: " + order.getPaymentMethod());
        holder.observation.setText( "Observações: " + order.getObservation());

        List<OrdersItems> ordersItems = new ArrayList<>();
        ordersItems = order.getItemOrders();
        String description = "";

        int numberItems = 1;
        double total = 0.0;
        String stringTotal = "";
        for (OrdersItems items : ordersItems) {
            int quantity = items.getQuantity();

            String priceString = items.getPrice().replace(",", ".");

            double price = Double.parseDouble(priceString);

            total += (quantity * price);

            String name = items.getNameProduct();

            priceString = priceString.replace(".", ",");

            stringTotal = String.valueOf(total);
            stringTotal = stringTotal.replace(".", ",");

            description += numberItems + ": " + name + " / " + quantity + " - " + priceString + "\n";
            numberItems++;
        }
        description += "Total: " + stringTotal;

        holder.order.setText(description);
    }

    @Override
    public int getItemCount() {
        return companyOrders.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView address;
        private final TextView paymentMethod;
        private final TextView observation;
        private final TextView order;

        public MyViewHolder(RowOrdersBinding binding) {
            super(binding.getRoot());
            name = binding.textViewNameOrders;
            address = binding.textViewAddressOrders;
            paymentMethod = binding.textViewPaymentMethodOrders;
            observation = binding.textViewObservationOrders;
            order = binding.textViewItemsOrders;

        }
    }
}
