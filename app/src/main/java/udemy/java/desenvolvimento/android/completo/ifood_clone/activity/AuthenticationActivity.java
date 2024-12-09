package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityAuthenticationBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.Constants;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.FirebaseConfiguration;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;
import udemy.java.desenvolvimento.android.completo.ifood_clone.model.User;


public class AuthenticationActivity extends AppCompatActivity {

    private ActivityAuthenticationBinding binding;

    private Button acessButton;
    private EditText textEmail, textPassword, textName;
    private SwitchCompat createAccount, accessUserType;
    private LinearLayout linearUserType;
    private FirebaseAuth auth;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();
        components();

        auth = FirebaseConfiguration.getFirebaseAuthentication();

        verificationUserLogged();

        createAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked ){//Empresa
                    linearUserType.setVisibility(View.VISIBLE);
                    textName.setVisibility(View.VISIBLE);
                }else {//Cliente
                    linearUserType.setVisibility(View.GONE);
                    textName.setVisibility(View.GONE);
                }
            }
        });


        acessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameText = textName.getText().toString();
                String emailText = textEmail.getText().toString();
                String passwordText = textPassword.getText().toString();

                if ( !nameText.isEmpty() ) {
                    if ( !emailText.isEmpty() ){
                        if ( !passwordText.isEmpty() ){

                            //Verifica o  estado do switch
                            if( createAccount.isChecked() ){//Register

                                auth.createUserWithEmailAndPassword(
                                        emailText, passwordText
                                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()){

                                            Toast.makeText(AuthenticationActivity.this, R.string.registro_de_utlizador_realizado_com_sucesso,
                                                    Toast.LENGTH_SHORT).show();

                                            User users = new User();
                                            String userType = getUserType();
                                            UserFirebase.updateUserType( userType );
                                            openMainView(userType);

                                            String userID = UserFirebase.getUserId();
                                            users.setUserId(userID);
                                            users.setName(nameText);
                                            users.setEmail(emailText);
                                            users.setUserType(userType);
                                            users.saveUser();

                                        }else {

                                            String exceptionError = "";

                                            try{
                                                throw Objects.requireNonNull(task.getException());
                                            }catch (FirebaseAuthWeakPasswordException e){
                                                exceptionError = getString(R.string.digite_uma_senha_mais_forte);
                                            }catch (FirebaseAuthInvalidCredentialsException e){
                                                exceptionError = getString(R.string.por_favor_digite_um_e_mail_v_lido);
                                            }catch (FirebaseAuthUserCollisionException e){
                                                exceptionError = getString(R.string.este_conta_j_registada);
                                            } catch (Exception e) {
                                                exceptionError = getString(R.string.ao_registar_usu_rio)  + e.getMessage();
                                                e.printStackTrace();
                                            }

                                            Toast.makeText(AuthenticationActivity.this,
                                                    "Erro: " + exceptionError ,
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }else {//Login

                                auth.signInWithEmailAndPassword(
                                        emailText,  passwordText
                                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){

                                            Toast.makeText(AuthenticationActivity.this,
                                                    getString(R.string.logado_com_sucesso),
                                                    Toast.LENGTH_SHORT).show();
                                            String userType = task.getResult().getUser().getDisplayName();
                                            openMainView(userType);

                                        }else {
                                            Toast.makeText(AuthenticationActivity.this,
                                                    getString(R.string.erro_ao_fazer_login) + task.getException() ,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }else {
                            Toast.makeText(AuthenticationActivity.this,
                                    R.string.preencha_a_senha,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(AuthenticationActivity.this,
                                R.string.preencha_o_e_mail,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AuthenticationActivity.this,
                            R.string.preencha_o_nome,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String getUserType() {
        return accessUserType.isChecked() ? Constants.COMPANY : Constants.USER_TYPE;
    }

    private void openMainView( String userType ) {
        if ( userType.equals(Constants.COMPANY) ){
            startActivity(new Intent(getApplicationContext(), CompanyActivity.class));
            finish();
        }else {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    private void verificationUserLogged() {

        FirebaseUser userLogged = auth.getCurrentUser();
        if (userLogged != null) {
            String userType = userLogged.getDisplayName();
            if (userType != null) {
                openMainView(userType);
            } else {
                Log.d("ERRO", "Error retrieving user type in user");
            }
        } else {
            Log.d("ERRO", "Error retrieving logged in user");
        }
    }

    private void components() {

            acessButton = binding.buttonAccessAccount;
            textName = binding.editTextName;
            textEmail = binding.editTextEmail;
            textPassword = binding.editTextPassword;
            createAccount = binding.switchCreateAccount;
            accessUserType = binding.switchAccessCompanyType;
            linearUserType = binding.linearLayoutUserType;

    }
}