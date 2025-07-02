package com.hasanjaved.reportmate.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hasanjaved.reportmate.R;

public  class PopupManager {

    public static void showConfirmCircuitPopup(Context context,ConfirmCircuitList confirmCircuitList ) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.popup_layout, null);

        // Get references to views
        TextView titleText = customView.findViewById(R.id.popup_title);
        TextView messageText = customView.findViewById(R.id.popup_message);
        Button yesButton = customView.findViewById(R.id.btn_yes);
        Button noButton = customView.findViewById(R.id.btn_no);

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setCancelable(true)
                .create();

        // Set content
        titleText.setText(R.string.confirm_action);
        messageText.setText(R.string.are_you_sure_you_want_to_save_circuits);

        // Set button actions
        yesButton.setOnClickListener(v -> {
            // YES action
            Toast.makeText(context, R.string.report_saved_to_ongoing_list, Toast.LENGTH_SHORT).show();
            confirmCircuitList.confirmed();
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            // NO action
//            Toast.makeText(context, "No clicked - Action cancelled", Toast.LENGTH_SHORT).show();
            confirmCircuitList.cancelled();
            dialog.dismiss();
        });

        // Show dialog
        dialog.show();
    }

    private void handleYesAction() {
        // Add your YES action logic here
        // Example: delete item, save data, etc.
    }

    private void handleNoAction() {
        // Add your NO action logic here
        // Example: cancel operation, show different screen, etc.
    }

    public interface ConfirmCircuitList{
        void confirmed();
        void cancelled();

    }
}
