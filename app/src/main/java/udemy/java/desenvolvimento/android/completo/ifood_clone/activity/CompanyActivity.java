package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;

import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityCompanyBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;


public class CompanyActivity extends AppCompatActivity {

    private ActivityCompanyBinding binding;

    private UserFirebase userFirebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        components();

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
        if (id == R.id.menuAddItem) {
            addNewItem();
            return true;
        }

        if (id == R.id.menuSettingsCompany) {
            settingUser();
            return true;
        }

        if (id == R.id.menuLogout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewItem() {
       changeActivity(NewItemCompanyActivity.class);
    }

    private void settingUser() {
        changeActivity(SettingCompanyActivity.class );
    }

    private void changeActivity(Class<? extends Activity> activity) {
        startActivity(new Intent(this, activity));
    }

    private void logoutUser() {
        userFirebase.logoutUser();
        startActivity(new Intent(this, AuthenticationActivity.class));
        finish();
    }

    private void components() {

        Toolbar toolbar = binding.toolbar.toolbarCompany;
        toolbar.setTitle("Ifood- company");
        toolbar.setPadding(28, 12, 0, 12);
        setSupportActionBar(toolbar);

        userFirebase = new UserFirebase();

    }
}


