package org.happymtb.unofficial;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.happymtb.unofficial.fragment.ForumListFragment;

/**
 * Created by Adrian on 25/10/2015.
 */
public class SettingsActivity extends AppCompatActivity {
    public static String TAG = "settimgs_frag";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    public static final String START_PAGE = "startpage";

    private SharedPreferences mPreferences;
    private Spinner mStartPageSpinner;
    private EditText mUserNameText;
    private EditText mPasswordText;
    private TextView mVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_frame);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserNameText = findViewById(R.id.settings_username);
        mPasswordText = findViewById(R.id.settings_password);
        mStartPageSpinner = findViewById(R.id.settings_startpage);
        mVersion = findViewById(R.id.version);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putInt(SettingsActivity.START_PAGE, mStartPageSpinner.getSelectedItemPosition());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            mVersion.setText("Version: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
