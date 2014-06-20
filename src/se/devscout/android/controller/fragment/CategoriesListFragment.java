package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.view.CategoriesListSearchView;
import se.devscout.server.api.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriesListFragment extends QuickSearchListFragment<CategoryListItem, CategoriesListSearchView> {

    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");

    public CategoriesListFragment() {
        // Send empty list to superclass. This does not matter since doSearch() is overridden.
        super(Collections.<CategoryListItem>emptyList());
    }

    @Override
    protected List<CategoryListItem> doSearch() {
        List<? extends Category> categories = ActivityBankFactory.getInstance(getActivity()).readCategories();
        List<CategoryListItem> result = new ArrayList<CategoryListItem>();
        for (Category category : categories) {
            result.add(new CategoryListItem(category));
        }
        return result;
    }

    @Override
    protected CategoriesListSearchView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new CategoriesListSearchView(getActivity(), 0, 0, false);
    }

/*
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
*/

    public static CategoriesListFragment create() {
        CategoriesListFragment fragment = new CategoriesListFragment();
        return fragment;
    }
}
