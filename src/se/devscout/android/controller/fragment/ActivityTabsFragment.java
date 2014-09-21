package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.util.ResourceUtil;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.Media;

public class ActivityTabsFragment extends TabsFragment {
    private ObjectIdentifierPojo key;

    public ActivityTabsFragment() {
    }

    public ActivityTabsFragment(ObjectIdentifierPojo key) {
        this.key = key;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            key = (ObjectIdentifierPojo) savedInstanceState.getSerializable("key");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        outState.putSerializable("key", key);
    }

    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager, getActivity());

        Activity activity = getActivityBank().readFull(key);
//        ActivityRevision revision = activity.getRevisions().get(activity.getRevisions().size() - 1);

        ResourceUtil resourceUtil = new ResourceUtil(getActivity());

        SimpleDocumentFragment mainTabFragment = SimpleDocumentFragment.create();
        if (activity.getCoverMedia() != null) {
            mainTabFragment.addImage(resourceUtil.toResourceId(activity.getCoverMedia().getURI()), false);
        }
        mainTabFragment
                .addHeaderAndText(R.string.activity_introduction, activity.getDescriptionIntroduction())
                .addHeaderAndText(R.string.activity_preparations, activity.getDescriptionPreparation())
                .addHeaderAndText(R.string.activity_how_to_do, activity.getDescription())
                .addHeaderAndText(R.string.activity_safety, activity.getDescriptionSafety())
                .addHeaderAndText(R.string.activity_notes, activity.getDescriptionNotes());

        pagerAdapter.addTab(R.string.activity_tab_description, R.drawable.ic_action_about, mainTabFragment);

        if (activity.getDescriptionMaterial() != null && activity.getDescriptionMaterial().length() > 0) {
            SimpleDocumentFragment materialTabFragment = SimpleDocumentFragment.create()
                    .addBodyText(activity.getDescriptionMaterial());
            pagerAdapter.addTab(R.string.activity_tab_material, R.drawable.ic_action_paste, materialTabFragment);
        }
        if (!activity.getMediaItems().isEmpty()) {
            SimpleDocumentFragment mediaTabFragment = SimpleDocumentFragment.create();
            for (Media media : activity.getMediaItems()) {
                mediaTabFragment.addImage(resourceUtil.toResourceId(media.getURI()), false);
            }
            pagerAdapter.addTab(R.string.activity_tab_photos, R.drawable.ic_action_picture, mediaTabFragment);
        }
        return pagerAdapter;
    }
}
