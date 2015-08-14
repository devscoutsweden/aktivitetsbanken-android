package se.devscout.android.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import se.devscout.android.R;
import se.devscout.android.model.ActivityKey;
import se.devscout.android.model.ActivityProperties;
import se.devscout.android.model.ActivityPropertiesBean;
import se.devscout.android.model.Rating;
import se.devscout.android.model.UserKey;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.http.UnauthorizedException;

public class ActivityEditView extends LinearLayout {
    private ActivityKey mActivityKey;
    private UserKey mUserKey;
    private ActivityBank mActivityBank;
    private ActivityProperties mActivityProperties;

    public ActivityEditView(Context context, ActivityKey activityKey, UserKey userKey, ActivityBank activityBank, ActivityProperties mActivityProperties) {
        super(context);
        mActivityKey = activityKey;
        mUserKey = userKey;
        mActivityBank = activityBank;
        this.mActivityProperties = mActivityProperties;
        init(context);
    }

    public ActivityEditView(Context context, AttributeSet attrs, ActivityKey activityKey, UserKey userKey, ActivityBank activityBank, ActivityProperties mActivityProperties) {
        super(context, attrs);
        mActivityKey = activityKey;
        mUserKey = userKey;
        mActivityBank = activityBank;
        this.mActivityProperties = mActivityProperties;
        init(context);
    }

    public ActivityEditView(Context context, AttributeSet attrs, int defStyle, ActivityKey activityKey, UserKey userKey, ActivityBank activityBank, ActivityProperties mActivityProperties) {
        super(context, attrs, defStyle);
        mActivityKey = activityKey;
        mUserKey = userKey;
        mActivityBank = activityBank;
        this.mActivityProperties = mActivityProperties;
        init(context);
    }

    private void init(final Context context) {
        final LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_edit_card, this, true);

        view.findViewById(R.id.activityEditControls).setVisibility(VISIBLE);
        view.findViewById(R.id.activityEditProgress).setVisibility(GONE);

        ((CheckBox)view.findViewById(R.id.activityEditFeaturedCheckbox)).setChecked(mActivityProperties.isFeatured());
        ((CheckBox)view.findViewById(R.id.activityEditFeaturedCheckbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mActivityProperties.setFeatured(isChecked);
            }
        });

        view.findViewById(R.id.activityEditSaveButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View buttonView) {
                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        try {
                            mActivityBank.updateActivity(mActivityKey, mActivityProperties);
                        } catch (UnauthorizedException e) {
                            return R.string.could_not_save_activity_unauthorized;
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        view.findViewById(R.id.activityEditControls).setVisibility(GONE);
                        view.findViewById(R.id.activityEditProgress).setVisibility(VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        view.findViewById(R.id.activityEditControls).setVisibility(VISIBLE);
                        view.findViewById(R.id.activityEditProgress).setVisibility(GONE);
                        if (result != null) {
                            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }
        });

//        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
    }

}
