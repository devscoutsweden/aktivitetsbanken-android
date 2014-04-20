package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.model.LocalActivity;
import se.devscout.android.util.ResourceUtil;
import se.devscout.android.view.StaticFragmentsPagerAdapter;
import se.devscout.shared.data.model.ActivityRevision;
import se.devscout.shared.data.model.Media;

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

        SimpleDocumentFragment mainTabFragment = SimpleDocumentFragment.create(revision);
        if (revision.getCoverMedia() != null) {
            mainTabFragment.addImage(resourceUtil.toResourceId(revision.getCoverMedia().getURI()));
        }
        mainTabFragment
                .addHeaderAndText("Introduktion", revision.getDescriptionIntroduction())
                .addHeaderAndText("Förberedelser", revision.getDescriptionPreparation())
                .addHeaderAndText("Genomförande", revision.getDescription())
                .addHeaderAndText("Säkerhet", revision.getDescriptionSafety())
                .addHeaderAndText("Noteringar", revision.getDescriptionNotes());

        pagerAdapter.addTab(R.string.activity_tab_description, mainTabFragment);

        if (revision.getDescriptionMaterial() != null && revision.getDescriptionMaterial().length() > 0) {
            SimpleDocumentFragment materialTabFragment = SimpleDocumentFragment.create(revision)
                    .addBodyText(revision.getDescriptionMaterial());
            pagerAdapter.addTab(R.string.activity_tab_material, materialTabFragment);
        }
        if (!revision.getMediaItems().isEmpty()) {
            SimpleDocumentFragment mediaTabFragment = SimpleDocumentFragment.create(revision);
            for (Media media : revision.getMediaItems()) {
                mediaTabFragment.addImage(resourceUtil.toResourceId(media.getURI()));
            }
            pagerAdapter.addTab(R.string.activity_tab_photos, mediaTabFragment);
        }
        return pagerAdapter;
    }
}
