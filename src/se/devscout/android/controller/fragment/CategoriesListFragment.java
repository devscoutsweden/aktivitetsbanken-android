package se.devscout.android.controller.fragment;

import android.util.Log;
import se.devscout.android.R;
import se.devscout.android.util.CategoryFilter;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.CategoryProperties;

import java.util.List;
import java.util.regex.Pattern;

public class CategoriesListFragment extends QuickSearchListFragment<CategoryProperties> {

    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");

    public CategoriesListFragment(List<? extends CategoryProperties> categories) {
        super((List<CategoryProperties>) categories);
    }

    @Override
    protected int getImageResId(CategoryProperties item) {
        int resourceId = getResourceId(item.getGroup() + "_" + item.getName(), "drawable");
        return resourceId > 0 ? resourceId : getResourceId(item.getGroup(), "drawable");
    }

    private int getResourceId(String text, String type) {
        String groupName = toResourceName(text);
        String packageName = getActivity().getPackageName();
        int identifier = getResources().getIdentifier(groupName, type, packageName);
        Log.d(CategoriesListFragment.class.getName(), "Id for " + type + " '" + groupName + "': " + identifier);
        return identifier;
    }

    private String toResourceName(String name) {
        return NOT_A_Z.matcher(name.toLowerCase()).replaceAll("");
    }

    @Override
    protected String getTitle(CategoryProperties option) {
        int resourceId = getResourceId(option.getName(), "string");
        return resourceId > 0 ? getString(resourceId) : option.getName();
    }

    @Override
    protected String getSubtitle(CategoryProperties option) {
        int resourceId = getResourceId(option.getGroup(), "string");
        return resourceId > 0 ? getString(resourceId) : option.getGroup();
    }

    @Override
    protected ActivityFilter createFilter(CategoryProperties option) {
        return new CategoryFilter(option);
    }

    @Override
    protected String getSearchResultTitle(CategoryProperties option) {
        return getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }

    public static CategoriesListFragment create(List<? extends CategoryProperties> categories) {
        CategoriesListFragment fragment = new CategoriesListFragment(categories);
        return fragment;
    }
}
