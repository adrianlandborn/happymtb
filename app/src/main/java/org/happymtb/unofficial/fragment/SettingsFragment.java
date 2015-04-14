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
		
		EditText UserName = (EditText) activity.findViewById(R.id.Settings_Username);
		EditText Password = (EditText) activity.findViewById(R.id.Settings_Password);
		Spinner TextSize = (Spinner) activity.findViewById(R.id.Settings_TextSize);
		Spinner StartPage = (Spinner) activity.findViewById(R.id.Settings_StartPage);
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		UserName.setText(mPreferences.getString("username", ""));
		Password.setText(mPreferences.getString("password", ""));
		TextSize.setSelection(mPreferences.getInt("textsize", 0));
		StartPage.setSelection(mPreferences.getInt("startpage", 0));
		
		Button SaveButton = (Button) activity.findViewById(R.id.Settings_Save);
		SaveButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v)
        	{  
        		EditText userName = (EditText) activity.findViewById(R.id.Settings_Username);
        		Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                editor.putString("username", userName.getText().toString());

                EditText password = (EditText) activity.findViewById(R.id.Settings_Password);
                editor.putString("password", password.getText().toString());

                Spinner textSize = (Spinner) activity.findViewById(R.id.Settings_TextSize);
                editor.putInt("textsize", textSize.getSelectedItemPosition());

                Spinner startPage = (Spinner) activity.findViewById(R.id.Settings_StartPage);
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
