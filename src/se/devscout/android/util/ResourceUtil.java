package se.devscout.android.util;

import android.R;
import android.content.Context;

import java.net.URI;

public class ResourceUtil {

    private Context mContext;

    public ResourceUtil(Context context) {
        mContext = context;
    }

    public int toResourceId(URI uri) {
        if ("app-drawable".equals(uri.getScheme())) {
            int identifier = mContext.getResources().getIdentifier(uri.getSchemeSpecificPart(), "drawable", mContext.getPackageName());
            if (identifier > 0) {
                return identifier;
            }
        }
        // Return default icon
        return R.drawable.ic_menu_gallery;
    }
}
