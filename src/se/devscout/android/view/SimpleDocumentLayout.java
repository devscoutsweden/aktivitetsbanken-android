package se.devscout.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.util.ScoutTypeFace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class SimpleDocumentLayout extends LinearLayout {

    static abstract class Rule {
        private Pattern pattern;
        private int minRowsMatching;

        private Rule(int minRowsMatching, Pattern pattern) {
            this.minRowsMatching = minRowsMatching;
            this.pattern = pattern;
        }

        abstract void apply(SpannableStringBuilder sb, int sequencePos, Context context, Matcher textMatcher);

        void addEmptyLine(SpannableStringBuilder sb, float height) {
            if (sb.length() > 0) {
                sb.append(' ');
                sb.setSpan(new RelativeSizeSpan(height), sb.length() - 1, sb.length(), 0);
                sb.append('\n');
            }
        }
    }

    /**
     * Pattern matches lines starting with (at least) one hash sign.
     */
    private static final Rule HEADER = new Rule(1, Pattern.compile("^(#+)\\s*(.*)")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, final Context context, final Matcher textMatcher) {
            addEmptyLine(sb, 0.5f);
            String text = textMatcher.group(2);
            sb.append(text);
            sb.setSpan(new HeaderSpan(textMatcher, context), sb.length() - text.length(), sb.length(), 0);
        }
    };
    /**
     * Pattern matches:
     * (1) lines starting with dash/asterisk and a space
     * (2) empty lines (this is to allow double-spaced lists to be seen as a single list -- the empty line is ignored by later code)
     */
    private static final Rule BULLET_LIST_ITEM = new Rule(2, Pattern.compile("^([-*]\\s*(.*)|\\s*)$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, final Context context, Matcher textMatcher) {
            if (sequencePos == 0) {
                addEmptyLine(sb, 0.5f);
            }
            String text = textMatcher.group(2);
            sb.append(text);
            sb.setSpan(new CustomBulletSpan(context), sb.length() - text.length(), sb.length(), 0);
        }
    };
    /**
     * Pattern matches:
     * (1) lines starting with digit(s) and a period
     * (2) empty lines (this is to allow double-spaced lists to be seen as a single list -- the empty line is ignored by later code)
     */
    private static final Rule NUMRERED_LIST_ITEM = new Rule(2, Pattern.compile("^(\\d+\\s*[.].*|\\s*)$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, Context context, Matcher textMatcher) {
            if (sequencePos == 0) {
                addEmptyLine(sb, 0.5f);
            }
            String text = textMatcher.group(1);
            sb.append(text);
            sb.setSpan(new BulletSpan(BulletSpan.STANDARD_GAP_WIDTH * 5), sb.length() - text.length(), sb.length(), 0);
        }
    };

    /**
     * Pattern matches lines starting with capital letter and ending with some
     * kind of sentence-ending character, like a period or a colon.
     */
    private static final Rule SINGLE_LINE_SENTENCE = new Rule(1, Pattern.compile("^([A-ZÅÄÖ].*[.:!?])$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, Context context, Matcher textMatcher) {
            addEmptyLine(sb, 0.5f);
            sb.append(textMatcher.group(1));
        }
    };

    /**
     * The pattern only matches a single empty line but the rule only triggers
     * if the pattern is matched twice (or more).
     */
    private static final Rule MULTIPLE_LINE_FEEDS = new Rule(2, Pattern.compile("^\\s*$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, Context context, Matcher textMatcher) {
            if (sequencePos == 0) {
                addEmptyLine(sb, 0.5f);
            }
        }
    };

    /**
     * The list of rules used to process each line of text. The ordering is
     * crucial since only the first matching rule is applied (and if a rule is
     * selected, it is used for all the following lines which also match the
     * rule regardless of whether or not those lines are better matched by other
     * rules).
     */
    private static final Rule[] RULES = new Rule[]{
            HEADER,
            BULLET_LIST_ITEM,
            NUMRERED_LIST_ITEM,
            SINGLE_LINE_SENTENCE,
            MULTIPLE_LINE_FEEDS
    };

    public SimpleDocumentLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public SimpleDocumentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public SimpleDocumentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    public SimpleDocumentLayout addHeader(int headerResId) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_document_headertextview, this, false);
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

    public SimpleDocumentLayout addBodyText(String text) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.simple_document_bodytext, this, false);
        textView.setText(parseText(text));
        Linkify.addLinks(textView, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
        addView(textView);
        return this;
    }

    private Spanned parseText(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String[] strings = text.split("\n");

        // Iterate all lines of text
        for (int i = 0; i < strings.length; i++) {
            boolean isStringMatchingRule = false;

            // Match line against the parsing rules
            for (Rule rule : RULES) {
                int rowsMatching = 0;

                // Check how many lines, starting with the current one, that match the rule's pattern.
                for (; i + rowsMatching < strings.length; rowsMatching++) {
                    Pattern rulePattern = rule.pattern;
                    String ruleSubject = strings[i + rowsMatching];
                    if (!rulePattern.matcher(ruleSubject).find()) {
                        break;
                    }
                }

                if (rule.minRowsMatching <= rowsMatching) {
                    // The number of rows matching the pattern equals/exceeds the rule's "triggering threshold".

                    // Apply the rule on all the lines matched by the rule/pattern
                    int sequencePos = 0;
                    for (int j = 0; j < rowsMatching; j++) {
                        String ruleSubject = strings[i + j];
                        if (ruleSubject.length() > 0) {
                            Matcher matcher = rule.pattern.matcher(ruleSubject);
                            matcher.find();

                            rule.apply(sb, sequencePos++, getContext(), matcher);
                            sb.append('\n');
                        }
                    }

                    // Increment the line counter since the rule may have been applied to more than one line.
                    i += rowsMatching - 1;
                    isStringMatchingRule = true;
                    break;
                }
            }
            if (!isStringMatchingRule) {
                // The line does not match any rule. Treat as "regular line of text" (append the line and a linefeed to the buffer).
                if (strings[i].length() > 0) {
                    sb.append(strings[i]);
                    if (i < strings.length - 1) {
                        sb.append('\n');
                    }
                }
            }
        }
        return sb;
    }

    private static class HeaderSpan extends MetricAffectingSpan {
        private final Matcher mTextMatcher;
        private final Context mContext;

        public HeaderSpan(Matcher textMatcher, Context context) {
            mTextMatcher = textMatcher;
            mContext = context;
        }

        @Override
        public void updateMeasureState(TextPaint textPaint) {
            apply(textPaint);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            apply(textPaint);
        }

        private void apply(Paint paint) {
            boolean isBigHeader = mTextMatcher.group(1).length() == 2;

            Typeface oldTypeface = paint.getTypeface();
            int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
            Typeface typeface = /*isBigHeader ? ScoutTypeFace.getInstance(mContext).getMedium() : */ScoutTypeFace.getInstance(mContext).getLight();
            int fakeStyle = oldStyle & ~typeface.getStyle();
            if ((fakeStyle & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fakeStyle & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(typeface);
            paint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.Document_Header_TextSize));
        }
    }

    /**
     * This class is better then the built-in BulletSpan when it comes to
     * taking the line height into account when drawing the bullet.
     */
    private static class CustomBulletSpan implements LeadingMarginSpan {
        private final Context mContext;

        public CustomBulletSpan(Context context) {
            mContext = context;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return mContext.getResources().getDimensionPixelSize(R.dimen.largeTextSize);
        }

        @Override
        public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
            if (first) {
                int emHeight = baseline - top;

                // Draw bullet
                canvas.drawCircle(0.4f * getLeadingMargin(first), 0.0f + baseline - (0.4f * emHeight), 0.25f * emHeight, paint);
            }
        }
    }
}
