package udemy.java.desenvolvimento.android.completo.ifood_clone.helper;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;


public class UserFirebase {

    private static FirebaseAuth auth;

    public static String getUserId(){

        auth = FirebaseConfiguration.getFirebaseAuthentication();
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();

    }

    public static FirebaseUser getLoggedUser(){
        auth = FirebaseConfiguration.getFirebaseAuthentication();
        return auth.getCurrentUser();
    }


    public void logoutUser(){
        try {
            auth = FirebaseConfiguration.getFirebaseAuthentication();
            auth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
