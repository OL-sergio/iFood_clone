package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivitySettingUserBinding;

public class SettingUserActivity extends AppCompatActivity {

    private ActivitySettingUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        components();

    }

    private void setupToolbar() {
        MaterialToolbar toolbar  = binding.toolbar.toolbarTitle;
        toolbar.setTitle("Set Setting User");
        toolbar.setNavigationContentDescription("Back");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);
    }

    private void components() {
        Snackbar.make(findViewById(R.id.main), "Setting User", Snackbar.LENGTH_LONG).show();
    }
}