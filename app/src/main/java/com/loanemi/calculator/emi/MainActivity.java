package com.loanemi.calculator.emi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.loanemi.calculator.emi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loanemi.calculator.emi.fragments.HistoryFragment;
import com.loanemi.calculator.emi.fragments.HomeFragment;
import com.loanemi.calculator.emi.fragments.SettingFragment;
import com.loanemi.calculator.emi.fragments.ToolsFragment;
import com.loanemi.calculator.emi.utils.Util;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            // Notification permission granted
                        } else {
                            // Permission denied
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.hide(this);
        setupWindowInsets();
        initViews();
        setupBottomNavigation();
        requestNotificationPermission();
        // Open HomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void requestNotificationPermission() {

        // Android 13+ only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );
            }
        }
    }

    private void setupWindowInsets() {
        View root = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_tools) {
                loadFragment(new ToolsFragment());
                return true;
            } else if (id == R.id.nav_history) {
                 loadFragment(new HistoryFragment());
                return true;
            } else if (id == R.id.nav_setting) {
                loadFragment(new SettingFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void selectHomeTab() {
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}