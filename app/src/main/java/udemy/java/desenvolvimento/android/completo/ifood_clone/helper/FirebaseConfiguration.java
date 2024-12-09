package udemy.java.desenvolvimento.android.completo.ifood_clone.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FirebaseConfiguration {
    private static DatabaseReference firebaseReference;
    private static FirebaseAuth authenticationReference;
    private static StorageReference storageReference;


    //retorna a referencia do database
    public static DatabaseReference getFirebaseDatabase(){
        if( firebaseReference == null ){
            firebaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseReference;
    }

    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAuthentication(){
        if( authenticationReference == null ){
            authenticationReference = FirebaseAuth.getInstance();
        }
        return authenticationReference;
    }

    //Retorna instancia do FirebaseStorage
    public static StorageReference getFirebaseStorage(){
        if( storageReference == null ){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }


}
