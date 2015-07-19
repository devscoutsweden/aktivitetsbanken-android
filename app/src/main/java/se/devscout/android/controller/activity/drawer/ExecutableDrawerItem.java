package se.devscout.android.controller.activity.drawer;

import android.app.Activity;
import android.content.Intent;
import se.devscout.android.controller.activity.FragmentCreator;
import se.devscout.android.controller.activity.FragmentCreatorActivity;

public abstract class ExecutableDrawerItem extends DefaultDrawerItem implements FragmentCreator {

    ExecutableDrawerItem(String title, int iconResIdesId) {
        super(title, iconResIdesId);
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    public void run(Activity ctx) {
        Intent intent = FragmentCreatorActivity.createIntent(ctx, this, getActivityTitle());
        ctx.startActivity(intent);
    }

    String getActivityTitle() {
        return getTitle();
    }

}
