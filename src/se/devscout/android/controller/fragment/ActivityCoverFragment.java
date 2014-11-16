package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.view.ActivitiesListItem;
import se.devscout.android.view.ActivityCoverView;

public class ActivityCoverFragment extends ActivityBankFragment implements View.OnClickListener {

    public static interface OnClickListener {

        void onImageClick(View view, ActivitiesListItem localActivity, Context ctx);
    }

    private ActivitiesListItem mActivity;

    private OnClickListener mOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityCoverView view = new ActivityCoverView(getActivity());
        view.init(mActivity.getCoverMedia(), mActivity.getName(), (SingleFragmentActivity) getActivity());

        if (mOnClickListener != null) {
            view.setOnClickListener(this);
        }

        return view;
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
