package com.example.loanemi.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.loanemi.Activities.fragments.HistoryFragment;
import com.example.loanemi.Activities.fragments.HomeFragment;
import com.example.loanemi.Activities.fragments.ToolsFragment;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.hide(this);
        setupWindowInsets();
        initViews();
        setupBottomNavigation();

        // Open HomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
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
                // loadFragment(new SettingFragment());
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
}