package udemy.java.desenvolvimento.android.completo.ifood_clone.utilities;

import android.app.Dialog;
import android.content.Context;

import android.os.Handler;


import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.DialogProgressBinding;

public class ProgressDialog {

    private final Dialog progressDialog;
    private final DialogProgressBinding binding;

    public ProgressDialog(Context context) {
        progressDialog = new Dialog(context);
        binding = DialogProgressBinding.inflate(progressDialog.getLayoutInflater());
        progressDialog.setContentView(binding.getRoot());
        progressDialog.setCancelable(false);
        int delayMillis = 7000; // Change this value to set a different duration
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
           // Call the dismiss method to close the dialog
        }, delayMillis);

    }


    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            binding.progressDialogTextViewTitle.setText(R.string.loading);
            progressDialog.show();
        }
    }

}
