package com.example.exchangeratealerts.adapters;

import static android.app.PendingIntent.getActivity;
import static android.view.View.GONE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.activities.TargetsActivity;
import com.example.exchangeratealerts.dialogs.DeleteTargetDialog;
import com.example.exchangeratealerts.dialogs.TargetDialog;
import com.example.exchangeratealerts.models.CurrencyAlert;
import com.example.exchangeratealerts.models.CurrencyTarget;
import com.example.exchangeratealerts.modules.APIClient;

import java.util.Arrays;

public class TargetsRecyclerViewAdapter extends RecyclerView.Adapter<TargetsRecyclerViewAdapter.ViewHolder> {

    private CurrencyTarget[] currencyTargets;
    private CurrencyAlert[] currencyAlerts;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView baseCurrencyCode;
        private final TextView quoteCurrencyCode;
        private final TextView currentValue;
        private final TextView targetValue;
        private final ImageButton targetEditButton;
        private final LinearLayout currentValueRow;
        private final LinearLayout targetValueRow;
        private final LinearLayout statusRibbon;
        private final ImageButton targetDeleteButton;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            baseCurrencyCode = (TextView) view.findViewById(R.id.baseCurrencyCode);
            quoteCurrencyCode = (TextView) view.findViewById(R.id.quoteCurrencyCode);
            currentValue = (TextView) view.findViewById(R.id.currentValue);
            targetValue = (TextView) view.findViewById(R.id.targetValue);
            targetEditButton = (ImageButton) view.findViewById(R.id.targetEditButton);
            currentValueRow = view.findViewById(R.id.currentValueRow);
            targetValueRow = view.findViewById(R.id.targetValueRow);
            statusRibbon = view.findViewById(R.id.statusRibbon);
            targetDeleteButton = view.findViewById(R.id.targetDeleteButton);
        }

        public TextView getBaseCurrencyCode() {
            return baseCurrencyCode;
        }

        public TextView getQuoteCurrencyCode() {
            return quoteCurrencyCode;
        }

        public TextView getCurrentValue() {
            return currentValue;
        }

        public TextView getTargetValue() {
            return targetValue;
        }

        public ImageButton getTargetEditButton() {
            return targetEditButton;
        }

        public LinearLayout getCurrentValueRow() {
            return currentValueRow;
        }

        public LinearLayout getTargetValueRow() {
            return targetValueRow;
        }

        public LinearLayout getStatusRibbon() {
            return statusRibbon;
        }

        public ImageButton getTargetDeleteButton() {
            return targetDeleteButton;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public TargetsRecyclerViewAdapter(CurrencyTarget[] targets, CurrencyAlert[] alerts) {
        currencyTargets = targets;
        currencyAlerts = alerts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.targets_recycler_item, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.getBaseCurrencyCode().setText(currencyTargets[position].baseCurrency);
        viewHolder.getQuoteCurrencyCode().setText(currencyTargets[position].quoteCurrency);
        viewHolder.getTargetValue().setText(currencyTargets[position].targetValue + " " + currencyTargets[position].quoteCurrency);

        CurrencyAlert currencyAlert = Arrays.stream(currencyAlerts).filter(alert ->
                alert.targetValue.equals(currencyTargets[position].targetValue) &&
                alert.baseCurrency.equals(currencyTargets[position].baseCurrency) &&
                alert.quoteCurrency.equals(currencyTargets[position].quoteCurrency)).findFirst().orElse(null);

        if (currencyAlert != null) {
            String value = currencyAlert.currentValue;
            viewHolder.getCurrentValue().setText(value.substring(0, Math.min(value.length(), 6)) +  " " + currencyTargets[position].quoteCurrency);
            viewHolder.getStatusRibbon().setBackground(viewHolder.getStatusRibbon().getResources().getDrawable(R.color.alert_recycler_item));
        }
        else {
            viewHolder.getCurrentValueRow().setVisibility(GONE);
        }

        viewHolder.getTargetEditButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TargetDialog dialog = new TargetDialog(
                        currencyTargets[position].baseCurrency,
                        currencyTargets[position].quoteCurrency,
                        currencyTargets[position].targetValue,
                        v.getContext(),
                        new TargetDialog.TargetDialogCallback() {
                            @Override
                            public void onSuccess() {
                                if (v.getContext() instanceof TargetsActivity) {
                                    ((TargetsActivity) v.getContext()).updateCurrencyTargetList(false);
                                }
                            }
                        });
                dialog.show(((AppCompatActivity)v.getContext()).getSupportFragmentManager(), "EditTargetDialog");
            }
        });

        viewHolder.getTargetDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteTargetDialog dialog = new DeleteTargetDialog(
                        currencyTargets[position].baseCurrency,
                        currencyTargets[position].quoteCurrency,
                        currencyTargets[position].targetValue,
                        v.getContext(),
                        new DeleteTargetDialog.DeleteTargetDialogCallback() {
                            @Override
                            public void onSuccess() {
                                if (v.getContext() instanceof TargetsActivity) {
                                    ((TargetsActivity) v.getContext()).updateCurrencyTargetList(false);
                                }
                            }
                        });
                dialog.show(((AppCompatActivity)v.getContext()).getSupportFragmentManager(), "DeleteTargetDialog");

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currencyTargets.length;
    }
}
