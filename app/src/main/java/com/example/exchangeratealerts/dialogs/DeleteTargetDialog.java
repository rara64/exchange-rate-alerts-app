package com.example.exchangeratealerts.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private final DeleteTargetDialogCallback callback;
    private final Context context;

    public interface DeleteTargetDialogCallback {
        public void onSuccess();
    }

    public DeleteTargetDialog(String baseCurr, String quoteCurr, String target, Context ctx, DeleteTargetDialogCallback clbck) {
        baseCurrency = baseCurr;
        quoteCurrency = quoteCurr;
        targetValue = target;
        client = new APIClient();
        storage = new PrivateStorage(ctx);
        callback = clbck;
        context = ctx;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        AlertDialog dialog = builder.setMessage(getString(R.string.targets_delete_dialog_message, baseCurrency, quoteCurrency))
                .setPositiveButton(R.string.targets_delete_dialog_delete_button, null)
                .setNegativeButton(R.string.targets_delete_dialog_cancel_button, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        CurrencyTarget target = new CurrencyTarget();
                        target.baseCurrency = baseCurrency;
                        target.quoteCurrency = quoteCurrency;
                        target.targetValue = targetValue;
                        client.DeleteCurrencyTarget(storage.getTokenPref(), target, new APIClient.ResponseCallback<CurrencyTarget>() {
                            @Override
                            public void onSuccess(CurrencyTarget target) {
                                dialog.dismiss();
                                callback.onSuccess();
                            }

                            @Override
                            public void onFailure(int httpCode) {
                                Toast toast = Toast.makeText(context, getString(R.string.targets_delete_failed_service_issue), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    }
                });
            }
        });

        return dialog;
    }
}
