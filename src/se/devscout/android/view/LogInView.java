package se.devscout.android.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));

    }

    private void refresh(final Context context, boolean loggedIn) {
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setVisibility(loggedIn ? VISIBLE : GONE);
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
    protected Parcelable onSaveInstanceState() {
        ActivityBankFactory.getInstance(getContext()).removeListener(this);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        ActivityBankFactory.getInstance(getContext()).addListener(this);
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
    }

    @Override
    public void onLogIn() {
        refresh(getContext(), true);
    }

    @Override
    public void onLogOut() {
        refresh(getContext(), false);
    }
}
