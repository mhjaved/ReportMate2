package com.hasanjaved.reportmate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.hasanjaved.reportmate.R;

public class SplashAndLoginActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private Handler handler;
    private Runnable runnable;

    private View layoutLogin;


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

        findViewById(R.id.btnLogin).setOnClickListener(view -> gotoHomeActivity());

    }

    private void showLoginPage(){

        layoutLogin.setVisibility(View.VISIBLE);

    }

    private void gotoHomeActivity(){
        Intent intent = new Intent(SplashAndLoginActivity.this , HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

