package se.devscout.android.controller.activity.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.ScoutTypeFace;

import java.util.ArrayList;
import java.util.List;

public class DrawerListAdapter extends BaseAdapter {

    private final static int TYPE_HEADER = 0;
    private final static int TYPE_ITEM = 1;

    private List<DrawerItem> mDrawerItems;
    private Context mContext;

    public DrawerListAdapter(Context context) {
        this(context, new ArrayList<DrawerItem>());
    }

    public DrawerListAdapter(Context context, List<DrawerItem> drawerItems) {
        mContext = context;
        mDrawerItems = drawerItems;
    }

    public void add(DrawerItem... items) {
        for (DrawerItem item : items) {
            mDrawerItems.add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isHeader = mDrawerItems.get(position).isHeader();
        return isHeader ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public DrawerItem getItem(int i) {
        return mDrawerItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_HEADER) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.document_headertextview, null);
                convertView.setEnabled(false);
                convertView.setOnClickListener(null);
            }
            ((TextView) convertView).setText(getItem(position).getTitle());
            ((TextView) convertView).setTypeface(ScoutTypeFace.getInstance(convertView.getContext()).getLight());
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_list_item, null);
            }
            ((TextView) convertView.findViewById(R.id.drawer_list_item_label)).setText(getItem(position).getTitle());
            int resourceId = getItem(position).getIconResourceId();
            if (resourceId > 0) {
                ((ImageView) convertView.findViewById(R.id.drawer_list_item_icon)).setImageDrawable(mContext.getResources().getDrawable(resourceId));
            } else {
                ((ImageView) convertView.findViewById(R.id.drawer_list_item_icon)).setImageDrawable(null);
            }
        }
        return convertView;
    }
}
