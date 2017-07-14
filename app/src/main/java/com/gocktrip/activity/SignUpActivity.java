package com.gocktrip.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gocktrip.AppData;
import com.gocktrip.R;
import com.gocktrip.manager.ApiManager;
import com.gocktrip.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends Activity {

    private Button btn_validate;
    private LoginButton btn_facebook;
    private ImageView btn_back;
    private EditText edit_firstname;
    private EditText edit_lastname;
    private EditText edit_email;
    private EditText edit_password;
    private ProgressDialog pDialog;
    private CheckBox checkBox;
    private AppData appData;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_signup);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbarcolor));
        }

        appData = AppData.getInstance();


        initUI();
    }

    private void initUI() {
        edit_firstname = (EditText) findViewById(R.id.edit_firstname_signup);
        edit_lastname = (EditText) findViewById(R.id.edit_lastname_signup);
        edit_email = (EditText) findViewById(R.id.edit_email_signup);
        edit_password = (EditText) findViewById(R.id.edit_pwd_signup);
        btn_facebook = (LoginButton) findViewById(R.id.btn_facebook);
        btn_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                int a = 1;
            }

            @Override
            public void onCancel() {
                int a = 1;
            }

            @Override
            public void onError(FacebookException error) {
                int a = 1;
            }
        });
        btn_validate = (Button) findViewById(R.id.btn_validate_signup);
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strFirstName = edit_firstname.getText().toString();
                String strLastName = edit_lastname.getText().toString();
                String strEmail = edit_email.getText().toString();
                String strPassword = edit_password.getText().toString();

                if (strFirstName.length() == 0) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.message_firstname), Toast.LENGTH_SHORT).show();
                } else if (strLastName.length() == 0) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.message_lastname), Toast.LENGTH_SHORT).show();
                }else if (strEmail.length() == 0){
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.message_email), Toast.LENGTH_SHORT).show();
                }else if (strPassword.length() == 0){
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.message_password), Toast.LENGTH_SHORT).show();
                }else if (!checkBox.isChecked()){
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Are you agree Gockel terms?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkBox.setChecked(true);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else {
                    pDialog = new ProgressDialog(SignUpActivity.this);
                    pDialog.setMessage(getResources().getString(R.string.message_loginDialog));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    ApiManager.getInstance(SignUpActivity.this).signup(strFirstName, strLastName, strEmail, strPassword, new ApiManager.OnApiManagerListener() {
                        @Override
                        public void onSucces(JSONObject result) {
                            pDialog.dismiss();

                            JSONObject jsonObject = null;

                            try {
                                jsonObject = result.getJSONObject("result");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (jsonObject == null) {
                                new AlertDialog.Builder(SignUpActivity.this)
                                        .setTitle("Cannot Log in")
                                        .setMessage("Please insert correct email and password!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }else {

                                User loginUser = new User(jsonObject);

                                loginUser.saveOnDisk(SignUpActivity.this);
                                appData.currentUser = loginUser;

                                Intent intent = new Intent(SignUpActivity.this, TabsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onSuccess(JSONArray resultArray) {

                        }

                        @Override
                        public void onFail(JSONObject fail) {
                            pDialog.dismiss();

                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Cannot access the server")
                                    .setMessage("Please check your network status and try again!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });
                }
            }
        });

        checkBox = (CheckBox) findViewById(R.id.checkbox_signup);

        btn_back = (ImageView) findViewById(R.id.btn_back_signup);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

    }
}