package com.example.loanemi.Activities.introflow;

import androidx.fragment.app.Fragment;

public abstract class LoanLazyFragment extends Fragment {
    private boolean _wasVisible = false;

    public void onFirstVisible() {
        this._wasVisible = true;
    }

    public void onVisibilityChange(boolean z) {
        throw new UnsupportedOperationException("method not overridden");
    }

    public void setUserVisibleHint(boolean z) {
        if (getActivity() != null) {
            if (!this._wasVisible) {
                onFirstVisible();
            }
            try {
                onVisibilityChange(z);
            } catch (UnsupportedOperationException unused) {
                unused.printStackTrace();
            }
        }
    }
}
