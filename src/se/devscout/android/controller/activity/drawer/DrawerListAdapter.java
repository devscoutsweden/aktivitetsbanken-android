package se.devscout.android.controller.activity.drawer;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.TitleActivityFilterVisitor;
import se.devscout.android.model.SearchHistory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.server.api.ActivityBank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DrawerListAdapter extends BaseAdapter {

    private final static int TYPE_HEADER = 0;
    private final static int TYPE_ITEM = 1;
    private static final int SEARCH_HISTORY_ITEM_COUNT = 3;
    private final TitleActivityFilterVisitor SEARCH_HISTORY_ITEM_TITLE_VISITOR;

    private final List<DrawerItem> mDrawerItems;
    private List<DrawerItem> mSearchHistoryDrawerItems;
    private final Context mContext;
    private HeaderDrawerItem mSearchHistoryHeaderDrawerItem;
    private ActivityBank mActivityBank;

    public DrawerListAdapter(Context context) {
        this(context, new ArrayList<DrawerItem>());
    }

    private DrawerListAdapter(Context context, List<DrawerItem> drawerItems) {
        mContext = context;
        mDrawerItems = drawerItems;
        SEARCH_HISTORY_ITEM_TITLE_VISITOR = new TitleActivityFilterVisitor(context);
    }

    public void add(DrawerItem... items) {
        Collections.addAll(mDrawerItems, items);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_list_header, null);
                convertView.setEnabled(false);
                convertView.setOnClickListener(null);
            }
            ((TextView) convertView).setText(getItem(position).getTitle());
//            ((TextView) convertView).setTypeface(ScoutTypeFace.getInstance(convertView.getContext()).getLight());
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

    public void addSearchHistory(String header, ActivityBank activityBank) {
        if (mSearchHistoryHeaderDrawerItem == null) {
            mSearchHistoryHeaderDrawerItem = new HeaderDrawerItem(header);
            mDrawerItems.add(mSearchHistoryHeaderDrawerItem);
            mActivityBank = activityBank;
            loadSearchHistoryItems(mActivityBank);
        } else {
            throw new IllegalStateException("Can only add live search history once");
        }
    }

    public void loadSearchHistoryItems(final ActivityBank activityBank) {
        LogUtil.d(DrawerListAdapter.class.getName(), "Start of loadSearchHistoryItems");

        AsyncTask<Void, Void, Collection<? extends SearchHistory>> task = new AsyncTask<Void, Void, Collection<? extends SearchHistory>>() {
            {
                LogUtil.initExceptionLogging(mContext);
            }
            @Override
            protected Collection<? extends SearchHistory> doInBackground(Void... voids) {
                try {
                    LogUtil.d(DrawerListAdapter.class.getName(), "Start of doInBackground");
                    Collection<? extends SearchHistory> list = activityBank.readSearchHistory(SEARCH_HISTORY_ITEM_COUNT + 1, CredentialsManager.getInstance(mContext).getCurrentUser());
                    LogUtil.d(DrawerListAdapter.class.getName(), "End of doInBackground");
                    return list;
                } catch (Throwable e) {
                    LogUtil.e(DrawerListAdapter.class.getName(), "Could not read search history due to unexpected problem.", e);
                    return Collections.emptyList();
                }
            }

            @Override
            protected void onPostExecute(Collection<? extends SearchHistory> searchHistoryItems) {
                LogUtil.d(DrawerListAdapter.class.getName(), "Start of onPostExecute");
                int pos = mDrawerItems.indexOf(mSearchHistoryHeaderDrawerItem);
                if (mSearchHistoryDrawerItems != null) {
                    mDrawerItems.removeAll(mSearchHistoryDrawerItems);
                    mSearchHistoryDrawerItems.clear();
                } else {
                    mSearchHistoryDrawerItems = new ArrayList<DrawerItem>();
                }
                if (!searchHistoryItems.isEmpty()) {
                    int numItems = searchHistoryItems.size();
//                    if (numItems > SEARCH_HISTORY_ITEM_COUNT) {
//                        searchHistoryItems = searchHistoryItems.subList(0, SEARCH_HISTORY_ITEM_COUNT);
//                    }
                    for (SearchHistory item : searchHistoryItems) {
                        String title = item.getData().getFilter().visit(SEARCH_HISTORY_ITEM_TITLE_VISITOR);
                        if (title.length() > 1) {
                            title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
                        }
                        ActivitiesListDrawerItem drawerItem = new ActivitiesListDrawerItem(title, R.drawable.ic_action_search, item.getData().getFilter());
                        mSearchHistoryDrawerItems.add(drawerItem);
                        mDrawerItems.add(++pos, drawerItem);
                        if (mSearchHistoryDrawerItems.size() >= SEARCH_HISTORY_ITEM_COUNT) {
                            break;
                        }
                    }
                    if (numItems > SEARCH_HISTORY_ITEM_COUNT) {
                        final DefaultDrawerItem drawerItem = new OpenSearchHistoryDrawerItem(mContext.getString(R.string.show_all), mContext.getString(R.string.drawer_search_history_header));
                        mSearchHistoryDrawerItems.add(drawerItem);
                        mDrawerItems.add(++pos, drawerItem);
                    }
                    notifyDataSetChanged();
                }
                LogUtil.d(DrawerListAdapter.class.getName(), "End of onPostExecute");
            }
        };
        task.execute();
        LogUtil.d(DrawerListAdapter.class.getName(), "End of loadSearchHistoryItems");
    }
}
