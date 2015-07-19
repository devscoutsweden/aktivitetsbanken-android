package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import se.devscout.android.R;
import se.devscout.android.model.ActivityKey;

import java.util.List;

public class RelatedActivitiesView extends LinearLayout {
    private ActivityKey mActivityKey;
    private List<? extends ActivityKey> mRelatedActivitiesKeys;

    public RelatedActivitiesView(Context context, ActivityKey activityKey, List<? extends ActivityKey> relatedActivitiesKeys) {
        super(context);
        mActivityKey = activityKey;
        mRelatedActivitiesKeys = relatedActivitiesKeys;
        init(context);
    }

    public RelatedActivitiesView(Context context, AttributeSet attrs, ActivityKey activityKey, List<ActivityKey> relatedActivitiesKeys) {
        super(context, attrs);
        mActivityKey = activityKey;
        mRelatedActivitiesKeys = relatedActivitiesKeys;
        init(context);
    }

    public RelatedActivitiesView(Context context, AttributeSet attrs, int defStyle, ActivityKey activityKey, List<ActivityKey> relatedActivitiesKeys) {
        super(context, attrs, defStyle);
        mActivityKey = activityKey;
        mRelatedActivitiesKeys = relatedActivitiesKeys;
        init(context);
    }

    private void init(final Context context) {
        final LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_related_card, this, true);

        final Button relButton = (Button) view.findViewById(R.id.showRelatedActivitiesButton);
        if (relButton != null) {
            // Button will be null after clicking it once since clicking it will also remove it.
            relButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    LinearLayout container = (LinearLayout) view.findViewById(R.id.showRelatedActivitiesContainer);
                    container.removeView(clickedView);
                    final ActivitiesListView relatedActivitiesView = new RelatedActivitiesListView(context, mActivityKey, mRelatedActivitiesKeys);
                    container.addView(relatedActivitiesView);
                    relatedActivitiesView.runSearchTaskInNewThread();
                }
            });
        }
    }

}
