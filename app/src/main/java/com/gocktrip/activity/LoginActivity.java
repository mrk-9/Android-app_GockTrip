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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gocktrip.AppData;
import com.gocktrip.R;
import com.gocktrip.manager.ApiManager;
import com.gocktrip.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private AppData appData;

    private ProgressDialog pDialog;
    private EditText txt_email;
    private EditText txt_password;
    private Button btn_login;
    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbarcolor));
        }

        appData = AppData.getInstance();

        txt_email = (EditText) findViewById(R.id.edit_email_login);
        txt_password = (EditText) findViewById(R.id.edit_pwd_login);

        btn_login = (Button) findViewById(R.id.btn_validate);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txt_email.getText().toString();
                String password = txt_password.getText().toString();

                if (email.length() == 0) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.message_email), Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.message_password), Toast.LENGTH_SHORT).show();
                } else {

                    //create progress Dialog and show
                    pDialog = new ProgressDialog(LoginActivity.this);
                    pDialog.setMessage(getResources().getString(R.string.message_loginDialog));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    ApiManager.getInstance(LoginActivity.this).login(email, password, new ApiManager.OnApiManagerListener() {
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
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Cannot Log in")
                                        .setMessage("Please insert correct email and password!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                txt_email.setText("");
                                                txt_password.setText("");
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }else {

                                User loginUser = new User(jsonObject);

                                loginUser.saveOnDisk(LoginActivity.this);
                                appData.currentUser = loginUser;

                                Intent intent = new Intent(LoginActivity.this, TabsActivity.class);
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

                            new AlertDialog.Builder(LoginActivity.this)
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
        btn_back = (ImageView) findViewById(R.id.btn_back_login);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
