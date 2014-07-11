package se.devscout.android.view.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.SearchFragment;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;

public class SearchActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory implements WidgetSpecification {
    public SearchActivitiesFinderComponentFactory(int nameResId, int iconResId) {
        super(iconResId, nameResId);
    }

    @Override
    public boolean isTitleImportant() {
        return false;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {
        return new SearchView(container.getContext());
    }

    @Override
    public boolean isDefaultWidget() {
        return true;
    }

    private static class SearchView extends FrameLayout {
        public SearchView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.search_widget, this, true);

            EditText textField = (EditText) findViewById(R.id.searchWidgetText);
            SharedPreferences prefs = getContext().getSharedPreferences(SearchFragment.class.getName(), Context.MODE_PRIVATE);
            String text = prefs.getString("searchText", "");
            textField.setText(text);

            ImageView linearLayout = (ImageView) findViewById(R.id.searchWidgetButton);
            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText textField = (EditText) findViewById(R.id.searchWidgetText);
                    getContext().startActivity(SearchResultActivity.createIntent(getContext(), ActivityBankFactory.getInstance(getContext()).getFilterFactory().createTextFilter(textField.getText().toString()), getContext().getString(R.string.searchResultTitle)));
                }
            });
        }
    }

}
