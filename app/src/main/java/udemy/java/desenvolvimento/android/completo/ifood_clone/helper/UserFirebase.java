package udemy.java.desenvolvimento.android.completo.ifood_clone.helper;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class UserFirebase {

    public static String getUserId(){

        FirebaseAuth auth = FirebaseConfiguration.getFirebaseAuthentication();
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();

    }

    public static FirebaseUser getLoggedUser(){
        FirebaseAuth auth = FirebaseConfiguration.getFirebaseAuthentication();
        return auth.getCurrentUser();
    }

    public static boolean updateUserType(String type){

            try {

                FirebaseUser user = getLoggedUser();
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(type)
                        .build();
                user.updateProfile(profile);

                return true;

            } catch (Exception e) {

                return false;

            }
    }
}
