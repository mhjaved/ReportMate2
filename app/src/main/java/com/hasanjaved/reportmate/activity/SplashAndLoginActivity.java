package com.hasanjaved.reportmate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.utility.Utility;

public class SplashAndLoginActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private Handler handler;
    private Runnable runnable;

    private View layoutLogin;
    private EditText etEmployeeId, etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        layoutLogin = findViewById(R.id.layoutLogin);


        handler = new Handler();
        runnable = () -> {
            showLoginPage();
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);

        findViewById(R.id.btnBack).setOnClickListener(view -> finish());


        etEmployeeId = findViewById(R.id.etEmployeeId);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(view -> {

            if (etEmployeeId.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty())
                Utility.showToast(this, "provide valid information");
            else {
                if (etPassword.getText().toString().equals("35112")) {
                    Employee employee = new Employee();
                    employee.setEmployeeId(etEmployeeId.getText().toString());
                    Utility.saveEmployee(this, employee);
                    gotoHomeActivity();
                } else Utility.showToast(this, "wrong password");


            }
        });

    }

    private void showLoginPage() {

        if (Utility.getEmployee(this) != null)
            gotoHomeActivity();
        else layoutLogin.setVisibility(View.VISIBLE);

    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(SplashAndLoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

