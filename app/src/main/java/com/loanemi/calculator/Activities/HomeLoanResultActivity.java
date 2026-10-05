package com.loanemi.calculator.Activities;

import static com.loanemi.calculator.utils.Util.setupEdgeToEdge;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loanemi.R;
import com.loanemi.calculator.utils.Util;

public class HomeLoanResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_loan_result);
        setupEdgeToEdge(this, R.id.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.hide(this);
    }
}