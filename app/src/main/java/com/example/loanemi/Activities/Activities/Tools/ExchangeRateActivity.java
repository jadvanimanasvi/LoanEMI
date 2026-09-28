package com.example.loanemi.Activities.Activities.Tools;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;

public class ExchangeRateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);
        setupEdgeToEdge(this, R.id.main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.hide(this);
    }
}