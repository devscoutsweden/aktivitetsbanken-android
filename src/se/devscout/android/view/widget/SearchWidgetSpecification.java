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

public class SearchWidgetSpecification implements WidgetSpecification {
    @Override
    public int getTitleResId() {
        return 0;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {
        return new View[] {
                new SearchView(container.getContext())
        };
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
