package de.vdvcount.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import de.vdvcount.app.R;
import de.vdvcount.app.databinding.DialogGpsWarningBinding;

public class GpsWarningDialog {

    private Context context;
    private DialogGpsWarningBinding dataBinding;

    public GpsWarningDialog(Context context) {
        this.context = context;
    }

    public void show() {
        this.dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.dialog_gps_warning, null, false);

        final AlertDialog alertDialog = new AlertDialog.Builder(this.context)
                .setView(this.dataBinding.getRoot())
                .setCancelable(true)
                .create();

        alertDialog.show();
    }
}
