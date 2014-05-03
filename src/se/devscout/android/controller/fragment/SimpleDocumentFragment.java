package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.ScoutTypeFace;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class SimpleDocumentFragment extends Fragment {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private static interface Item {
        void append(LinearLayout layout, LayoutInflater inflater);
    }

    private static class ImageItem implements Item {

        private int mImageResId;
        private boolean mZoom;

        private ImageItem(int imageResId, boolean zoom) {
            mImageResId = imageResId;
            mZoom = zoom;
        }

        @Override
        public void append(LinearLayout layout, LayoutInflater inflater) {
            ImageView imageView = new ImageView(layout.getContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    layout.getContext().getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height)));
            imageView.setScaleType(mZoom ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(mImageResId);
            layout.addView(imageView);
        }
    }

    private static class HeaderItem implements Item {
        private int mHeaderResId;

        private HeaderItem(int headerResId) {
            mHeaderResId = headerResId;
        }

        @Override
        public void append(LinearLayout layout, LayoutInflater inflater) {
            TextView textView = (TextView) inflater.inflate(R.layout.document_headertextview, layout, false);
            textView.setText(mHeaderResId);
            textView.setTypeface(ScoutTypeFace.getInstance(layout.getContext()).getMedium());
            layout.addView(textView);
        }
    }

    private static class BodyTextItem implements Item {
        private String mText;

        private BodyTextItem(String text) {
            mText = text;
        }

        @Override
        public void append(LinearLayout layout, LayoutInflater inflater) {
            TextView textView = (TextView) inflater.inflate(R.layout.document_bodytext, layout, false);
            StringBuilder sb = new StringBuilder();
            String[] parts = mText.split("\\s*\\n(\\s*\\n)+\\s*");
            for (String part : parts) {
                part = WHITE_SPACE_PATTERN.matcher(part).replaceAll(" ");
                sb.append(part).append('\n').append('\n');
            }
            textView.setText(sb.toString());
            layout.addView(textView);
        }
    }

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
            item.append(linearLayout, inflater);
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

    public SimpleDocumentFragment addHeader(int headerResId) {
        mItems.add(new HeaderItem(headerResId));
        return this;
    }

    public SimpleDocumentFragment addImage(int imageResId, boolean zoom) {
        mItems.add(new ImageItem(imageResId, zoom));
        return this;
    }

    public SimpleDocumentFragment addHeaderAndText(int headerResId, String body) {
        if (body != null && body.length() > 0) {
            addHeader(headerResId);
            addBodyText(body);
        }
        return this;
    }

    public SimpleDocumentFragment addBodyText(String text) {
        mItems.add(new BodyTextItem(text));
        return this;
    }

    public static SimpleDocumentFragment create() {
        return new SimpleDocumentFragment();
    }
}
