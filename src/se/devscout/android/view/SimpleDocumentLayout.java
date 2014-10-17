package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.ScoutTypeFace;

import java.util.regex.Pattern;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class SimpleDocumentLayout extends LinearLayout {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    public SimpleDocumentLayout(Context context) {
        super(context);
//        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.card_background));
        setOrientation(VERTICAL);
    }

    public SimpleDocumentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.card_background));
        setOrientation(VERTICAL);
    }

    public SimpleDocumentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.card_background));
        setOrientation(VERTICAL);
    }

    public SimpleDocumentLayout addHeader(int headerResId) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.document_headertextview, this, false);
        textView.setText(headerResId);
        textView.setTypeface(ScoutTypeFace.getInstance(getContext()).getMedium());
        addView(textView);
        return this;
    }

    public SimpleDocumentLayout addImage(int imageResId, boolean zoom) {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getContext().getResources().getDimensionPixelSize(android.R.dimen.thumbnail_height)));
        imageView.setScaleType(zoom ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(imageResId);
        addView(imageView);
        return this;
    }

    public SimpleDocumentLayout addHeaderAndText(int headerResId, String body) {
        if (body != null && body.length() > 0) {
            addHeader(headerResId);
            addBodyText(body);
        }
        return this;
    }

    public SimpleDocumentLayout addBodyText(String text) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.document_bodytext, this, false);
        StringBuilder sb = new StringBuilder();
        String[] parts = text.split("\\s*\\n(\\s*\\n)+\\s*");
        for (String part : parts) {
            part = WHITE_SPACE_PATTERN.matcher(part).replaceAll(" ");
            if (sb.length() > 0) {
                sb.append('\n').append('\n');
            }
            sb.append(part);
        }
        textView.setText(sb.toString());
        addView(textView);
        return this;
    }

}
