package de.vdvcount.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import de.vdvcount.app.ui.permission.PermissionFragment;

public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PermissionFragment.newInstance())
                    .commitNow();
        }
    }
}