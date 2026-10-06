package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.utils.Util;

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