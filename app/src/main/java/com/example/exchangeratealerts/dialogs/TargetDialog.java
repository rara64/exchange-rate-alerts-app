package com.example.exchangeratealerts.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private final TargetDialogCallback callback;
    private final Context context;
    public interface TargetDialogCallback {
        public void onSuccess();
    }

    public TargetDialog(String baseCurr, String quoteCurr, String targetVal, Context ctx, TargetDialogCallback clbk){
        baseCurrency = baseCurr;
        quoteCurrency = quoteCurr;
        targetValue = targetVal;
        client = new APIClient();
        storage = new PrivateStorage(ctx);
        callback = clbk;
        context = ctx;
    }

    @Override
    @NotNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        AlertDialog dialog = builder.setView(dialogView)
                .setPositiveButton(R.string.targets_dialog_submit_button, null)
                .setNegativeButton(R.string.targets_dialog_cancel_button, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
             @Override
             public void onShow(DialogInterface dialog) {
                 Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                 button.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick (View view){
                         CurrencyTarget target = new CurrencyTarget();
                         target.baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
                         target.quoteCurrency = quoteCurrencySpinner.getSelectedItem().toString();
                         target.targetValue = beforeDecimalPicker.getValue() + "." + afterDecimalPicker.getValue();

                         if (target.baseCurrency.equals(target.quoteCurrency)) {
                             Toast toast = Toast.makeText(context, getString(R.string.targets_dialog_same_currency_error), Toast.LENGTH_LONG);
                             toast.show();
                             return;
                         }

                         client.SetCurrencyTarget(storage.getTokenPref(), target, new APIClient.ResponseCallback<CurrencyTarget>() {
                             @Override
                             public void onSuccess(CurrencyTarget target) {
                                 dialog.dismiss();
                                 callback.onSuccess();
                             }

                             @Override
                             public void onFailure(int httpCode) {
                                 Toast toast = Toast.makeText(context, getString(R.string.targets_dialog_service_issue), Toast.LENGTH_LONG);
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
