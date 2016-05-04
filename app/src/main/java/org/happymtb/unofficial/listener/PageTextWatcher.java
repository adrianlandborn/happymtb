package org.happymtb.unofficial.listener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Adrian on 19/08/2015.
 */
public class PageTextWatcher implements TextWatcher {

	AlertDialog mGoToDialog;
    int max;

	public PageTextWatcher(final AlertDialog goToDialog, int maxPages) {
        mGoToDialog = goToDialog;
        max = maxPages;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int page = -1;
        try {
            page = Integer.parseInt(s.toString().trim());
        } catch (NumberFormatException e) {

        }
        if (page > 0 && page <= max) {
            mGoToDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            mGoToDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
