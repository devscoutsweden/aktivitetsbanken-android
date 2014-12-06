package se.devscout.android.controller.fragment;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.android.view.AsyncImageView;

import java.util.List;

public class AsyncImageArrayAdapter extends ArrayAdapter<ActivitiesListItem> {

    public AsyncImageArrayAdapter(Context context, List<ActivitiesListItem> result) {
        super(context, R.layout.simple_list_item_1, result);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AsyncImageView view = null;
        // TODO: Other getView should perhaps initialize components using a simiar if statement
        if (convertView == null) {
            view = new AsyncImageView(getContext());
            if (parent instanceof ListView) {
                ListView listView = (ListView) parent;
                listView.setDividerHeight(getContext().getResources().getDimensionPixelSize(se.devscout.android.R.dimen.defaultMargin));
            } else if (parent instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) parent;
                layout.setDividerDrawable(getContext().getResources().getDrawable(se.devscout.android.R.drawable.list_item_padding));
            }
        } else if (convertView instanceof AsyncImageView) {
            view = (AsyncImageView) convertView;
        }
        ActivitiesListItem item = getItem(position);


        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        view.init(item.getCoverMedia(), item.getName(), (SingleFragmentActivity) getContext(), screenWidth);
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.thumbnail_height)));
        return view;
    }


}
