package se.devscout.android.view.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.SearchFragment;
import se.devscout.android.util.ActivityBankFactory;

public class SearchActivitiesFinderComponentFactory extends AbstractActivitiesFinderComponentFactory implements WidgetComponentFactory {
    public SearchActivitiesFinderComponentFactory(String name) {
        super(name, R.string.startTabSearch, R.drawable.ic_drawer, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        return new SearchView(container.getContext());
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
                    String condition = textField.getText().toString().trim();
                    if (condition.length() == 0) {
                        Toast.makeText(getContext(), R.string.searchNoFiltersMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        getContext().startActivity(SearchResultActivity.createIntent(getContext(), ActivityBankFactory.getInstance(getContext()).getFilterFactory().createTextFilter(condition), getContext().getString(R.string.searchResultTitle)));
                    }
                }
            });
        }
    }

}
