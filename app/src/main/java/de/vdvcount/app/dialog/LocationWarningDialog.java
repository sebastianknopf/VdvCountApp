package de.vdvcount.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import de.vdvcount.app.R;
import de.vdvcount.app.databinding.DialogLocationWarningBinding;

public class LocationWarningDialog {

    private Context context;
    private DialogLocationWarningBinding dataBinding;
    private AlertDialog alertDialog;

    public LocationWarningDialog(Context context) {
        this.context = context;

        this.dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.dialog_location_warning, null, false);

        this.alertDialog = new AlertDialog.Builder(this.context)
                .setView(this.dataBinding.getRoot())
                .setCancelable(false)
                .create();
    }

    public void show() {
        if (this.alertDialog != null) {
            this.alertDialog.show();
        }
    }

    public void hide() {
        if (this.alertDialog != null) {
            this.alertDialog.dismiss();
        }
    }
}
