package se.devscout.android.util;

import android.content.Context;
import android.graphics.Typeface;

public class ScoutTypeFace {
    private static ScoutTypeFace instance;
    private Typeface mLight;
    private Typeface mMedium;

    public ScoutTypeFace(Context context) {
        mMedium = Typeface.createFromAsset(context.getAssets(), "ScouternaDIN-Medium.ttf");
        mLight = Typeface.createFromAsset(context.getAssets(), "ScouternaDIN-ExtraLight.ttf");
    }

    public static ScoutTypeFace getInstance(Context context) {
        if (instance == null) {
            instance = new ScoutTypeFace(context);
        }
        return instance;
    }

    private ScoutTypeFace() {
    }

    public Typeface getLight() {
        return mLight;
    }

    public Typeface getMedium() {
        return mMedium;
    }
}
