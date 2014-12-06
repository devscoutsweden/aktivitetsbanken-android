package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.LogUtil;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.android.view.ActivityCoverView;

//TODO: Rename to something like AsyncImageFragment
public class ActivityCoverFragment extends ActivityBankFragment implements View.OnClickListener {

    public static interface OnClickListener {

        void onImageClick(View view, ActivitiesListItem localActivity, Context ctx);
    }

    //TODO: Implement/use more generic data holder than ActivitiesListItem
    private ActivitiesListItem mActivity;
    private int mSize;

    private OnClickListener mOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mActivity = (ActivitiesListItem) savedInstanceState.getSerializable("mActivity");
            mSize = savedInstanceState.getInt("mSize");
        } else {
            mSize = container.getContext().getResources().getDisplayMetrics().widthPixels;
        }
        ActivityCoverView view = new ActivityCoverView(getActivity());

        view.init(
                mActivity.getCoverMedia(),
                mActivity.getName(),
                (SingleFragmentActivity) getActivity(),
                mSize,
                ImageView.ScaleType.FIT_CENTER);

        if (mOnClickListener != null) {
            view.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        LogUtil.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mActivity", mActivity);
        outState.putInt("mSize", mSize);
        LogUtil.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onImageClick(view, mActivity, getActivity());
        }
    }

    public static ActivityCoverFragment create(ActivitiesListItem properties, OnClickListener onClickListener) {
        ActivityCoverFragment fragment = new ActivityCoverFragment();
        fragment.mActivity = properties;
        fragment.mOnClickListener = onClickListener;
        return fragment;
    }
}
