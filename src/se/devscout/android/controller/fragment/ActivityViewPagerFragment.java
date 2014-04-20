package se.devscout.android.controller.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.model.LocalActivity;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.StaticFragmentsPagerAdapter;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Media;

public class ActivityViewPagerFragment extends ViewPagerFragment {
    private LocalActivity key;

    public ActivityViewPagerFragment() {
    }

    public ActivityViewPagerFragment(LocalActivity key) {
        this.key = key;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            key = (LocalActivity) savedInstanceState.getSerializable("key");
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
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager);

        ActivityRevision revision = key.getRevisions().get(0);

        ResourceUtil resourceUtil = new ResourceUtil(getActivity());

        boolean isLandscape = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        SimpleDocumentFragment mainTabFragment = SimpleDocumentFragment.create();
        if (revision.getCoverMedia() != null) {
            mainTabFragment.addImage(resourceUtil.toResourceId(revision.getCoverMedia().getURI()));
        }
        mainTabFragment
                .addHeaderAndText(R.string.activity_introduction, revision.getDescriptionIntroduction())
                .addHeaderAndText(R.string.activity_preparations, revision.getDescriptionPreparation())
                .addHeaderAndText(R.string.activity_how_to_do, revision.getDescription())
                .addHeaderAndText(R.string.activity_safety, revision.getDescriptionSafety())
                .addHeaderAndText(R.string.activity_notes, revision.getDescriptionNotes());

        pagerAdapter.addTab(isLandscape ? R.string.activity_tab_description : 0, android.R.drawable.ic_menu_info_details, mainTabFragment);

        if (revision.getDescriptionMaterial() != null && revision.getDescriptionMaterial().length() > 0) {
            SimpleDocumentFragment materialTabFragment = SimpleDocumentFragment.create()
                    .addBodyText(revision.getDescriptionMaterial());
            pagerAdapter.addTab(isLandscape ? R.string.activity_tab_material : 0, R.drawable.ic_action_paste, materialTabFragment);
        }
        if (!revision.getMediaItems().isEmpty()) {
            SimpleDocumentFragment mediaTabFragment = SimpleDocumentFragment.create();
            for (Media media : revision.getMediaItems()) {
                mediaTabFragment.addImage(resourceUtil.toResourceId(media.getURI()));
            }
            pagerAdapter.addTab(isLandscape ? R.string.activity_tab_photos : 0, android.R.drawable.ic_menu_gallery, mediaTabFragment);
        }
        return pagerAdapter;
    }
}
