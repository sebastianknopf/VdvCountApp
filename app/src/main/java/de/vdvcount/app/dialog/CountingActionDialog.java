package de.vdvcount.app.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import de.vdvcount.app.R;
import de.vdvcount.app.databinding.DialogCountingActionBinding;

public class CountingActionDialog {

    private Context context;
    private DialogCountingActionBinding dataBinding;

    private View.OnClickListener onActionCountingClickListener;
    private View.OnClickListener onActionAdditionalStopClickListener;
    private View.OnClickListener onActionRunThroughListener;

    private boolean secondRunThroughClick;
    private Runnable runThroughResetRunnable;
    private Handler runThroughResetHandler;

    public CountingActionDialog(Context context) {
        this.context = context;

        this.runThroughResetRunnable = () -> {
            this.secondRunThroughClick = false;
            this.dataBinding.layoutActionRunThrough.setBackgroundResource(this.getThemeResource(this.context, androidx.appcompat.R.attr.selectableItemBackground));
        };

        this.runThroughResetHandler = new Handler();
    }

    public void setOnActionCountingClickListener(View.OnClickListener listener) {
        this.onActionCountingClickListener = listener;
    }

    public void setOnActionAdditionalStopClickListener(View.OnClickListener listener) {
        this.onActionAdditionalStopClickListener = listener;
    }

    public void setOnActionRunThroughListener(View.OnClickListener listener) {
        this.onActionRunThroughListener = listener;
    }

    public void show() {
        this.dataBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.dialog_counting_action, null, false);

        final AlertDialog alertDialog = new AlertDialog.Builder(this.context)
                .setView(this.dataBinding.getRoot())
                .setCancelable(true)
                .create();

        if (this.onActionCountingClickListener != null) {
            this.dataBinding.layoutActionCount.setVisibility(View.VISIBLE);
            this.dataBinding.layoutActionCount.setOnClickListener(view -> {
                alertDialog.hide();

                this.onActionCountingClickListener.onClick(view);
            });
        } else {
            this.dataBinding.layoutActionCount.setVisibility(View.GONE);
        }

        if (this.onActionAdditionalStopClickListener != null) {
            this.dataBinding.layoutActionAdditionalStop.setVisibility(View.VISIBLE);
            this.dataBinding.layoutActionAdditionalStop.setOnClickListener(view -> {
                alertDialog.hide();

                this.onActionAdditionalStopClickListener.onClick(view);
            });
        } else {
            this.dataBinding.layoutActionAdditionalStop.setVisibility(View.GONE);
        }

        if (this.onActionRunThroughListener != null) {
            this.dataBinding.layoutActionRunThrough.setVisibility(View.VISIBLE);
            this.dataBinding.layoutActionRunThrough.setOnClickListener(view -> {
                if (!this.secondRunThroughClick) {
                    this.secondRunThroughClick = true;
                    this.runThroughResetHandler.postDelayed(this.runThroughResetRunnable, 2000);

                    this.dataBinding.layoutActionRunThrough.setBackgroundColor(this.getThemeColor(this.context, androidx.appcompat.R.attr.colorPrimary));
                } else {
                    this.runThroughResetHandler.removeCallbacks(this.runThroughResetRunnable);

                    alertDialog.hide();

                    this.onActionRunThroughListener.onClick(view);
                }
            });
        } else {
            this.dataBinding.layoutActionRunThrough.setVisibility(View.GONE);
        }

        alertDialog.show();
    }

    public int getThemeColor(Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        if (theme.resolveAttribute(attrResId, typedValue, true)) {
            if (typedValue.resourceId != 0) {
                return ContextCompat.getColor(context, typedValue.resourceId);
            } else {
                return typedValue.data;
            }
        }

        return Color.TRANSPARENT;
    }

    public int getThemeResource(Context context, int attrResId) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        if(theme.resolveAttribute(attrResId, outValue, true)) {
            if (outValue.resourceId != 0) {
                return outValue.resourceId;
            } else {
                return outValue.data;
            }
        }

        throw new Resources.NotFoundException(String.format("could not resolve attrResId %d", attrResId));
    }
}
