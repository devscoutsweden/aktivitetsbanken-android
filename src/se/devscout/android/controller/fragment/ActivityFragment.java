package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.controller.activity.GalleryThumbnailsActivity;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.concurrency.BackgroundTask;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.util.concurrency.UpdateFavouriteStatusParam;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.android.view.*;
import se.devscout.android.view.widget.WidgetView;
import se.devscout.server.api.model.*;

import java.util.List;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class ActivityFragment extends ActivityBankFragment implements BackgroundTasksHandlerThread.Listener/* implements BackgroundTasksHandlerThread.Listener */ {

    private ObjectIdentifierBean mActivityKey;

    public void onRead(final Activity activityProperties, final View view) {
        view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        final FragmentActivity context = getActivity();
        if (view == null || context == null) {
            LogUtil.e(ActivityFragment.class.getName(), "Could not display activity information since view or context is null: view = " + view + " context = " + context);
            return;
        }

        context.invalidateOptionsMenu();


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
        ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setVisibility(isAgeAndParticipantsSet ? View.VISIBLE : View.GONE);

        String time = activityProperties.getTimeActivity().toString();
        boolean isTimeSet = time.length() > 0;
        if (isTimeSet) {
            ((TextView) view.findViewById(R.id.activityFactTime)).setText(context.getString(R.string.activitiesListItemTime, time));
        }
        ((TextView) view.findViewById(R.id.activityFactTime)).setVisibility(isTimeSet ? View.VISIBLE : View.GONE);

        int favouritesCount = activityProperties.getFavouritesCount() != null ? activityProperties.getFavouritesCount() : 0;
        final UserKey userKey = CredentialsManager.getInstance(getActivity()).getCurrentUser();
        Boolean isFavourite = getActivityBank().isFavourite(mActivityKey, userKey);
        int favouriteCountWhenNotPersonalFavourite = favouritesCount - (isFavourite ? 1 : 0);

        ToggleButton favouriteButton = (ToggleButton) view.findViewById(R.id.activityFavouriteToggleButton);
        favouriteButton.setTextOn(String.valueOf(favouriteCountWhenNotPersonalFavourite + 1));
        favouriteButton.setTextOff(String.valueOf(favouriteCountWhenNotPersonalFavourite));
        favouriteButton.setChecked(isFavourite);
        favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                getBackgroundTasksHandlerThread(getActivity()).queueUpdateFavouriteStatus(mActivityKey, isChecked);
            }
        });

        final RatingBar averageRatingBar = (RatingBar) view.findViewById(R.id.activityRatingAverage);
        Double ratingAverage = activityProperties.getRatingAverage();
        if (ratingAverage != null) {
            averageRatingBar.setRating(ratingAverage.floatValue());
            averageRatingBar.setVisibility(View.VISIBLE);
        } else {
            averageRatingBar.setVisibility(View.GONE);
        }

        Integer commentCount = null;
        boolean isCommented = commentCount != null && commentCount > 0;
//        if (isCommented) {
//            ((TextView) view.findViewById(R.id.activityFactFavourites)).setText(context.getString(R.string.activitiesListItemCommentsCount, commentCount));
//        }
        ((TextView) view.findViewById(R.id.activityFactComments)).setVisibility(isCommented ? View.VISIBLE : View.GONE);

        String categories = TextUtils.join(", ", activityProperties.getCategories());
        boolean isCategoriesSet = categories.length() > 0;
        if (isCategoriesSet) {
            ((TextView) view.findViewById(R.id.activityFactCategories)).setText(categories);
        }
        ((TextView) view.findViewById(R.id.activityFactCategories)).setVisibility(isCategoriesSet ? View.VISIBLE : View.GONE);

        if (activityProperties.getCoverMedia() != null) {
            List<? extends Media> mediaItems = activityProperties.getMediaItems();
            final List<Media> keys = ResourceUtil.getImageMediaItems(mediaItems);

            AsyncImageView asyncImageView = (AsyncImageView) view.findViewById(R.id.activityCover);
            AsyncImageBean asyncImageProps = new AsyncImageBean(
                    null,
                    getFullScreenMediaURIs(activityProperties.getCoverMedia()));
            asyncImageView.setImage(asyncImageProps, ((SingleFragmentActivity) context).getBackgroundTasksHandlerThread());
            asyncImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().startActivity(GalleryThumbnailsActivity.createIntent(getActivity(), keys));
                }

            });

            TextView activityCoverMore = (TextView) view.findViewById(R.id.activityCoverMore);
            if (keys.size() > 1) {
                activityCoverMore.setText("" + (keys.size() - 1) + "+");
                activityCoverMore.setVisibility(View.VISIBLE);
            } else {
                activityCoverMore.setVisibility(View.GONE);
            }

            view.findViewById(R.id.activityCover).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.activityCover).setVisibility(View.GONE);
        }

        LinearLayout activityItems = (LinearLayout) view.findViewById(R.id.activityItems);
        activityItems.addView(
                new WidgetView(
                        context,
                        R.string.showRelatedActivitiesHeader,
                        new RelatedActivitiesView(
                                context,
                                mActivityKey,
                                activityProperties.getRelatedActivitiesKeys())));

        activityItems.addView(
                new WidgetView(
                        context,
                        R.string.activityRatingHeader,
                        new ActivityRatingView(
                                context,
                                mActivityKey,
                                CredentialsManager.getInstance(getActivity()).getCurrentUser(),
                                getActivityBank())));

        TextView textView = (TextView) view.findViewById(R.id.activityDocument);

        StringBuilder body = new StringBuilder();
        if (activityProperties.getDescriptionIntroduction().length() > 0) {
            body.append("## " + getString(R.string.activity_introduction) + "\n" + activityProperties.getDescriptionIntroduction() + "\n");
        }
        if (activityProperties.getDescriptionMaterial().length() > 0) {
            body.append("## " + getString(R.string.activity_tab_material) + "\n" + activityProperties.getDescriptionMaterial() + "\n");
        }
        if (activityProperties.getDescriptionPreparation().length() > 0) {
            body.append("## " + getString(R.string.activity_preparations) + "\n" + activityProperties.getDescriptionPreparation() + "\n");
        }
        if (activityProperties.getDescription().length() > 0) {
            body.append("## " + getString(R.string.activity_how_to_do) + "\n" + activityProperties.getDescription() + "\n");
        }
        if (activityProperties.getDescriptionSafety().length() > 0) {
            body.append("## " + getString(R.string.activity_safety) + "\n" + activityProperties.getDescriptionSafety() + "\n");
        }
        if (activityProperties.getDescriptionNotes().length() > 0) {
            body.append("## " + getString(R.string.activity_notes) + "\n" + activityProperties.getDescriptionNotes() + "\n");
        }

        if (!activityProperties.getMediaItems().isEmpty()) {
            body.append("## " + getString(R.string.activity_tab_photos) + "\n");
            for (Media media : activityProperties.getMediaItems()) {
                body.append(/*"* " + */media.getURI().toString() + "\n");
            }
        }
        if (!activityProperties.getReferences().isEmpty()) {
            body.append("## " + getString(R.string.activity_tab_references) + "\n");
            for (Reference reference : activityProperties.getReferences()) {
                body.append(/*"* " + */reference.getURI().toString() + "\n");
            }
        }

        textView.setText(TextViewUtil.parseText(body.toString().trim(), context));
        textView.setLineSpacing(
                getResources().getDimensionPixelSize(R.dimen.Document_Paragraph_LineSpacingExtra),
                1.0f);

        view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    @Override
    public BackgroundTasksHandlerThread.ListenerAction onDone(Object parameter, Object response, BackgroundTask task) {
        if (response instanceof ActivityList) {
            ActivityList activities = (ActivityList) response;
            Activity activity = activities.get(mActivityKey);
            if (activity != null) {
                onRead(activity, getView());
            }
        } else if (response instanceof UnauthorizedException && parameter instanceof UpdateFavouriteStatusParam) {
            UnauthorizedException e = (UnauthorizedException) response;
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return BackgroundTasksHandlerThread.ListenerAction.KEEP;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivityKey = (ObjectIdentifierBean) savedInstanceState.getSerializable("mActivityKey");
        }
        return createView(inflater, container, container.getRootView().getContext());
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Context context) {

        View view = inflater.inflate(R.layout.activity, container, false);

        view.findViewById(R.id.scrollView).setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getBackgroundTasksHandlerThread(getActivity()).addListener(this);
        getBackgroundTasksHandlerThread(getActivity()).queueReadActivity(mActivityKey);
    }

    @Override
    public void onStop() {
        getBackgroundTasksHandlerThread(getActivity()).removeListener(this);
        super.onStop();
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
