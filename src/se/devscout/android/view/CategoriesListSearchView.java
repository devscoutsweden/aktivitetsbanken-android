package se.devscout.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.controller.fragment.CategoryListItem;
import se.devscout.android.util.*;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Category;
import se.devscout.server.api.model.Media;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriesListSearchView extends QuickSearchListView<CategoryListItem> implements BackgroundTasksHandlerThread.Listener {
    private static final Pattern NOT_A_Z = Pattern.compile("[^a-z_]");
    private SingleFragmentActivity mActivity = null;

    public CategoriesListSearchView(Context context, int emptyMessageTextId, int emptyHeaderTextId, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight, Collections.<CategoryListItem>emptyList());
        mActivity = (SingleFragmentActivity) context;
        mActivity.getBackgroundTasksHandlerThread().addListener(this);
    }

    @Override
    public SearchTask createSearchTask() {
        return new MySearchTask();
    }

    @Override
    protected int getImageResId(CategoryListItem item, ImageView imageView) {

        Media media = ActivityBankFactory.getInstance(getContext()).readMediaItem(item.getIconMediaKey());
        if (media != null) {
            imageView.setTag(R.id.imageViewUri, media.getURI());
            mActivity.getBackgroundTasksHandlerThread().queueGetMediaResource(imageView, media.getURI());
        }
        return R.drawable.ic_action_labels;
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

    @Override
    public void onDone(Object[] parameters, Object response) {
        if (parameters[0] instanceof ImageView && parameters[1] instanceof URI && response instanceof Bitmap) {
            ImageView imageView = (ImageView) parameters[0];
            URI loadedURI = (URI) parameters[1];
            if (loadedURI.equals(imageView.getTag(R.id.imageViewUri))) {
                imageView.setImageBitmap((Bitmap) response);
            } else {
                LogUtil.d(SingleFragmentActivity.class.getName(), "Image has been loaded but the image view has been recycled and is now used for another image.");
            }
        }
        LogUtil.i(SingleFragmentActivity.class.getName(), "Task completed");
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
