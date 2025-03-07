package udemy.java.desenvolvimento.android.completo.ifood_clone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowProductsBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Products;


public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.MyViewHolder>{


    private final List<Products> products;
    private final Context context;

    public AdapterProducts(List<Products> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowProductsBinding binding = RowProductsBinding.inflate(inflater, parent, false);

        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_products, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Products product = products.get(i);
        holder.name.setText(product.getProductName());
        holder.description.setText(product.getProductCategory());
        String convertString = String.valueOf(product.getProductPrice());
        holder.price.setText(convertString);
        String imageUrl = product.getImageUrlProduct();


        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_add_a_photo_84)
                .error(R.drawable.ic_broken_image_24)
                .fit()
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        CurrencyEditText price;
        ImageView image;

        /* public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textNameProduct);
            description = itemView.findViewById(R.id.textDescriptionProduct);
            price = itemView.findViewById(R.id.textPriceProduct);

         }*/

        public MyViewHolder(RowProductsBinding binding) {
            super(binding.getRoot());
            name = binding.textNameProduct;
            description = binding.textDescriptionProduct;
            price = binding.textPriceProduct;
            image = binding.imageViewProduct;
            price.configureViewForLocale(Locale.GERMANY);
            }
        }
}
