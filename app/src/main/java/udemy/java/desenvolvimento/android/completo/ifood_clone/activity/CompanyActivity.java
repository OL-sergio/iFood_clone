package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;

import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityCompanyBinding;


public class CompanyActivity extends AppCompatActivity {

    private ActivityCompanyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbarCompany);
        toolbar.setPadding(androidx.appcompat.R.styleable.Toolbar_titleMarginStart, 0, 0, 0);
        toolbar.setTitle("Ifood- company");
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_company, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuSearch) {

            return true;
        }

        if (id == R.id.menuSettings) {

            return true;
        }

        if (id == R.id.menuLogout) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


