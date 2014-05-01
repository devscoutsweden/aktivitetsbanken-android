package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import se.devscout.android.R;

abstract class SingleFragmentActivity<T extends Fragment> extends FragmentActivity {

    protected T mFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        FragmentManager fm = getSupportFragmentManager();

        mFragment = (T) fm.findFragmentById(R.id.emptyContainer);
        if (mFragment == null) {
            mFragment = createFragment();
            fm.beginTransaction().add(R.id.emptyContainer, mFragment).commit();
        }

        if (savedInstanceState != null) {
            Fragment.SavedState state = (Fragment.SavedState) savedInstanceState.getParcelable("state");
            if (state != null) {
                mFragment.setInitialSavedState(state);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.emptyContainer);
        if (fragment == null) {
            Fragment.SavedState state = fm.saveFragmentInstanceState(fragment);
            outState.putParcelable("state", state);
        }
    }

    protected abstract T createFragment();
}
