package org.happymtb.unofficial.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.SettingsActivity;

public class SettingsFragment extends Fragment {
    public static String TAG = "settimgs_frag";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USE_LARGE_IMAGES = "use_large_images";

	private SharedPreferences mPreferences;
    private Spinner mStartPageSpinner;
	private EditText mUserNameText;
	private EditText mPasswordText;
	private TextView mVersion;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);

        final Activity activity = getActivity();
		
		mUserNameText = activity.findViewById(R.id.settings_username);
		mPasswordText = activity.findViewById(R.id.settings_password);
		mStartPageSpinner = activity.findViewById(R.id.settings_startpage);
        mVersion = activity.findViewById(R.id.version);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		mUserNameText.setText(mPreferences.getString(USERNAME, ""));
        mUserNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("username", s.toString().trim());
                editor.putString(ForumListFragment.COOKIE_NAME, "");
                editor.putString(ForumListFragment.COOKIE_VALUE, "");
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
		mPasswordText.setText(mPreferences.getString(PASSWORD, ""));
        mPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("password", s.toString().trim());
                editor.putString(ForumListFragment.COOKIE_NAME, "");
                editor.putString(ForumListFragment.COOKIE_VALUE, "");
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

		mStartPageSpinner.setSelection(mPreferences.getInt(SettingsActivity.START_PAGE, 0));
		mStartPageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                editor.putInt(SettingsActivity.START_PAGE, mStartPageSpinner.getSelectedItemPosition());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            mVersion.setText("Version: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_frame, container, false);
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.settings_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

}
