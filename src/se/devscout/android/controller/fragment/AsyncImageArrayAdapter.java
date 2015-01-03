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
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.android.view.AsyncImageBean;
import se.devscout.android.view.AsyncImageView;

import java.util.ArrayList;
import java.util.List;

public class AsyncImageArrayAdapter extends ArrayAdapter<AsyncImageBean> {

    public AsyncImageArrayAdapter(Context context, List<AsyncImageBean> result) {
        super(context, R.layout.simple_list_item_1, result);
    }

    public static ArrayAdapter fromList(List<ActivitiesListItem> result, Context context) {
        List<AsyncImageBean> props = new ArrayList<AsyncImageBean>();
        for (ActivitiesListItem item : result) {
            props.add(new AsyncImageBean(item.getName(), ResourceUtil.getFullScreenMediaURIs(item.getCoverMedia(), context)));
        }
        return new AsyncImageArrayAdapter(context, props);
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
        AsyncImageBean item = getItem(position);


        view.setImage(item, ((SingleFragmentActivity) getContext()).getBackgroundTasksHandlerThread());
        view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.thumbnail_height)));
        return view;
    }


}
