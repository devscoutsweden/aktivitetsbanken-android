package se.devscout.android.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import se.devscout.android.R;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.model.User;
import se.devscout.android.model.UserProperties;
import se.devscout.android.model.repo.ActivityBank;
import se.devscout.android.model.repo.remote.ServerAPICredentials;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.IdentityProvider;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.util.auth.CredentialsManager;

public class AuthenticationView extends LinearLayout implements CredentialsManager.Listener {

    @Override
    public void onAuthenticationStatusChange(final CredentialsManager.State currentState) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refresh(currentState);
            }
        });
    }

    @Override
    public void onAuthenticated(IdentityProvider provider, ServerAPICredentials data, UserProperties userProperties) {
    }

    public AuthenticationView(Context context, boolean isListContentHeight) {
        super(context);
        init(context, isListContentHeight);
    }

    public AuthenticationView(Context context, AttributeSet attrs, boolean isListContentHeight) {
        super(context, attrs);
        init(context, isListContentHeight);
    }

    public AuthenticationView(Context context, AttributeSet attrs, int defStyle, boolean isListContentHeight) {
        super(context, attrs, defStyle);
        init(context, isListContentHeight);
    }

    private void init(final Context context, boolean isListContentHeight) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.authentication, this, true);

        SignInButton signInButton = (SignInButton) findViewById(R.id.auth_google_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof SingleFragmentActivity) {
                    SingleFragmentActivity activity = (SingleFragmentActivity) context;
                    CredentialsManager.getInstance(activity).logIn(activity, false, IdentityProvider.GOOGLE);
                }
            }
        });

        TextView logOutButton = (TextView) findViewById(R.id.auth_log_out_button);
        logOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof SingleFragmentActivity) {
                    SingleFragmentActivity activity = (SingleFragmentActivity) context;
                    CredentialsManager.getInstance(activity).logOut(activity, false);
                }
            }
        });

        initParentCollapseButton(R.id.auth_logged_out_message_collapse_button, "auth_logged_out_message");
        initParentCollapseButton(R.id.auth_logged_in_message_collapse_button, "auth_logged_in_message");

        refresh(CredentialsManager.getInstance(context).getState());

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
    }

    private void initParentCollapseButton(final int buttonId, final String dismissedId) {
        final View view = findViewById(buttonId);
        final View viewToCollapse = (View) view.getParent();
        if (PreferencesUtil.isMessageDismissed(getContext(), dismissedId)) {
            viewToCollapse.setVisibility(GONE);
        } else {
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View clickedView) {
                    PreferencesUtil.setMessageDismissed(getContext(), dismissedId);
                    final int height = viewToCollapse.getMeasuredHeight();
                    ValueAnimator animator = ValueAnimator.ofInt(height, 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int value = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = viewToCollapse.getLayoutParams();
                            layoutParams.height = value;
                            view.getParent().requestLayout();
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            viewToCollapse.setVisibility(GONE);
                        }
                    });
                    animator.start();
                }
            });
        }
    }

    private void refresh(CredentialsManager.State state) {
        findViewById(R.id.auth_logged_in_container).setVisibility(state.isLoggedIn() && !state.isWorking() ? VISIBLE : GONE);
        findViewById(R.id.auth_logged_out_container).setVisibility(!state.isLoggedIn() && !state.isWorking() ? VISIBLE : GONE);
        findViewById(R.id.auth_progress_container).setVisibility(state.isWorking() ? VISIBLE : GONE);

        switch (state) {
            case LOGGED_IN:
                TextView loggedInMessage = (TextView) findViewById(R.id.auth_logged_in_text);
                ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
                User user = activityBank.readUser(CredentialsManager.getInstance(getContext()).getCurrentUser());
                String name = user.getDisplayName() != null ? user.getDisplayName() : user.getName();
                loggedInMessage.setText(getContext().getString(R.string.auth_logged_in_promo, name != null && !UserProperties.USER_NAME_ANONYMOUS.equals(name) ? " " + name : ""));
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        CredentialsManager.getInstance(getContext()).addListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        CredentialsManager.getInstance(getContext()).removeListener(this);
        super.onDetachedFromWindow();
    }
}
