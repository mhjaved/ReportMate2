package com.hasanjaved.reportmate.utility;

import android.widget.EditText;

public class InputValidator {

    public static boolean validLoginInput(EditText usernameEditText, EditText passwordEditText) {
        boolean isValid = true;

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate username
        if (username.isEmpty()) {
            usernameEditText.setError("Employee ID cannot be empty");
            isValid = false;
        } else {
            usernameEditText.setError(null);
        }

        // Validate password
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            isValid = false;
        } else if (password.length() < 5) {
            passwordEditText.setError("Password must be at least 5 characters");
            isValid = false;
        } else {
            passwordEditText.setError(null);
        }

        return isValid;
    }

    public static boolean validateEditText(EditText editText) {
        boolean allFilled = true;

        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            editText.setError("This field cannot be empty");
            allFilled = false;
        } else {
            editText.setError(null); // Clear previous error
        }

        return allFilled;
    }

}
