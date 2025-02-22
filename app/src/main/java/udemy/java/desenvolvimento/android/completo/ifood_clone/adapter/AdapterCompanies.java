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
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.RowCompaniesBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.Companies;


public class AdapterCompanies extends RecyclerView.Adapter<AdapterCompanies.MyViewHolder> {

    private List<Companies> companyList;
    private Context context;
    public AdapterCompanies(List<Companies> companies , Context context) {
        this.companyList = companies;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowCompaniesBinding binding = RowCompaniesBinding.inflate(inflater, parent, false);
        //View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_companies, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Companies company = companyList.get(i);
        holder.companyName.setText(company.getName());
        holder.category.setText(company.getCategory() + "  - ");
        holder.estimateTime.setText(company.getEstimatedTime() + " Min - ");
        holder.deliveryPrice.setText("R$ " + company.getTotalPrice());


        String imageUrl = company.getCompanyImageUrl();

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.ic_broken_image_24)
                .into(holder.imageCompany);
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public void updateData(List<Companies> newCompanyList) {
        this.companyList.clear();
        this.companyList.addAll(newCompanyList);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCompany;
        TextView companyName;
        TextView category;
        TextView estimateTime;
        CurrencyEditText deliveryPrice;

        public MyViewHolder(RowCompaniesBinding binding) {
            super(binding.getRoot());

            companyName = binding.textViewCompaniesName;
            category = binding.textViewCompaniesType;
            estimateTime = binding.textViewCompaniesDeliveryTime;
            deliveryPrice = binding.editTextCurrencyCompaniesTotalPrice;
            imageCompany = binding.imageViewCompanies;

            deliveryPrice.configureViewForLocale(Locale.GERMANY);
        }
    }
}
