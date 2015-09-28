package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;

/**
 * Created by Adrian on 30/05/2015.
 */
public class KoSSortDialogFragment extends DialogFragment {
    private SharedPreferences mPreferences;

    public KoSSortDialogFragment() {
    }

	public interface SortDialogDataListener {
		void onSortData(int attr, int order);
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		final MainActivity activity = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.kos_sort, null);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        // Tid, Rubrik, Omrade, Kategori, Pris
        final Spinner attSpinner = (Spinner) view.findViewById(R.id.kos_dialog_sort_attribute);
        attSpinner.setSelection(mPreferences.getInt(KoSListFragment.SORT_ATTRIBUTE_POS, 0));
        // Stigande, Fallande
        final Spinner orderSpinner = (Spinner) view.findViewById(R.id.kos_dialog_sort_order);
        orderSpinner.setSelection(mPreferences.getInt(KoSListFragment.SORT_ORDER_POS, 0));

        builder.setView(view);
        builder.setPositiveButton(R.string.sort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                activity.onSortData(attSpinner.getSelectedItemPosition(), orderSpinner.getSelectedItemPosition());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                KoSSortDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
	}
}
