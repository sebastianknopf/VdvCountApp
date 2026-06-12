package de.vdvcount.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.databinding.ActivityAppInfoBinding;

public class AppInfoActivity extends AppCompatActivity {

    private ActivityAppInfoBinding dataBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logging.i(this.getClass().getName(), "App info activity opened");

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_info);
        this.setSupportActionBar(this.dataBinding.toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.initViewData();
        this.initViewEvents();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Logging.i(this.getClass().getName(), "App info activity closed");
    }

    private void initViewData() {
        this.dataBinding.lblAppVersion.setText(BuildConfig.VERSION_NAME);
        this.dataBinding.lblDeviceId.setText(Secret.getSecretString(Secret.DEVICE_ID, null));
    }

    private void initViewEvents() {
        this.dataBinding.btnSendLogs.setOnClickListener(view -> {
            Logging.sendLogs();
        });
    }
}