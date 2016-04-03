package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.devscout.android.R;
import se.devscout.android.controller.activity.GalleryThumbnailsActivity;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.Activity;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.ActivityList;
import se.devscout.android.model.ActivityProperties;
import se.devscout.android.model.Media;
import se.devscout.android.model.ObjectIdentifierBean;
import se.devscout.android.model.Reference;
import se.devscout.android.model.UserKey;
import se.devscout.android.model.UserProperties;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.concurrency.BackgroundTask;
import se.devscout.android.util.concurrency.BackgroundTasksHandlerThread;
import se.devscout.android.util.concurrency.UpdateFavouriteStatusParam;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.android.view.ActivityEditView;
import se.devscout.android.view.ActivityRatingView;
import se.devscout.android.view.AsyncImageBean;
import se.devscout.android.view.AsyncImageView;
import se.devscout.android.view.RelatedActivitiesView;
import se.devscout.android.view.TextViewUtil;
import se.devscout.android.view.widget.WidgetView;

/**
 * Fragment for displaying (very) simple documents with headings, body paragraphs and images.
 */
public class ActivityFragment extends ActivityBankFragment implements BackgroundTasksHandlerThread.Listener/* implements BackgroundTasksHandlerThread.Listener */ {

    private String mAges;
    private String mParticipantCount;
    private String mTime;
    private int mFavouritesCount;
    private Double mRatingAverage;
    private String mCategories;
    private String mBodyText;
    private AsyncImageBean mAsyncImageProps;
    private ArrayList<ObjectIdentifierBean> mGalleryMediaKeys;
    private ArrayList<ObjectIdentifierBean> mRelatedActivitiesKeys;

    private ObjectIdentifierBean mActivityKey;

    public synchronized void initView(final View view, ActivityProperties activityProperties) {
        final FragmentActivity context = getActivity();
        if (view == null || context == null) {
            LogUtil.e(ActivityFragment.class.getName(), "Could not display activity information since view or context is null: view = " + view + " context = " + context);
            return;
        }

        if (mBodyText != null) {
            LogUtil.e(ActivityFragment.class.getName(), "Activity has already been loaded.");

            if (mAges.length() > 0 && mParticipantCount.length() > 0) {
                ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(context.getString(R.string.activityFactAgeAndParticipants, mAges, mParticipantCount));
            } else if (mParticipantCount.length() > 0) {
                ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(context.getString(R.string.activityFactParticipants, mParticipantCount));
            } else if (mAges.length() > 0) {
                ((TextView) view.findViewById(R.id.activityFactAgeAndParticipants)).setText(context.getString(R.string.activityFactAge, mAges));
            }
            boolean isAgeAndParticipantsSet = mAges.length() > 0 || mParticipantCount.length() > 0;
            view.findViewById(R.id.activityFactAgeAndParticipants).setVisibility(isAgeAndParticipantsSet ? View.VISIBLE : View.GONE);

            boolean isTimeSet = mTime.length() > 0;
            if (isTimeSet) {
                ((TextView) view.findViewById(R.id.activityFactTime)).setText(context.getString(R.string.activitiesListItemTime, mTime));
            }
            view.findViewById(R.id.activityFactTime).setVisibility(isTimeSet ? View.VISIBLE : View.GONE);

            CredentialsManager credentialsManager = CredentialsManager.getInstance(getActivity());
            final UserKey currentUser = credentialsManager.getCurrentUser();

            Boolean isFavourite = getActivityBank().isFavourite(mActivityKey, currentUser);
            int favouriteCountWhenNotPersonalFavourite = mFavouritesCount - (isFavourite ? 1 : 0);

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
            if (mRatingAverage != null) {
                averageRatingBar.setRating(mRatingAverage.floatValue());
                averageRatingBar.setVisibility(View.VISIBLE);
            } else {
                averageRatingBar.setVisibility(View.GONE);
            }

            Integer commentCount = null;
            boolean isCommented = commentCount != null && commentCount > 0;
//        if (isCommented) {
//            ((TextView) view.findViewById(R.id.activityFactFavourites)).setText(context.getString(R.string.activitiesListItemCommentsCount, commentCount));
//        }
            view.findViewById(R.id.activityFactComments).setVisibility(isCommented ? View.VISIBLE : View.GONE);

            boolean isCategoriesSet = mCategories.length() > 0;
            if (isCategoriesSet) {
                ((TextView) view.findViewById(R.id.activityFactCategories)).setText(mCategories);
            }
            view.findViewById(R.id.activityFactCategories).setVisibility(isCategoriesSet ? View.VISIBLE : View.GONE);

            if (mAsyncImageProps != null) {
                AsyncImageView asyncImageView = (AsyncImageView) view.findViewById(R.id.activityCover);
                asyncImageView.setImage(mAsyncImageProps, ((SingleFragmentActivity) context).getBackgroundTasksHandlerThread());
                asyncImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().startActivity(GalleryThumbnailsActivity.createIntent(getActivity(), mGalleryMediaKeys));
                    }

                });

                TextView activityCoverMore = (TextView) view.findViewById(R.id.activityCoverMore);
                if (mGalleryMediaKeys.size() > 1) {
                    activityCoverMore.setText("" + (mGalleryMediaKeys.size() - 1) + "+");
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
                                    mRelatedActivitiesKeys)));

            activityItems.addView(
                    new WidgetView(
                            context,
                            R.string.activityRatingHeader,
                            new ActivityRatingView(
                                    context,
                                    mActivityKey,
                                    currentUser,
                                    getActivityBank())));

            if (activityProperties != null &&
                    credentialsManager.getState().isLoggedIn() &&
                    getActivityBank().readUser(currentUser).getRole() != null &&
                    getActivityBank().readUser(currentUser).getRole().equals(UserProperties.ROLE_ADMINISTRATOR)) {
                activityItems.addView(
                        new WidgetView(
                                context,
                                R.string.activityEditHeader,
                                new ActivityEditView(
                                        context,
                                        mActivityKey,
                                        currentUser,
                                        getActivityBank(),
                                        activityProperties)));
            }

            TextView textView = (TextView) view.findViewById(R.id.activityDocument);
            textView.setText(TextViewUtil.parseText(mBodyText, context));
            Linkify.addLinks(textView, Linkify.WEB_URLS);
            textView.setLineSpacing(
                    getResources().getDimensionPixelSize(R.dimen.Document_Paragraph_LineSpacingExtra),
                    1.0f);

            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.scrollView).setVisibility(View.INVISIBLE);
        }

        context.invalidateOptionsMenu();

    }

    private String getBodyText(Activity activityProperties) {
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

        Collection<Media> mediaFilesToShow = getMediaItemsToShow(activityProperties);
        if (!mediaFilesToShow.isEmpty()) {
            body.append("## " + getString(R.string.activity_tab_photos) + "\n");
            for (Media media : mediaFilesToShow) {
                body.append(/*"* " + */media.getURI().toString() + "\n");
            }
        }
        if (!activityProperties.getReferences().isEmpty()) {
            body.append("## " + getString(R.string.activity_tab_references) + "\n");
            for (Reference reference : activityProperties.getReferences()) {
                body.append(/*"* " + */reference.getURI().toString() + "\n");
            }
        }

        return body.toString().trim();
    }

    @NonNull
    private Collection<Media> getMediaItemsToShow(Activity activityProperties) {
        Collection<Media> mediaFilesToShow = new ArrayList<>();
        for (Media media : activityProperties.getMediaItems()) {
            if (!"localhost".equals(media.getURI().getHost())) {
                mediaFilesToShow.add(media);
            }
        }
        return mediaFilesToShow;
    }

    @Override
    public BackgroundTasksHandlerThread.ListenerAction onDone(Object parameter, Object response, BackgroundTask task) {
        if (response instanceof ActivityList) {
            ActivityList activities = (ActivityList) response;
            Activity activityProperties = activities.get(mActivityKey);
            if (activityProperties != null) {

                mAges = activityProperties.getAges().toString();
                mParticipantCount = activityProperties.getParticipants().toString();
                mTime = activityProperties.getTimeActivity().toString();
                mGalleryMediaKeys = new ArrayList<>();
                if (activityProperties.getCoverMedia() != null) {
                    List<? extends Media> mediaItems = activityProperties.getMediaItems();
                    for (Media media : ResourceUtil.getImageMediaItems(mediaItems)) {
                        mGalleryMediaKeys.add(new ObjectIdentifierBean(media.getId()));
                    }
                    mAsyncImageProps = new AsyncImageBean(
                            null,
                            getFullScreenMediaURIs(activityProperties.getCoverMedia()));
                } else {
                    mAsyncImageProps = null;
                }
                mCategories = TextUtils.join(", ", activityProperties.getCategories());
                mRatingAverage = activityProperties.getRatingAverage();
                mFavouritesCount = activityProperties.getFavouritesCount() != null ? activityProperties.getFavouritesCount() : 0;
                mBodyText = getBodyText(activityProperties);
                if (activityProperties.getRelatedActivitiesKeys() != null) {
                    mRelatedActivitiesKeys = new ArrayList<>();
                    for (ActivityKey key : activityProperties.getRelatedActivitiesKeys()) {
                        mRelatedActivitiesKeys.add(new ObjectIdentifierBean(key.getId()));
                    }
                } else {
                    mRelatedActivitiesKeys = null;
                }

                initView(getView(), activityProperties);
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

            mAges = savedInstanceState.getString("mAges");
            mParticipantCount = savedInstanceState.getString("mParticipantCount");
            mTime = savedInstanceState.getString("mTime");
            mFavouritesCount = savedInstanceState.getInt("mFavouritesCount");
            mRatingAverage = savedInstanceState.containsKey("mRatingAverage") ? savedInstanceState.getDouble("mRatingAverage") : null;
            mCategories = savedInstanceState.getString("mCategories");
            mBodyText = savedInstanceState.getString("mBodyText");
            mAsyncImageProps = (AsyncImageBean) savedInstanceState.getSerializable("mAsyncImageProps");
            mGalleryMediaKeys = (ArrayList<ObjectIdentifierBean>) savedInstanceState.getSerializable("mGalleryMediaKeys");
            mRelatedActivitiesKeys = (ArrayList<ObjectIdentifierBean>) savedInstanceState.getSerializable("mRelatedActivitiesKeys");
        }

        View view = inflater.inflate(R.layout.activity, container, false);
        initView(view, null);
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
        if (!isActivityLoaded()) {
            getBackgroundTasksHandlerThread(getActivity()).queueReadActivity(mActivityKey);
        }
    }

    private boolean isActivityLoaded() {
        return mBodyText != null;
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

        outState.putString("mAges", mAges);
        outState.putString("mParticipantCount", mParticipantCount);
        outState.putString("mTime", mTime);
        outState.putInt("mFavouritesCount", mFavouritesCount);
        if (mRatingAverage != null) {
            outState.putDouble("mRatingAverage", mRatingAverage);
        }
        outState.putString("mCategories", mCategories);
        outState.putString("mBodyText", mBodyText);
        outState.putSerializable("mAsyncImageProps", mAsyncImageProps);
        outState.putSerializable("mGalleryMediaKeys", mGalleryMediaKeys);
        outState.putSerializable("mRelatedActivitiesKeys", mRelatedActivitiesKeys);
    }

    public static ActivityFragment create(ActivityKey activityKey) {
        ActivityFragment fragment = new ActivityFragment();
        fragment.mActivityKey = new ObjectIdentifierBean(activityKey.getId());
        return fragment;
    }

}
