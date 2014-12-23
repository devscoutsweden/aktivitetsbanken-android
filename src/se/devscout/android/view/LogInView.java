package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.gms.common.SignInButton;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;
//TODO: It might be cleaner to create an AbstractActivityBankListener instead of implementing ActivityBankListener
public class LogInView extends LinearLayout implements ActivityBankListener {
    public LogInView(Context context, boolean isListContentHeight) {
        super(context);
        init(context, isListContentHeight);
    }

    public LogInView(Context context, AttributeSet attrs, boolean isListContentHeight) {
        super(context, attrs);
        init(context, isListContentHeight);
    }

    public LogInView(Context context, AttributeSet attrs, int defStyle, boolean isListContentHeight) {
        super(context, attrs, defStyle);
        init(context, isListContentHeight);
    }

    private void init(final Context context, boolean isListContentHeight) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.log_in_toast, this, true);

        refresh(context, ActivityBankFactory.getInstance(getContext()).isLoggedIn());

//        ActivityBankFactory.getInstance(getContext()).addListener(this);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
    }

    private void refresh(final Context context, boolean loggedIn) {
        Button logOutButton = (Button) findViewById(R.id.auth_log_out_button);
        logOutButton.setVisibility(loggedIn ? VISIBLE : GONE);
        logOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof SingleFragmentActivity) {
                    SingleFragmentActivity activity = (SingleFragmentActivity) context;
                    activity.signOutFromGplus();
                }
            }
        });
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setVisibility(loggedIn ? GONE : VISIBLE);
        if (loggedIn) {

        } else {
            signInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof SingleFragmentActivity) {
                        SingleFragmentActivity activity = (SingleFragmentActivity) context;
                        activity.signInWithGplus();
                    }
                }
            });
        }
    }

    @Override
    protected void onAttachedToWindow() {
        ActivityBankFactory.getInstance(getContext()).addListener(this);
        super.onAttachedToWindow();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onDetachedFromWindow() {
        ActivityBankFactory.getInstance(getContext()).removeListener(this);
        super.onDetachedFromWindow();    //To change body of overridden methods use File | Settings | File Templates.
    }

/*
    @Override
    protected Parcelable onSaveInstanceState() {
        ActivityBankFactory.getInstance(getContext()).removeListener(this);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        ActivityBankFactory.getInstance(getContext()).addListener(this);
        refresh(getContext(), ActivityBankFactory.getInstance(getContext()).isLoggedIn());
        super.onRestoreInstanceState(state);
    }
*/

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
    }

    @Override
    public void onLogIn() {
        if (getContext() instanceof SingleFragmentActivity) {
            SingleFragmentActivity activity = (SingleFragmentActivity) getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh(getContext(), true);
                }
            });
        }
    }

    @Override
    public void onLogOut() {
        if (getContext() instanceof SingleFragmentActivity) {
            SingleFragmentActivity activity = (SingleFragmentActivity) getContext();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh(getContext(), false);
                }
            });
        }
    }

    @Override
    public void onAsyncException(Exception e) {
    }
}
