package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.SimpleDocumentLayout;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.Media;

import java.util.regex.Pattern;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class ActivityFragment extends ActivityBankFragment {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private ObjectIdentifierBean mActivityKey;

    private static interface Item {
        void append(SimpleDocumentLayout layout, LayoutInflater inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivityKey = (ObjectIdentifierBean) savedInstanceState.getSerializable("mActivityKey");
        }
        return createView(inflater, container, getActivityBank().readActivity(mActivityKey), getActivity());
    }

    public static View createView(LayoutInflater inflater, ViewGroup container, ActivityProperties activityProperties, Context context) {
        View view = inflater.inflate(R.layout.activity, container, false);
        SimpleDocumentLayout linearLayout = (SimpleDocumentLayout) view.findViewById(R.id.activityDocument);

//        ActivityRevision revision = ActivityUtil.getLatestActivityRevision(activityProperties);

        String ages = activityProperties.getAges().toString();
        String participantCount = activityProperties.getParticipants().toString();
        if (ages.length() > 0 && participantCount.length() > 0) {
            ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(context.getString(R.string.activityFactAgeAndParticipants, ages, participantCount));
        } else if (participantCount.length() > 0) {
            ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(context.getString(R.string.activityFactParticipants, participantCount));
        } else if (ages.length() > 0) {
            ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(context.getString(R.string.activityFactAge, ages));
        }
        boolean isAgeAndParticipantsSet = ages.length() > 0 || participantCount.length() > 0;
        ((TextView)view.findViewById(R.id.activityFactAgeAndParticipants)).setVisibility(isAgeAndParticipantsSet ? View.VISIBLE : View.GONE);


        String time = activityProperties.getTimeActivity().toString();
        boolean isTimeSet = time.length() > 0;
        if (isTimeSet) {
            ((TextView) view.findViewById(R.id.activityFactTime)).setText(context.getString(R.string.activitiesListItemTime, time));
        }
        ((TextView)view.findViewById(R.id.activityFactTime)).setVisibility(isTimeSet ? View.VISIBLE : View.GONE);

        String categories = TextUtils.join(", ", activityProperties.getCategories());
        boolean isCategoriesSet = categories.length() > 0;
        if (isCategoriesSet) {
            ((TextView)view.findViewById(R.id.activityFactCategories)).setText(categories);
        }
        ((TextView)view.findViewById(R.id.activityFactCategories)).setVisibility(isCategoriesSet ? View.VISIBLE : View.GONE);

        ResourceUtil resourceUtil = new ResourceUtil(context);

        if (activityProperties.getCoverMedia() != null) {
            ImageView activityCoverImage = (ImageView) view.findViewById(R.id.activityCoverImage);
            activityCoverImage.setImageResource(resourceUtil.toResourceId(activityProperties.getCoverMedia().getURI()));

            TextView activityCoverMore = (TextView) view.findViewById(R.id.activityCoverMore);
            if (activityProperties.getMediaItems().size() > 1) {
                activityCoverMore.setText("" + (activityProperties.getMediaItems().size() - 1) + "+");
                activityCoverMore.setVisibility(View.VISIBLE);
            } else {
                activityCoverMore.setVisibility(View.GONE);
            }

            view.findViewById(R.id.activityCover).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.activityCover).setVisibility(View.GONE);
        }
        linearLayout
                .addHeaderAndText(R.string.activity_introduction, activityProperties.getDescriptionIntroduction())
                .addHeaderAndText(R.string.activity_tab_material, activityProperties.getDescriptionMaterial())
                .addHeaderAndText(R.string.activity_preparations, activityProperties.getDescriptionPreparation())
                .addHeaderAndText(R.string.activity_how_to_do, activityProperties.getDescription())
                .addHeaderAndText(R.string.activity_safety, activityProperties.getDescriptionSafety())
                .addHeaderAndText(R.string.activity_notes, activityProperties.getDescriptionNotes());

        if (!activityProperties.getMediaItems().isEmpty()) {
            linearLayout.addHeader(R.string.activity_tab_photos);
            for (Media media : activityProperties.getMediaItems()) {
                linearLayout.addImage(resourceUtil.toResourceId(media.getURI()), false);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        outState.putSerializable("mActivityKey", mActivityKey);
    }

    public static ActivityFragment create(ActivityKey activityKey) {
        ActivityFragment fragment = new ActivityFragment();
        fragment.mActivityKey = new ObjectIdentifierBean(activityKey.getId());
        return fragment;
    }
}
