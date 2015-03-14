package org.pebeijer.happymtb.fragment;

import org.pebeijer.happymtb.R;
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
	private SharedPreferences preferences;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		EditText UserName = (EditText) getActivity().findViewById(R.id.Settings_Username);
		EditText Password = (EditText) getActivity().findViewById(R.id.Settings_Password);
		Spinner TextSize = (Spinner) getActivity().findViewById(R.id.Settings_TextSize);
		Spinner StartPage = (Spinner) getActivity().findViewById(R.id.Settings_StartPage);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		UserName.setText(preferences.getString("username", ""));	
		Password.setText(preferences.getString("password", ""));
		TextSize.setSelection(preferences.getInt("textsize", 0));
		StartPage.setSelection(preferences.getInt("startpage", 0));
		
		Button SaveButton = (Button) getActivity().findViewById(R.id.Settings_Save);
		SaveButton.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v)
        	{  
        		EditText UserName = (EditText) getActivity().findViewById(R.id.Settings_Username);
        		Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("username", UserName.getText().toString());              
                editor.commit();
                
                EditText Password = (EditText) getActivity().findViewById(R.id.Settings_Password);
        		editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("password", Password.getText().toString());              
                editor.commit();         
                
                Spinner TextSize = (Spinner) getActivity().findViewById(R.id.Settings_TextSize);
        		editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putInt("textsize", TextSize.getSelectedItemPosition());              
                editor.commit();                                         

                Spinner StartPage = (Spinner) getActivity().findViewById(R.id.Settings_StartPage);
        		editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putInt("startpage", StartPage.getSelectedItemPosition());              
                editor.commit();                
                
                Toast.makeText(getActivity(), "Anv�ndarnamn och l�senord sparade", Toast.LENGTH_SHORT).show();
        	}	
        }); 		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.settingsmenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}			
}
