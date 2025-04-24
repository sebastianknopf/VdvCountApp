package de.vdvcount.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.util.UUID;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.ActivityAppBinding;

public class AppActivity extends AppCompatActivity {

    private ActivityAppBinding dataBinding;

    private NavController navigationController;

    private int actionBarTapCount = 0;
    private Toast actionBarTapCountToast = null;
    private Runnable actionBarTapCountResetRunnable;
    private Handler actionBarTapCountResetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.actionBarTapCount = 0;
        this.actionBarTapCountResetRunnable = () -> {
            this.actionBarTapCount = 0;
        };
        this.actionBarTapCountResetHandler = new Handler();

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_app);
        this.setSupportActionBar(this.dataBinding.toolbar);

        this.navigationController = Navigation.findNavController(this, R.id.nav_host_fragment);

        this.initViewEvents();
    }

    public NavController getNavigationController() {
        return this.navigationController;
    }

    public void initViewEvents() {
        this.dataBinding.toolbar.setOnClickListener(view -> {
            this.actionBarTapCount++;

            if (this.actionBarTapCountToast != null) {
                this.actionBarTapCountToast.cancel();
            }

            if (this.actionBarTapCount == 1) {
                this.actionBarTapCountResetHandler.postDelayed(this.actionBarTapCountResetRunnable, 5000);
            } else if (this.actionBarTapCount >= 5 && this.actionBarTapCount < 10) {
                this.actionBarTapCountToast = Toast.makeText(this, this.getString(R.string.app_reset_counter, 10 - this.actionBarTapCount), Toast.LENGTH_SHORT);
                this.actionBarTapCountToast.show();
            } else if (this.actionBarTapCount >= 10) {
                Status.setString(Status.STATUS, Status.Values.INITIAL);

                try {
                    Secret.setSecretString(Secret.DEVICE_ID, null);
                    Secret.setSecretString(Secret.API_ENDPOINT, null);
                    Secret.setSecretString(Secret.API_USERNAME, null);
                    Secret.setSecretString(Secret.API_PASSWORD, null);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                }

                this.finishAffinity();
            }
        });
    }
}