package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}