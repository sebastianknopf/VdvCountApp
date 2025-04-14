package de.vdvcount.app.adapter;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.databinding.BindingAdapter;

public final class BindingAdapters {

    @BindingAdapter(value = {"date", "dateFormat"}, requireAll = false)
    public static void setFormattedDate(TextView view, Date date, String format) {
        if (date == null) {
            view.setText("");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format != null ? format : "dd.MM.yyyy HH:mm", Locale.getDefault());
        view.setText(sdf.format(date));
    }
}
