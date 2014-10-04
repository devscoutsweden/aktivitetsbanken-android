package se.devscout.android.view;

import android.content.Context;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.CategoryListItem;
import se.devscout.android.model.repo.remote.UnauthorizedException;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.SimpleCategoryFilter;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriesListSearchView extends QuickSearchListView<CategoryListItem> {
    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");

    public CategoriesListSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
    }

    @Override
    public SearchTask createSearchTask() {
        return new MySearchTask();
    }

    @Override
    protected int getImageResId(CategoryListItem item) {
        String resName = toResourceName(item.getGroup() + "_" + item.getName());

        if (resName.equals("andaktstillastund")) {
            return R.drawable.scoutconcept_andaktstillastund;
        } else if (resName.equals("ceremonier")) {
            return R.drawable.scoutconcept_ceremonier;
        } else if (resName.equals("diskussion")) {
            return R.drawable.scoutconcept_diskussion;
        } else if (resName.equals("friluftslivland")) {
            return R.drawable.scoutconcept_friluftslivland;
        } else if (resName.equals("friluftslivsj")) {
            return R.drawable.scoutconcept_friluftslivsj;
        } else if (resName.equals("hantverkpyssel")) {
            return R.drawable.scoutconcept_hantverkpyssel;
        } else if (resName.equals("lekar")) {
            return R.drawable.scoutconcept_lekar;
        } else if (resName.equals("lgerbl")) {
            return R.drawable.scoutconcept_lgerbl;
        } else if (resName.equals("matlagning")) {
            return R.drawable.scoutconcept_matlagning;
        } else if (resName.equals("teatermusiksng")) {
            return R.drawable.scoutconcept_teatermusiksng;
        } else if (resName.equals("utomhus")) {
            return R.drawable.scoutconcept_utomhus;
        } else if (resName.equals("scoutmethod")) {
            return R.drawable.scoutmethod;
        } else if (resName.equals("scoutmethod_friluftsliv")) {
            return R.drawable.scoutmethod_friluftsliv;
        } else if (resName.equals("scoutmethod_patrullen")) {
            return R.drawable.scoutmethod_patrullen;
        } else {
            return 0;
        }
    }

    private String toResourceName(String name) {
        return NOT_A_Z.matcher(name.toLowerCase()).replaceAll("");
    }

    @Override
    protected String getTitle(CategoryListItem option) {
        return option.getName();
    }

    @Override
    protected String getSubtitle(CategoryListItem option) {
        return option.getGroup();
    }

    @Override
    protected ActivityFilter createFilter(CategoryListItem option) {
        return new SimpleCategoryFilter(option.getGroup(), option.getName(), option.getServerId());
    }

    @Override
    protected String getSearchResultTitle(CategoryListItem option) {
        return getContext().getString(R.string.searchResultTitleCategoryTrack, option.getName());
    }

    private class MySearchTask extends SearchTask {
        @Override
        protected List<CategoryListItem> doSearch() throws UnauthorizedException {
            List<? extends Category> categories = ActivityBankFactory.getInstance(getContext()).readCategories();
            List<CategoryListItem> result = new ArrayList<CategoryListItem>();
            for (Category category : categories) {
                result.add(new CategoryListItem(category));
            }
            return result;
        }
    }
}
