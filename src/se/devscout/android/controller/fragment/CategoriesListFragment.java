package se.devscout.android.controller.fragment;

import android.util.Log;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Category;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriesListFragment extends QuickSearchListFragment<Category> {

    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");

    public CategoriesListFragment() {
        // Send empty list to superclass. This does not matter since doSearch() is overridden.
        super(Collections.<Category>emptyList());
    }

    @Override
    protected List<Category> doSearch() {
        return (List<Category>) ActivityBankFactory.getInstance(getActivity()).readCategories();
    }

    @Override
    protected Category getResultObjectFromId(ObjectIdentifierPojo identifier) {
        return ActivityBankFactory.getInstance(getActivity()).readCategoryFull(identifier);
    }

    @Override
    protected int getImageResId(Category item) {
        int resourceId = getResourceId(item.getGroup() + "_" + item.getName(), "drawable");
        return resourceId > 0 ? resourceId : getResourceId(item.getGroup(), "drawable");
    }

    private int getResourceId(String text, String type) {
        String resName = toResourceName(text);
        // TODO Fix hard-coded icons.
        if ("scoutmethod_friluftsliv".equals(resName)) {
            return R.drawable.scout_method_outdoors;
        } else if ("scoutmethod_patrullen".equals(resName)) {
            return R.drawable.scout_method_troupe;
        } else {
            String packageName = getActivity().getPackageName();
            int identifier = getResources().getIdentifier(resName, type, packageName);
            Log.d(CategoriesListFragment.class.getName(), "Id for " + type + " '" + resName + "': " + identifier);
            return identifier;
        }
    }

    private String toResourceName(String name) {
        return NOT_A_Z.matcher(name.toLowerCase()).replaceAll("");
    }

    @Override
    protected String getTitle(Category option) {
        int resourceId = getResourceId(option.getName(), "string");
        return resourceId > 0 ? getString(resourceId) : option.getName();
    }

    @Override
    protected String getSubtitle(Category option) {
        int resourceId = getResourceId(option.getGroup(), "string");
        return resourceId > 0 ? getString(resourceId) : option.getGroup();
    }

    @Override
    protected ActivityFilter createFilter(Category option) {
        return new SimpleCategoryFilter(option);
    }

    @Override
    protected String getSearchResultTitle(Category option) {
        return getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }

    public static CategoriesListFragment create() {
        CategoriesListFragment fragment = new CategoriesListFragment();
        return fragment;
    }
}
