package de.vdvcount.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.util.Map;
import java.util.UUID;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.vdvcount.app.common.Logging;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.common.Status;
import de.vdvcount.app.databinding.ActivityAppBinding;
import de.vdvcount.app.remote.RemoteRepository;
import de.vdvcount.app.security.Cipher;

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

        Status.setBoolean(Status.TERMINATED, false);
        Logging.i(this.getClass().getName(), "Application starting");

        this.actionBarTapCount = 0;
        this.actionBarTapCountResetRunnable = () -> {
            this.actionBarTapCount = 0;
        };
        this.actionBarTapCountResetHandler = new Handler();

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_app);
        this.setSupportActionBar(this.dataBinding.toolbar);

        this.navigationController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // generate device ID if not already existing
        // device ID is null on very first start and "" on every start after reset
        // see #31 for more information
        String deviceId = Secret.getSecretString(Secret.DEVICE_ID, null);
        if (deviceId == null || deviceId.isEmpty()) {
            Cipher.generateSecretKey(Cipher.DEFAULT_KEY);

            Secret.setSecretString(Secret.DEVICE_ID, UUID.randomUUID().toString());
        }

        // check former app termination state
        boolean terminated = Status.getBoolean(Status.TERMINATED, false);
        if (!terminated && !Status.getString(Status.STATUS, Status.Values.INITIAL).equals(Status.Values.INITIAL)) {
            Logging.i(this.getClass().getName(), "Application was killed by user or system before");

            this.sendLogs();
        }

        this.initViewEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Status.setBoolean(Status.TERMINATED, true);
        Logging.i(this.getClass().getName(), "Application terminating");

        if (!Status.getString(Status.STATUS, Status.Values.INITIAL).equals(Status.Values.INITIAL)) {
            this.sendLogs();
        }
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

                // set device ID to "" in order to mark the state as "reset" for the next app startup
                // see more information in #31
                Secret.setSecretString(Secret.DEVICE_ID, "");

                Secret.setSecretString(Secret.API_ENDPOINT, "");
                Secret.setSecretString(Secret.API_USERNAME, "");
                Secret.setSecretString(Secret.API_PASSWORD, "");

                Status.setInt(Status.CURRENT_STATION_ID, -1);
                Status.setString(Status.CURRENT_STATION_NAME, null);
                Status.setInt(Status.CURRENT_TRIP_ID, -1);
                Status.setInt(Status.CURRENT_START_STOP_SEQUENCE, -1);
                Status.setString(Status.CURRENT_VEHICLE_ID, null);
                Status.setStringArray(Status.CURRENT_COUNTED_DOOR_IDS, new String[] {});

                this.finish();
            }
        });
    }

    public void sendLogs() {
        Runnable runnable = () -> {
            try {
                Logging.i(this.getClass().getName(), "Sending logs to remote server");
                Logging.archiveCurrentLogs();

                RemoteRepository repository = RemoteRepository.getInstance();
                Map<String, String> archivedLogs = Logging.getArchivedLogs();
                for (Map.Entry<String, String> archivedLog : archivedLogs.entrySet()) {
                    if (repository.postLogs(archivedLog.getValue())) {
                        Logging.removeArchivedLog(archivedLog.getKey());
                    }
                }
            } catch (Exception ex) {
                Logging.e(this.getClass().getName(), "Failed to send logs to remote API", ex);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }
}