package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityIntroBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.utilities.SystemUi;


public class IntroActivity extends AppCompatActivity {

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityIntroBinding binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SystemUi sysTemUi = new SystemUi(this);
        sysTemUi.hideSystemUIFullScream();

        auth = FirebaseConfiguration.getFirebaseAuthentication();
        //auth.signOut();

        Handler handler = new Handler();
        handler.postDelayed(
                this::openAuthentication, 2000
        );
    }
    private void openAuthentication() {
        startActivity(new Intent(IntroActivity.this, AuthenticationActivity.class));
        finish();
    }
}