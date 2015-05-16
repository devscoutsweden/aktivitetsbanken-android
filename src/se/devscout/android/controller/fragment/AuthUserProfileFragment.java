package se.devscout.android.controller.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.model.UserPropertiesBean;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.UserProfile;

public class AuthUserProfileFragment extends ActivityBankFragment {

    private static final int[] DETAIL_VIEWS = {
            R.id.profileApiDescription,
            R.id.profileApiHeader,
            R.id.profileApiValue,
            R.id.profileRoleDescription
    };

    private boolean mShowDetails;
    private String mAPIKey;
    private String mDisplayName;
    private String mRole;
    private String mRoleDescription;
    private String mEmail;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mShowDetails", mShowDetails);
        outState.putString("mAPIKey", mAPIKey);
        outState.putString("mDisplayName", mDisplayName);
        outState.putString("mRole", mRole);
        outState.putString("mRoleDescription", mRoleDescription);
        outState.putString("mEmail", mEmail);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mShowDetails = savedInstanceState.getBoolean("mShowDetails");
            mAPIKey = savedInstanceState.getString("mAPIKey");
            mDisplayName = savedInstanceState.getString("mDisplayName");
            mRole = savedInstanceState.getString("mRole");
            mRoleDescription = savedInstanceState.getString("mRoleDescription");
            mEmail = savedInstanceState.getString("mEmail");
        }

        final View view = inflater.inflate(R.layout.auth_user_profile, container, false);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.profileShowDetails);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
                setDetailsVisibility(isChecked, AuthUserProfileFragment.this.getView());
            }
        });

        Button okButton = (Button) view.findViewById(R.id.buttonOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View button) {
                view.findViewById(R.id.progressView).setVisibility(View.VISIBLE);
                String updatedName = ((EditText) view.findViewById(R.id.profileNameValue)).getText().toString();
                String updatedEmail = ((EditText) view.findViewById(R.id.profileEmailValue)).getText().toString();
                new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... params) {
                        try {
                            getActivityBank().updateUserProfile(new UserPropertiesBean(
                                    params[0],
                                    null,
                                    0L,
                                    0L,
                                    false,
                                    params[1]
                            ));
                            return true;
                        } catch (UnauthorizedException e) {
                            Toast.makeText(getActivity(), getString(R.string.profileCouldNotSaveToAPI), Toast.LENGTH_SHORT);
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        view.findViewById(R.id.progressView).setVisibility(View.GONE);
                        if (success) {
                            getActivity().finish();
                        }
                    }
                }.execute(updatedName, updatedEmail);
            }
        });
        Button cancelButton = (Button) view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View button) {
                getActivity().finish();
            }
        });

        setDetailsVisibility(mShowDetails, view);

        if (isProfileLoaded()) {
            initView(view);
        } else {
            view.findViewById(R.id.progressView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contentView).setVisibility(View.GONE);

            new AsyncTask<Void, Void, UserProfile>() {
                @Override
                protected UserProfile doInBackground(Void... voids) {
                    return getActivityBank().readUserProfile();
                }

                @Override
                protected void onPostExecute(UserProfile profile) {
                    mAPIKey = profile.getAPIKey();
                    mDisplayName = profile.getDisplayName();
                    mEmail = profile.getEmailAddress();
                    mRole = profile.getRole();
                    mRoleDescription = getString(R.string.profileRoleDescriptionTemplate, profile.getRole(), "\n- " + TextUtils.join("\n- ", profile.getRolePermissions()));
                    initView(view);
                }
            }.execute();
        }

        return view;
    }

    private void initView(View view) {
        ((TextView) view.findViewById(R.id.profileApiValue)).setText(mAPIKey);
        ((EditText) view.findViewById(R.id.profileNameValue)).setText(mDisplayName);
        ((EditText) view.findViewById(R.id.profileEmailValue)).setText(mEmail);
        ((TextView) view.findViewById(R.id.profileRoleValue)).setText(mRole);
        ((TextView) view.findViewById(R.id.profileRoleDescription)).setText(mRoleDescription);
        view.findViewById(R.id.progressView).setVisibility(View.GONE);
        view.findViewById(R.id.contentView).setVisibility(View.VISIBLE);
    }

    private boolean isProfileLoaded() {
        return mRole != null;
    }

    private void setDetailsVisibility(boolean visible, View view) {
        for (int id : DETAIL_VIEWS) {
            view.findViewById(id).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }


    public static AuthUserProfileFragment create() {
        return new AuthUserProfileFragment();
    }

}
