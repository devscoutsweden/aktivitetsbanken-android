package se.devscout.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.devscout.android.R;
import se.devscout.android.util.ScoutTypeFace;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class TextViewUtil {

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
            if (text != null) {
                sb.append(text);
                sb.setSpan(new HeaderSpan(textMatcher, context), sb.length() - text.length(), sb.length(), 0);
            }
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
            if (text != null) {
                sb.append(text);
                sb.setSpan(new CustomBulletSpan(context), sb.length() - text.length(), sb.length(), 0);
            }
        }
    };

    private static final Rule QUOTE = new Rule(1, Pattern.compile("^>\\s*(.*)$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, final Context context, Matcher textMatcher) {
            if (sequencePos == 0) {
                addEmptyLine(sb, 0.5f);
            }
            String text = textMatcher.group(1);
            if (!TextUtils.isEmpty(text)) {
                // Do not print empty lines (especially if it is the last line of the quote) as they tend to make the subsequent paragraphs indented as well.
                sb.append(text);
                sb.setSpan(new QuotationSpan(context), sb.length() - text.length(), sb.length(), 0);
                sb.setSpan(new StyleSpan(Typeface.ITALIC), sb.length() - text.length(), sb.length(), 0);
            }
        }
    };

    /**
     * Pattern matches:
     * (1) lines starting with digit(s) and a period
     * (2) empty lines (this is to allow double-spaced lists to be seen as a single list -- the empty line is ignored by later code)
     */
    private static final Rule NUMRERED_LIST_ITEM = new Rule(2, Pattern.compile("^((\\d+)\\s*[.](.*)|\\s*)$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, Context context, Matcher textMatcher) {
            if (sequencePos == 0) {
                addEmptyLine(sb, 0.5f);
            }
            if (textMatcher.groupCount() >= 3 && textMatcher.group(3) != null) {
                String text = textMatcher.group(3).trim();
                int number = Integer.parseInt(textMatcher.group(2));
                if (text != null) {
                    sb.append(text);
                    sb.setSpan(new CustomNumberedItemSpan(context, number == 1 && sequencePos > 0 ? (sequencePos + 1) : number), sb.length() - text.length(), sb.length(), 0);
                }
            }
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
            String text = textMatcher.group(1);
            if (text != null) {
                sb.append(text);
            }
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
     * The pattern only matches a single empty line but the rule only triggers
     * if the pattern is matched twice (or more).
     */
    private static final Rule INLINE_LINK = new Rule(1, Pattern.compile("!?\\[([^\\[]*)\\]\\(([^\\)]+)\\)")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, final Context context, final Matcher textMatcher) {

            final String url = textMatcher.group(2).trim();
            if (!TextUtils.isEmpty(url)) {
                sb.append(url);
                sb.setSpan(new URLSpan(url), sb.length() - url.length(), sb.length(), 0);
            }
        }
    };

    /**
     * The pattern only matches a single empty line but the rule only triggers
     * if the pattern is matched twice (or more).
     */
    private static final Rule INLINE_IMAGE = new Rule(1, Pattern.compile("^!\\[([^\\[]*)\\]\\(([^\\)]+)\\)$")) {
        @Override
        void apply(SpannableStringBuilder sb, int sequencePos, final Context context, final Matcher textMatcher) {

            String descr = textMatcher.group(1);
            String uri = textMatcher.group(2);

            sb.append("[image]");
            sb.setSpan(new ImageSpan(context, R.drawable.action_bar, DynamicDrawableSpan.ALIGN_BOTTOM), sb.length() - "[image]".length(), sb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
            QUOTE,
            SINGLE_LINE_SENTENCE,
            MULTIPLE_LINE_FEEDS
    };

    public static Spanned parseText(String text, Context context) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        parseBlockMarkup(text, context, sb);

        parseInlineMarkup(sb);
        return sb;
    }

    private static void parseBlockMarkup(String text, Context context, SpannableStringBuilder sb) {
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

                            rule.apply(sb, sequencePos++, context, matcher);
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
                final String line = strings[i].trim();
                if (line.length() > 0) {
                    sb.append(line);
                    if (i < strings.length - 1) {
                        sb.append('\n');
                    }
                }
            }
        }
    }

    private static void parseInlineMarkup(SpannableStringBuilder sb) {
        Matcher matcher = INLINE_LINK.pattern.matcher(sb);
        int start = 0;
        while (matcher.find(start)) {
            sb.replace(matcher.start(), matcher.end(), matcher.group(2));
            start = matcher.start() + matcher.group(2).length();
            matcher = INLINE_LINK.pattern.matcher(sb);
        }
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

    /**
     * This class is better then the built-in BulletSpan when it comes to
     * taking the line height into account when drawing the bullet.
     */
    private static class CustomNumberedItemSpan implements LeadingMarginSpan {
        private final Context mContext;
        private final int mNumber;

        public CustomNumberedItemSpan(Context context, int number) {
            mContext = context;
            mNumber = number;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return mContext.getResources().getDimensionPixelSize(R.dimen.largeTextSize);
        }

        @Override
        public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
            if (first) {
                canvas.drawText(String.valueOf(mNumber) + ".", 0.2f * getLeadingMargin(first), 0.0f + baseline, paint);
            }
        }
    }

    /**
     * This class is better then the built-in BulletSpan when it comes to
     * taking the line height into account when drawing the bullet.
     */
    private static class QuotationSpan implements LeadingMarginSpan {
        private final Context mContext;

        public QuotationSpan(Context context) {
            mContext = context;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return mContext.getResources().getDimensionPixelSize(R.dimen.extraLargeTextSize);
        }

        @Override
        public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        }
    }
}
