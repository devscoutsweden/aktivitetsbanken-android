package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.StaticFragmentsPagerAdapter;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Media;

public class ActivityViewPagerFragment extends ViewPagerFragment {
    private ObjectIdentifierPojo key;

    public ActivityViewPagerFragment() {
    }

    public ActivityViewPagerFragment(ObjectIdentifierPojo key) {
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
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager, boolean landscape) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager);

        // TODO Use some kind of factory for accessing/creating the ActivityBank instead of forcing SQLiteActivityRepo?
        Activity activity = SQLiteActivityRepo.getInstance(getActivity()).read(key);
        ActivityRevision revision = activity.getRevisions().get(activity.getRevisions().size() - 1);

        ResourceUtil resourceUtil = new ResourceUtil(getActivity());

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

        pagerAdapter.addTab(landscape ? R.string.activity_tab_description : 0, R.drawable.ic_action_about, mainTabFragment);

        if (revision.getDescriptionMaterial() != null && revision.getDescriptionMaterial().length() > 0) {
            SimpleDocumentFragment materialTabFragment = SimpleDocumentFragment.create()
                    .addBodyText(revision.getDescriptionMaterial());
            pagerAdapter.addTab(landscape ? R.string.activity_tab_material : 0, R.drawable.ic_action_paste, materialTabFragment);
        }
        if (!revision.getMediaItems().isEmpty()) {
            SimpleDocumentFragment mediaTabFragment = SimpleDocumentFragment.create();
            for (Media media : revision.getMediaItems()) {
                mediaTabFragment.addImage(resourceUtil.toResourceId(media.getURI()));
            }
            pagerAdapter.addTab(landscape ? R.string.activity_tab_photos : 0, R.drawable.ic_action_picture, mediaTabFragment);
        }
        return pagerAdapter;
    }
}
