package udemy.java.desenvolvimento.android.completo.ifood_clone.utilities;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import udemy.java.desenvolvimento.android.completo.ifood_clone.R;

public class SystemUi {
    private int uiOptions;
   private final Activity activity;

   public SystemUi(Activity activity) {
       this.activity = activity;
   }

    public void hideSystemUIFullScream(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                EdgeToEdge.enable((ComponentActivity) this.activity);
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars() | WindowInsets.Type.systemBars());
                controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {

            activity.getWindow().getDecorView().getSystemUiVisibility();
            uiOptions = ( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }
    public void hideSystemUIHideNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            WindowInsetsController controller = activity.getWindow().getInsetsController();
            if (controller != null) {
                controller.hide( WindowInsets.Type.navigationBars() );
                controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else {

            activity.getWindow().getDecorView().getSystemUiVisibility();
            uiOptions = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            ); // Enable immersive mode
            activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);

            Window window = activity.getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.c_red_devil_100));
        }
    }
}
