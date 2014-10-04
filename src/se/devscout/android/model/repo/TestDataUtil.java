package se.devscout.android.model.repo;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import se.devscout.android.R;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.server.api.model.Range;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestDataUtil {
    public static List<LocalActivity> readXMLTestData(Context ctx) {
        List<LocalActivity> mActivities = new ArrayList<LocalActivity>();
        XmlResourceParser parser = ctx.getResources().getXml(R.xml.activities);
        int eventType = 0;
        LocalActivity revision = null;
//        LocalActivityRevision revision = null;
//        LocalActivityRevision firstRevision = null;
        try {
            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("activity".equals(parser.getName())) {
                            String featuresImageValue = parser.getAttributeValue(null, "featured-image");
                            int featuredImageResId = -1;
                            if (featuresImageValue != null) {
                                Log.i("Aktivitetsbanken", "" + featuresImageValue);
                                featuredImageResId = ctx.getResources().getIdentifier(featuresImageValue, "drawable", ctx.getPackageName());
                            }
                            boolean featured = parser.getAttributeBooleanValue(null, "featured", false);
                            String name = parser.getAttributeValue(null, "name");
                            revision = new LocalActivity(
                                    null,
                                    LocalActivity.debugCounter++,
                                    0,
                                    0,
                                    false);
                            mActivities.add(revision);
                            revision.setName(name);
                            revision.setFeatured(featured);
//                            firstRevision = new LocalActivityRevision("." + name, featured, revision, LocalActivityRevision.debugCounter++);
//                            revision.getRevisions().add(firstRevision);
//                            revision = new LocalActivityRevision(name, featured, revision, LocalActivityRevision.debugCounter++);
//                            revision.getRevisions().add(revision);
                        } else if ("introduction".equals(parser.getName())) {
                            revision.setIntroduction(parser.nextText());
                        } else if ("description".equals(parser.getName())) {
                            String type = parser.getAttributeValue(null, "type");
                            String descr = parser.nextText().trim();
                            if ("activity".equals(type)) {
//                                firstRevision.setDescription("_" + descr);
                                revision.setDescription(descr);
                            } else if ("safety".equals(type)) {
                                revision.setSafety(descr);
                            } else if ("references".equals(type)) {
//                                revision.setDescription(descr);
                            } else if ("material".equals(type)) {
                                revision.setMaterial(descr);
                            } else if ("activity-name".equals(type)) {
//                                firstRevision.setName("." + descr);
                                revision.setName(descr);
                            } else if ("ages".equals(type)) {
                                revision.setAges(toRange(descr));
                            } else if ("participant-count".equals(type)) {
                                revision.setParticipants(toRange(descr));
                            } else if ("scout-method".equals(type)) {
//                                revision.addCategory("scout-method", descr);
                            } else {
                                revision.addDescriptionNote(descr);
                            }
                        } else if ("category".equals(parser.getName())) {
//                            revision.addCategory(
//                                    parser.getAttributeValue(null, "group"),
//                                    parser.getAttributeValue(null, "name"));
                        } else if ("media".equals(parser.getName())) {
                            URI uri = URI.create(parser.getAttributeValue(null, "uri"));
//                            revision.addMediaItem(uri, null);
                        } else if ("participants".equals(parser.getName())) {
                            revision.setParticipants(new IntegerRangePojo(
                                    parser.getAttributeIntValue(null, "min", 1),
                                    parser.getAttributeIntValue(null, "max", 99)));
                        } else if ("time".equals(parser.getName())) {
                            revision.setTimeActivity(new IntegerRangePojo(
                                    parser.getAttributeIntValue(null, "min", 1),
                                    parser.getAttributeIntValue(null, "max", 99)));
                        }
                        break;
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(ctx.getString(R.string.app_name), "Could not initialize repository.", e);
        } catch (IOException e) {
            Log.e(ctx.getString(R.string.app_name), "Could not initialize repository.", e);
        }
        return mActivities;
    }

    private static Range<Integer> toRange(String s) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        Scanner scanner = new Scanner(s);
        scanner.useDelimiter("[^0-9]+");
        while (scanner.hasNextInt()) {
            int value = scanner.nextInt();
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        return new IntegerRangePojo(min, max);
    }
}
