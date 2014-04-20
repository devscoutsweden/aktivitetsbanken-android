package se.devscout.android.controller.activity;

import android.support.v4.app.Fragment;
import se.devscout.android.controller.fragment.StartViewPagerFragment;

public class StartActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new StartViewPagerFragment();
    }
}