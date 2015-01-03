package se.devscout.android.util;

import android.R;
import android.content.Context;
import android.util.DisplayMetrics;
import se.devscout.server.api.model.Media;

import java.net.URI;

public class ResourceUtil {

    private Context mContext;

    public ResourceUtil(Context context) {
        mContext = context;
    }

    public static URI[] getFullScreenMediaURIs(Media media, Context context) {
        if (media != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int[] widths = new int[]{
                    Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels),
                    Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels),
                    context.getResources().getDimensionPixelSize(R.dimen.thumbnail_width)
            };
            URI[] uris = new URI[widths.length];
            for (int i = 0; i < widths.length; i++) {
                int width = widths[i];
                uris[i] = ActivityBankFactory.getInstance(context).getMediaItemURI(media, width, width);
            }
            return uris;
        } else {
            return new URI[0];
        }
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
