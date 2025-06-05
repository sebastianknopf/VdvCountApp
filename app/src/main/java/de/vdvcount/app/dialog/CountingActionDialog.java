package de.vdvcount.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.databinding.DataBindingUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import de.vdvcount.app.R;
import de.vdvcount.app.databinding.DialogCountingActionBinding;

public class CountingActionDialog {

    private Context context;

    private View.OnClickListener onActionCountingClickListener;
    private View.OnClickListener onActionAdditionalStopClickListener;

    public CountingActionDialog(Context context) {
        this.context = context;
    }

    public void setOnActionCountingClickListener(View.OnClickListener listener) {
        this.onActionCountingClickListener = listener;
    }

    public void setOnActionAdditionalStopClickListener(View.OnClickListener listener) {
        this.onActionAdditionalStopClickListener = listener;
    }

    public void show() {
        DialogCountingActionBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.dialog_counting_action, null, false);

        final AlertDialog alertDialog = new AlertDialog.Builder(this.context)
                .setView(dataBinding.getRoot())
                .setCancelable(true)
                .create();

        if (this.onActionCountingClickListener != null) {
            dataBinding.layoutActionCount.setVisibility(View.VISIBLE);
            dataBinding.layoutActionCount.setOnClickListener(view -> {
                alertDialog.hide();

                this.onActionCountingClickListener.onClick(view);
            });
        } else {
            dataBinding.layoutActionCount.setVisibility(View.GONE);
        }

        if (this.onActionAdditionalStopClickListener != null) {
            dataBinding.layoutActionAdditionalStop.setVisibility(View.VISIBLE);
            dataBinding.layoutActionAdditionalStop.setOnClickListener(view -> {
                alertDialog.hide();

                this.onActionAdditionalStopClickListener.onClick(view);
            });
        } else {
            dataBinding.layoutActionAdditionalStop.setVisibility(View.GONE);
        }

        alertDialog.show();
    }
}
