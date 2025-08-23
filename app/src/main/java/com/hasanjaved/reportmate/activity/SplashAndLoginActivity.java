package com.hasanjaved.reportmate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hasanjaved.reportmate.R;
import com.hasanjaved.reportmate.model.Employee;
import com.hasanjaved.reportmate.model.LoginResponse;
import com.hasanjaved.reportmate.network.ApiInterface;
import com.hasanjaved.reportmate.network.RetrofitInstance;
import com.hasanjaved.reportmate.utility.InputValidator;
import com.hasanjaved.reportmate.utility.PopupManager;
import com.hasanjaved.reportmate.utility.TimeValidator;
import com.hasanjaved.reportmate.utility.Utility;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashAndLoginActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private Handler handler;
    private Runnable runnable;

    private View layoutLogin;
    private ImageView ivTogglePassword;
    private EditText etEmployeeId, etPassword;
    final boolean[] isPasswordVisible = {false};


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
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        ivTogglePassword.setOnClickListener(view -> {

            if (isPasswordVisible[0]) {
                // Hide password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_close_password);
            } else {
                // Show password
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_show_password);
            }
            // Move cursor to the end
            etPassword.setSelection(etPassword.length());
            isPasswordVisible[0] = !isPasswordVisible[0];

        });
        findViewById(R.id.imgLogo).setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            PopupManager.showAndroidId(this, clipboard);
        });
        findViewById(R.id.btnLogin).setOnClickListener(view -> {

//            if (TimeValidator.isTimeValid(this))
//                Utility.showToast(this,"valid");
//            else {
//                Utility.showToast(this,"not valid, setting for 2 min");
//                TimeValidator.saveStartTime(this,120);
//            }


//------------------------------------- main login system ---------------------------------------
//-----------------------------------------------------------------------------------------------
            if (InputValidator.validLoginInput(etEmployeeId, etPassword)) {
                login(etEmployeeId.getText().toString().trim(),
                        etPassword.getText().toString().trim());
            }
//------------------------------------- main login system ends---------------------------------------


//            if (etEmployeeId.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty())
//                Utility.showToast(this, "provide valid information");
//            else {
//                if (etPassword.getText().toString().equals("35112")) {
//                    Employee employee = new Employee();
//                    employee.setEmployeeId(etEmployeeId.getText().toString());
//                    Utility.saveEmployee(this, employee);
//                    gotoHomeActivity();
//                } else Utility.showToast(this, "wrong password");
//            }
        });

    }

    private void showLoginPage() {

        if (TimeValidator.isTimeValid(this) && Utility.getEmployee(this) != null) {
            gotoHomeActivity();
        } else layoutLogin.setVisibility(View.VISIBLE);

    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(SplashAndLoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoHistoryActivity() {
        Intent intent = new Intent(SplashAndLoginActivity.this, HistoryActivity.class);
        intent.putExtra(Utility.HISTORY_FRAGMENT_TOKEN, Utility.HISTORY_FRAGMENT_ONGOING);

        startActivity(intent);
        finish();
    }

    private void login(String employeeId, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        HashMap<String, String> map = new HashMap<>();
//        {
//            "username":"ocr_admin",
//                "password":"ocr_admin",
//                "device_id":"12388"
//        }
        map.put("username", employeeId);
        map.put("password", password);
        map.put("device_id", Utility.getDeviceId(this));


        Utility.showLog(map.toString());

        RetrofitInstance.getApiInterface().login(map).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                LoginResponse loginResponse = response.body();
                if (loginResponse != null) {
                    Utility.showLog(loginResponse.toString());
                    if (loginResponse.getStatus() != null && loginResponse.getExpiresIn() != null)
                        if (loginResponse.getStatus()) {
                            saveUser(employeeId, loginResponse.getExpiresIn());
                        }
                } else {
                    Utility.showLog("onResponse " + response.body());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SplashAndLoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void saveUser(String employeeId, Long expiresIn) {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        Utility.saveEmployee(this, employee);
        TimeValidator.saveStartTime(this, expiresIn);
        gotoHomeActivity();
    }


//    private void requestLogin(String employeeId,String password) {
//
//
////        showLoading();
//
//
//        ApiInterface apiService = RetrofitInstance.getApiInterface().create(ApiInterface.class);
////        HashMap<String, String> map = new HashMap<>();
//
////                "uid":"939",
////                "source":"app",
////                "app_name":"mKiddo_v:2.5.0",
////                "child_name":"Arwa2",
////                "birth_date":"2020-06-05",
////                "child_profile_path":
//
//        if (getUserID() == null) {
//            hideLoading();
//            return;
//        }
//
//
////        map.put("uid", getUserID());
////        map.put("source", "app");
////        map.put("app_name", "mKiddo_v:" + BuildConfig.VERSION_NAME);
////        map.put("child_name", binding.layoutCreateChildProfileId.etChildName.getText().toString());
////        map.put("birth_date", Utility.getSendingBirthDay(birthDay));
////        map.put("child_profile_path", imageBase64);
////        map.put("interest_list", imageBase64);
//
//        AddChildRequestModel model = new AddChildRequestModel();
//        model.setUid(getUserID());
//        model.setSource("app");
//        model.setApp_name("mKiddo_v:" + BuildConfig.VERSION_NAME);
//        model.setChild_name(binding.layoutCreateChildProfileId.etChildName.getText().toString().trim());
//        model.setBirth_date(Utility.getSendingBirthDay(birthDay));
//        model.setChild_profile_path(imageBase64);
//        model.setInterestList(getInterestList());
//
//        String token = Utility.getApiTokenModel(this).getAccessToken();
//
//        JAppUtils.showLog("inside requestAddChild method  " + model);
//
//        Call<AddChildResponseModel> call = apiService.requestAddChild(Utility.getApiTokenModel(this).getAccessToken(), model);
//
//        JAppUtils.showLog("inside requestAddChild method  ");
//
//        call.enqueue(new Callback<AddChildResponseModel>() {
//
//            @Override
//            public void onResponse(Call<AddChildResponseModel> call, Response<AddChildResponseModel> response) {
//                JAppUtils.showLog("\n\nisSuccessfull " + response.isSuccessful()
//                        + " code " + response.code());
//
//                hideLoading();
//
//                if (!response.isSuccessful()) {
//                    return;
//                }
//
//                assert response.body() != null;
//                if (response.body().getChildInfo() != null) {
//                    Toast.makeText(LoginActivityV3.this, "Successfully added", Toast.LENGTH_SHORT).show();
//                    birthDay = "";
//                    try {
//                        setSelectedChild(response.body().getChildInfo().get(0));
//                    } catch (Exception e) {
//
//                    }
//
//                } else JAppUtils.showLog(" \n" + response.body().getMessage());
//
////                if (response.body().getChildInfo()!=null)
//                updateKidList(response.body().getChildInfo());
//
//            }
//
//            @Override
//            public void onFailure(Call<AddChildResponseModel> call, Throwable t) {
//
//                hideLoading();
//
//                if (call.isCanceled()) {
//                    JAppUtils.showLog(" call cancelled" + t.toString());
//                    return;
//                }
//                JAppUtils.showLog(" inside onFailure " + t.toString());
//
//            }
//        });
//
//    }

}

