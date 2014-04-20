package se.devscout.android.controller.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.shared.data.model.ActivityRevisionProperties;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class SimpleDocumentFragment extends Fragment {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private static interface Item {
        void append(LinearLayout layout);
    }

    private static class ImageItem implements Item {

        private int mImageResId;

        private ImageItem(int imageResId) {
            mImageResId = imageResId;
        }

        @Override
        public void append(LinearLayout layout) {
            ImageView imageView = new ImageView(layout.getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    layout.getContext().getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mImageResId);
            layout.addView(imageView);
        }
    }

    private static class HeaderItem implements Item {
        private String mText;

        private HeaderItem(String text) {
            mText = text;
        }

        @Override
        public void append(LinearLayout layout) {
            TextView textView = new TextView(layout.getContext());
            textView.setText(mText);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, layout.getResources().getDimensionPixelSize(R.dimen.mediumTextSize));
            textView.setTypeface(null, Typeface.BOLD);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(textView);
        }
    }

    private static class BodyTextItem implements Item {
        private String mText;

        private BodyTextItem(String text) {
            mText = text;
        }

        @Override
        public void append(LinearLayout layout) {
            TextView textView = new TextView(layout.getContext());
            StringBuilder sb = new StringBuilder();
            String[] parts = mText.split("\\s*\\n(\\s*\\n)+\\s*");
            for (String part : parts) {
                part = WHITE_SPACE_PATTERN.matcher(part).replaceAll(" ");
                sb.append(part).append('\n').append('\n');
            }
            textView.setText(sb.toString());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.addView(textView);
        }
    }

    private ActivityRevisionProperties mProperties;

    private ArrayList<Item> mItems = new ArrayList<Item>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mItems = (ArrayList<Item>) savedInstanceState.getSerializable("mItems");
        }
        View view = inflater.inflate(R.layout.simple_document, container, false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.activityDescriptionList);

        for (Item item : mItems) {
            item.append(linearLayout);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        outState.putSerializable("mItems", mItems);
    }

    public SimpleDocumentFragment addHeader(String text) {
        mItems.add(new HeaderItem(text));
        return this;
    }

    public SimpleDocumentFragment addImage(int imageResId) {
        mItems.add(new ImageItem(imageResId));
        return this;
    }

    public SimpleDocumentFragment addHeaderAndText(String header, String body) {
        if (body != null && body.length() > 0) {
            addHeader(header);
            addBodyText(body);
        }
        return this;
    }

    public SimpleDocumentFragment addBodyText(String text) {
        mItems.add(new BodyTextItem(text));
        return this;
    }

    public static SimpleDocumentFragment create(ActivityRevisionProperties properties) {
        SimpleDocumentFragment fragment = new SimpleDocumentFragment();
        fragment.mProperties = properties;
        return fragment;
    }
}
