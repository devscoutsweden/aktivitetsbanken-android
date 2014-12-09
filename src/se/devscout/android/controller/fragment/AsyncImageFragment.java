package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.LogUtil;
import se.devscout.android.view.AsyncImageBean;
import se.devscout.android.view.AsyncImageView;

public class AsyncImageFragment extends ActivityBankFragment implements View.OnClickListener {

    public static interface OnClickListener {

        void onImageClick(View view, AsyncImageBean localActivity, Context ctx);
    }

    private AsyncImageBean mAsyncImageBean;
    private int mSize;

    private OnClickListener mOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mAsyncImageBean = (AsyncImageBean) savedInstanceState.getSerializable("mAsyncImageBean");
            mSize = savedInstanceState.getInt("mSize");
        } else {
            mSize = container.getContext().getResources().getDisplayMetrics().widthPixels;
        }
        AsyncImageView view = new AsyncImageView(getActivity());

        view.setImage(
                mAsyncImageBean,
                ((SingleFragmentActivity) getActivity()).getBackgroundTasksHandlerThread());
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
        outState.putSerializable("mAsyncImageBean", mAsyncImageBean);
        outState.putInt("mSize", mSize);
        LogUtil.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onImageClick(view, mAsyncImageBean, getActivity());
        }
    }

    public static AsyncImageFragment create(AsyncImageBean properties, OnClickListener onClickListener) {
        AsyncImageFragment fragment = new AsyncImageFragment();
        fragment.mAsyncImageBean = properties;
        fragment.mOnClickListener = onClickListener;
        return fragment;
    }
}
