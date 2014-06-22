package se.devscout.android.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import se.devscout.android.R;

public class StartWidgetView extends RelativeLayout {
    private int mTitleTextId;

    public StartWidgetView(Context context, int titleTextId) {
        super(context);
        mTitleTextId = titleTextId;
        init(context);
    }

    public StartWidgetView(Context context, AttributeSet attrs, int titleTextId) {
        super(context, attrs);
        mTitleTextId = titleTextId;
        init(context);
    }

    public StartWidgetView(Context context, AttributeSet attrs, int defStyle, int titleTextId) {
        super(context, attrs, defStyle);
        mTitleTextId = titleTextId;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.start_widget_container, this, true);
        if (mTitleTextId > 0) {
            final TextView title = (TextView) findViewById(R.id.textView);
            title.setText(mTitleTextId);
        } else {
            findViewById(R.id.linearLayout).setVisibility(GONE);
        }
    }

    public void setContentView(View view) {
        LinearLayout widgetContentList = (LinearLayout) findViewById(R.id.start_widget_container);
        widgetContentList.removeAllViews();
        widgetContentList.addView(view);
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

    public void onResume() {
        LinearLayout widgetContentList = (LinearLayout) findViewById(R.id.start_widget_container);

        //TODO: It is good to assume the first child is the one we want?
        View view = widgetContentList.getChildAt(0);
        if (view instanceof NonBlockingSearchView) {
            NonBlockingSearchView nonBlockingSearchView = (NonBlockingSearchView) view;
            if (!nonBlockingSearchView.isResultPresent()) {
                nonBlockingSearchView.createSearchTask().execute();
            }
        }
    }
}
