package org.happymtb.unofficial.fragment;

import org.happymtb.unofficial.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
	private SharedPreferences mPreferences;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);

        final Activity activity = getActivity();
		
		EditText userNameText = (EditText) activity.findViewById(R.id.settings_username);
		EditText passwordText = (EditText) activity.findViewById(R.id.Settings_Password);
		Spinner startPageSpinner = (Spinner) activity.findViewById(R.id.settings_startpage);
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		userNameText.setText(mPreferences.getString("username", ""));
		passwordText.setText(mPreferences.getString("password", ""));

		startPageSpinner.setSelection(mPreferences.getInt("startpage", 0));
		
		Button SaveButton = (Button) activity.findViewById(R.id.settings_save);
		SaveButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v)
        	{  
        		EditText userName = (EditText) activity.findViewById(R.id.settings_username);
        		Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                editor.putString("username", userName.getText().toString());

                EditText password = (EditText) activity.findViewById(R.id.Settings_Password);
                editor.putString("password", password.getText().toString());

                Spinner startPage = (Spinner) activity.findViewById(R.id.settings_startpage);
                editor.putInt("startpage", startPage.getSelectedItemPosition());
                editor.apply();
                
                Toast.makeText(activity, "Användarnamn och lösenord sparade", Toast.LENGTH_SHORT).show();
        	}	
        }); 		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.settings_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}			
}
