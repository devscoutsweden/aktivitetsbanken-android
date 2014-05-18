package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.util.ActivityUtil;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.SimpleDocumentLayout;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Media;

import java.util.regex.Pattern;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class ActivityFragment extends ActivityBankFragment {

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private ObjectIdentifierPojo mActivityKey;

    private static interface Item {
        void append(SimpleDocumentLayout layout, LayoutInflater inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivityKey = (ObjectIdentifierPojo) savedInstanceState.getSerializable("mActivityKey");
        }
        View view = inflater.inflate(R.layout.activity, container, false);
        SimpleDocumentLayout linearLayout = (SimpleDocumentLayout) view.findViewById(R.id.activityDocument);

        ActivityProperties properties = getActivityBank().read(mActivityKey);

        ActivityRevision revision = ActivityUtil.getLatestActivityRevision(properties);

        String ages = revision.getAges().toString();
        String participantCount = revision.getParticipants().toString();
        if (ages.length() > 0 && participantCount.length() > 0) {
            ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(getString(R.string.activityFactAgeAndParticipants, ages, participantCount));
        } else if (participantCount.length() > 0) {
            ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(getString(R.string.activityFactParticipants, participantCount));
        } else if (ages.length() > 0) {
            ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(getString(R.string.activityFactAge, ages));
        }
        boolean isAgeAndParticipantsSet = ages.length() > 0 || participantCount.length() > 0;
        ((TextView)view.findViewById(R.id.activityFactAgeAndParticipants)).setVisibility(isAgeAndParticipantsSet ? View.VISIBLE : View.GONE);


        String time = revision.getTimeActivity().toString();
        boolean isTimeSet = time.length() > 0;
        if (isTimeSet) {
            ((TextView) view.findViewById(R.id.activityFactTime)).setText(getString(R.string.activitiesListItemTime, time));
        }
        ((TextView)view.findViewById(R.id.activityFactTime)).setVisibility(isTimeSet ? View.VISIBLE : View.GONE);

        String categories = TextUtils.join(", ", revision.getCategories());
        boolean isCategoriesSet = categories.length() > 0;
        if (isCategoriesSet) {
            ((TextView)view.findViewById(R.id.activityFactCategories)).setText(categories);
        }
        ((TextView)view.findViewById(R.id.activityFactCategories)).setVisibility(isCategoriesSet ? View.VISIBLE : View.GONE);

        ResourceUtil resourceUtil = new ResourceUtil(getActivity());

        if (revision.getCoverMedia() != null) {
            ImageView activityCoverImage = (ImageView) view.findViewById(R.id.activityCoverImage);
            activityCoverImage.setImageResource(resourceUtil.toResourceId(revision.getCoverMedia().getURI()));

            TextView activityCoverMore = (TextView) view.findViewById(R.id.activityCoverMore);
            if (revision.getMediaItems().size() > 1) {
                activityCoverMore.setText("" + (revision.getMediaItems().size() - 1) + "+");
                activityCoverMore.setVisibility(View.VISIBLE);
            } else {
                activityCoverMore.setVisibility(View.GONE);
            }

            view.findViewById(R.id.activityCover).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.activityCover).setVisibility(View.GONE);
        }
        linearLayout
                .addHeaderAndText(R.string.activity_introduction, revision.getDescriptionIntroduction())
                .addHeaderAndText(R.string.activity_tab_material, revision.getDescriptionMaterial())
                .addHeaderAndText(R.string.activity_preparations, revision.getDescriptionPreparation())
                .addHeaderAndText(R.string.activity_how_to_do, revision.getDescription())
                .addHeaderAndText(R.string.activity_safety, revision.getDescriptionSafety())
                .addHeaderAndText(R.string.activity_notes, revision.getDescriptionNotes());

        if (!revision.getMediaItems().isEmpty()) {
            linearLayout.addHeader(R.string.activity_tab_photos);
            for (Media media : revision.getMediaItems()) {
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
        fragment.mActivityKey = new ObjectIdentifierPojo(activityKey.getId());
        return fragment;
    }
}
