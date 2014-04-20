package se.devscout.android;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import se.devscout.android.model.LocalActivity;
import se.devscout.android.model.LocalRange;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.shared.data.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DemoActivityRepo implements ActivityBank {
    private static DemoActivityRepo ourInstance;

    public static DemoActivityRepo getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new DemoActivityRepo(ctx);
        }
        return ourInstance;
    }

    private static List<LocalActivity> mActivities = new ArrayList<LocalActivity>();

    private DemoActivityRepo(Context ctx) {
        XmlResourceParser parser = ctx.getResources().getXml(R.xml.activities);
        int eventType = 0;
        LocalActivity localActivity = null;
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
                            localActivity = new LocalActivity(
                                    name,
                                    featuredImageResId,
                                    featured,
                                    mActivities.size());
                            mActivities.add(localActivity);
                        } else if ("introduction".equals(parser.getName())) {
                            localActivity.setIntroduction(parser.nextText());
                        } else if ("description".equals(parser.getName())) {
                            String type = parser.getAttributeValue(null, "type");
                            String descr = parser.nextText().trim();
                            if ("activity".equals(type)) {
                                localActivity.setDescription(descr);
                            } else if ("safety".equals(type)) {
                                localActivity.setSafety(descr);
                            } else if ("references".equals(type)) {
//                                localActivity.setDescription(descr);
                            } else if ("material".equals(type)) {
                                localActivity.setMaterial(descr);
                            } else if ("activity-name".equals(type)) {
                                localActivity.setName(descr);
                            } else if ("ages".equals(type)) {
                                localActivity.setAges(toRange(descr));
                            } else if ("participant-count".equals(type)) {
                                localActivity.setParticipants(toRange(descr));
                            } else if ("scout-method".equals(type)) {
                                localActivity.addCategory("scout-method", descr);
                            } else {
                                localActivity.addDescriptionNode(descr);
                            }
                        } else if ("category".equals(parser.getName())) {
                            localActivity.addCategory(
                                    parser.getAttributeValue(null, "group"),
                                    parser.getAttributeValue(null, "name"));
                        } else if ("media".equals(parser.getName())) {
                            URI uri = URI.create(parser.getAttributeValue(null, "uri"));
                            localActivity.addMediaItem(uri);
                        } else if ("participants".equals(parser.getName())) {
                            localActivity.setParticipants(new LocalRange(
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
    }

    private Range<Integer> toRange(String s) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        Scanner scanner = new Scanner(s);
        scanner.useDelimiter("[^0-9]+");
        while (scanner.hasNextInt()) {
            int value = scanner.nextInt();
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        return new LocalRange(min, max);
    }

    @Override
    public List<LocalActivity> find(String name, Boolean featured) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<LocalActivity> find(ActivityFilter condition) {
        ArrayList<LocalActivity> res = new ArrayList<LocalActivity>();
        for (LocalActivity activity : mActivities) {
            if (condition.matches(activity)) {
                res.add(activity);
            }
        }
        return res;
    }

    @Override
    public Activity create(ActivityProperties properties) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(ActivityKey key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ActivityProperties update(ActivityKey key, ActivityProperties properties) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LocalActivity read(ActivityKey key) {
        for (LocalActivity activity : mActivities) {
            if (key.getId().equals(activity.getId())) {
                return activity;
            }
        }
        return null;
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteReference(ActivityKey key, ReferenceKey referenceKey) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Reference> readReferences(ActivityKey key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
