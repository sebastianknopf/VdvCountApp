package de.vdvcount.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import de.vdvcount.app.databinding.ActivityAppBinding;

public class AppActivity extends AppCompatActivity {

    private ActivityAppBinding dataBinding;

    private NavController navigationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_app);
        this.setSupportActionBar(this.dataBinding.toolbar);

        this.navigationController = Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    public NavController getNavigationController() {
        return this.navigationController;
    }
}