package com.example.exchangeratealerts.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.models.CurrencyTarget;
import com.example.exchangeratealerts.modules.APIClient;
import com.example.exchangeratealerts.modules.PrivateStorage;

public class DeleteTargetDialog extends DialogFragment {
    private final String baseCurrency;
    private final String quoteCurrency;
    private final String targetValue;
    private final APIClient client;
    private final PrivateStorage storage;

    private DeleteTargetDialogCallback callback;

    public interface DeleteTargetDialogCallback {
        public void onSuccess();
    }

    public DeleteTargetDialog(String baseCurr, String quoteCurr, String target, Context context, DeleteTargetDialogCallback clbck) {
        baseCurrency = baseCurr;
        quoteCurrency = quoteCurr;
        targetValue = target;
        client = new APIClient();
        storage = new PrivateStorage(context);
        callback = clbck;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.targets_delete_dialog_message, baseCurrency, quoteCurrency))
                .setPositiveButton(R.string.targets_delete_dialog_delete_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CurrencyTarget target = new CurrencyTarget();
                        target.baseCurrency = baseCurrency;
                        target.quoteCurrency = quoteCurrency;
                        target.targetValue = targetValue;
                        client.DeleteCurrencyTarget(storage.getTokenPref(), target, new APIClient.DeleteCurrencyTargetCallback() {
                            @Override
                            public void onSuccess(CurrencyTarget target) {
                                callback.onSuccess();
                            }

                            @Override
                            public void onFailure(int httpCode) {

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.targets_delete_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }
}
