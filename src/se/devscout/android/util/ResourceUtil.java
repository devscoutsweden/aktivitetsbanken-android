package se.devscout.android.util;

import android.R;
import android.content.Context;
import android.util.DisplayMetrics;
import se.devscout.server.api.model.Media;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceUtil {

    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");

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

    public static List<Media> getImageMediaItems(List<? extends Media> mediaItems) {
        List<Media> keys = new ArrayList<Media>();
        for (Media mediaItem : mediaItems) {
            if (IMAGE_MIME_TYPES.contains(mediaItem.getMimeType())) {
                keys.add(mediaItem);
            }
        }
        return keys;
    }
}
