package com.example.exchangeratealerts.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.work.WorkManager;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.activities.LoginRegisterActivity;
import com.example.exchangeratealerts.activities.TargetsActivity;
import com.example.exchangeratealerts.modules.PrivateStorage;

public class SettingsDialog extends DialogFragment {
    private final Context context;
    private final PrivateStorage storage;

    public SettingsDialog(Context ctx, PrivateStorage strg) {
        context = ctx;
        storage = strg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.settings_dialog_logout_question))
                .setPositiveButton(R.string.settings_dialog_logout_submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WorkManager.getInstance(context).cancelAllWork();
                        storage.setPasswordPref("");
                        Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(R.string.settings_dialog_logout_cancel, null);

        return builder.create();
    }
}

