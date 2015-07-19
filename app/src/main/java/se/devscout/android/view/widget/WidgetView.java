package se.devscout.android.view.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.view.ActivitiesListView;

public class WidgetView extends LinearLayout implements FragmentListener {
    private int mTitleTextId;

    public WidgetView(Context context, int titleTextId) {
        super(context);
        mTitleTextId = titleTextId;
        init(context);
    }

    public WidgetView(Context context, AttributeSet attrs, int titleTextId) {
        super(context, attrs);
        mTitleTextId = titleTextId;
        init(context);
    }

    public WidgetView(Context context, AttributeSet attrs, int defStyle, int titleTextId) {
        super(context, attrs, defStyle);
        mTitleTextId = titleTextId;
        init(context);
    }

    public WidgetView(Context context, int titleTextId, View contentView) {
        this(context, titleTextId);
        setContentView(contentView);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.widget_container, this, true);
        final TextView title = (TextView) findViewById(R.id.textView);
        if (mTitleTextId > 0) {
            title.setText(mTitleTextId);
        } else {
            ((LinearLayout) findViewById(R.id.start_widget)).removeView(title);
        }
    }

    public void setContentView(View view) {
        int padding = getResources().getDimensionPixelSize(R.dimen.iconButtonMargin);
        view.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LinearLayout container = (LinearLayout) findViewById(R.id.start_widget);
        container.addView(view);
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mTitleTextId", mTitleTextId);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mTitleTextId = bundle.getInt("mTitleTextId");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onFragmentResume(boolean refreshResultOnResume) {
        LinearLayout widgetContentList = (LinearLayout) findViewById(R.id.start_widget);
        View view = widgetContentList.getChildAt(widgetContentList.getChildCount() - 1);
        if (view instanceof FragmentListener) {
            FragmentListener fragmentListener = (FragmentListener) view;
            fragmentListener.onFragmentResume(refreshResultOnResume);
        }

        if (view instanceof ActivitiesListView) {
            ActivitiesListView activitiesListView = (ActivitiesListView) view;
            if (refreshResultOnResume || !activitiesListView.isResultPresent()) {
                activitiesListView.runSearchTaskInNewThread();
            }
        }

    }
}
