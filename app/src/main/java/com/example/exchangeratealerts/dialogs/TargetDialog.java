package com.example.exchangeratealerts.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.models.CurrencyTarget;
import com.example.exchangeratealerts.modules.APIClient;
import com.example.exchangeratealerts.modules.PrivateStorage;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TargetDialog extends DialogFragment {
    private final String baseCurrency;
    private final String quoteCurrency;
    private final String targetValue;
    private final APIClient client;
    private final PrivateStorage storage;

    private TargetDialogCallback callback;

    public interface TargetDialogCallback {
        public void onSuccess();
    }

    public TargetDialog(String baseCurr, String quoteCurr, String targetVal, Context context, TargetDialogCallback clbk){
        baseCurrency = baseCurr;
        quoteCurrency = quoteCurr;
        targetValue = targetVal;
        client = new APIClient();
        storage = new PrivateStorage(context);
        callback = clbk;
    }

    @Override
    @NotNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        View dialogView = inflater.inflate(R.layout.targets_edit_dialog, null);
        NumberPicker beforeDecimalPicker = dialogView.findViewById(R.id.editTargetValueBeforeDecimal);
        NumberPicker afterDecimalPicker = dialogView.findViewById(R.id.editTargetValueAfterDecimal);

        beforeDecimalPicker.setMinValue(0);
        beforeDecimalPicker.setMaxValue(9999);
        beforeDecimalPicker.setValue(1);

        afterDecimalPicker.setMinValue(0);
        afterDecimalPicker.setMaxValue(999);
        afterDecimalPicker.setValue(0);

        if (!targetValue.isEmpty()) {
            String[] splitValue = targetValue.split("\\.");
            beforeDecimalPicker.setValue(Integer.parseInt(splitValue[0]));
            afterDecimalPicker.setValue(Integer.parseInt(splitValue[1]));
        }

        List<String> currencyCodes = List.of(
                "AUD", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "EUR", "GBP", "HKD",
                "HUF", "IDR", "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK",
                "NZD", "PHP", "PLN", "RON", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
        );

        Spinner baseCurrencySpinner = dialogView.findViewById(R.id.editBaseCurrencySpinner);
        Spinner quoteCurrencySpinner = dialogView.findViewById(R.id.editQuoteCurrencySpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                dialogView.getContext(),
                android.R.layout.simple_spinner_item,
                currencyCodes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        baseCurrencySpinner.setAdapter(adapter);
        quoteCurrencySpinner.setAdapter(adapter);

        int baseCurrencyIndex = currencyCodes.indexOf(baseCurrency);
        int quoteCurrencyIndex = currencyCodes.indexOf(quoteCurrency);

        if (baseCurrencyIndex != -1)
            baseCurrencySpinner.setSelection(baseCurrencyIndex);

        if (quoteCurrencyIndex != -1)
            quoteCurrencySpinner.setSelection(quoteCurrencyIndex);

        TextView dialogTitle = dialogView.findViewById(R.id.editTargetDialogTitle);

        if (baseCurrency.isEmpty() && quoteCurrency.isEmpty())
            dialogTitle.setText(R.string.targets_dialog_title_add_variant);

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.targets_dialog_submit_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CurrencyTarget target = new CurrencyTarget();
                        target.baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
                        target.quoteCurrency = quoteCurrencySpinner.getSelectedItem().toString();
                        target.targetValue = beforeDecimalPicker.getValue() + "." + afterDecimalPicker.getValue();

                        if (target.baseCurrency.equals(target.quoteCurrency)) {
                            Toast toast = Toast.makeText(dialogView.getContext(), "UPS!", Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }

                        client.SetCurrencyTarget(storage.getTokenPref(), target, new APIClient.SetCurrencyTargetCallback() {
                            @Override
                            public void onSuccess(CurrencyTarget target) {
                                callback.onSuccess();
                            }

                            @Override
                            public void onFailure(int httpCode) {
                                Toast toast = Toast.makeText(dialogView.getContext(), "UPS!", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.targets_dialog_cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
