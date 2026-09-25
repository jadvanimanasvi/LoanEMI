package com.example.loanemi.Activities.Activities;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loanemi.R;

public class HomeLoanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_loan);
        setupEdgeToEdge(this, R.id.main);
    }
}