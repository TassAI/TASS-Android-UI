package com.adammiltonbarker.ai.tassandroidui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that requiring iotJumpWay application MQTT credentials.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AppLoginTask         mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mAppIdView;
    private EditText             musernameView;
    private EditText             mpasswordView;
    private View                 mProgressView;
    private View                 mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#01172C"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        getWindow().setBackgroundDrawableResource(R.drawable.background);

        // Set up the login form.
        mAppIdView = (AutoCompleteTextView) findViewById(R.id.appId);
        musernameView = (EditText) findViewById(R.id.username);
        mpasswordView = (EditText) findViewById(R.id.password);

        Button mConnectButton = (Button) findViewById(R.id.connect);
        mConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * If there are form errors (invalid App ID, MQTT password, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAppIdView.setError(null);
        musernameView.setError(null);
        mpasswordView.setError(null);

        // Store values at the time of the login attempt.
        String appIdVal = mAppIdView.getText().toString();
        String usernameVal = musernameView.getText().toString();
        String passwordView = mpasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for App ID
        if (TextUtils.isEmpty(appIdVal)) {
            mAppIdView.setError(getString(R.string.error_field_required));
            focusView = mAppIdView;
            cancel = true;
        }

        // Check for App Public Key
        if (TextUtils.isEmpty(usernameVal)) {
            musernameView.setError(getString(R.string.error_field_required));
            focusView = musernameView;
            cancel = true;
        }

        // Check for App Private Key
        if (TextUtils.isEmpty(passwordView)) {
            mpasswordView.setError(getString(R.string.error_field_required));
            focusView = mpasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new AppLoginTask(appIdVal, usernameVal, passwordView);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the application.
     */
    public class AppLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAppId;
        private final String musername;
        private final String mpassword;

        AppLoginTask(String sAppID, String susername, String spassword) {
            mAppId = sAppID;
            musername = susername;
            mpassword = spassword;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {

            URL url = null;
            HttpURLConnection client = null;

            Map<String,String> data = new HashMap<>();
            data.put("AppUser",musername);

            String credentials = "Basic "+musername+":"+mpassword;
            String encoded = null;

            try {
                encoded = Base64.encodeToString(credentials.getBytes("UTF-8"), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                url = new URL("https://iotJumpWay.tech/API/REST/IntelliLan/TASS/1_0/Login");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                client = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            client.setRequestProperty ("Authorization", encoded);

            try {
                client.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            client.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            client.setDoOutput(true);
            client.setInstanceFollowRedirects(true);

            try( DataOutputStream wr = new DataOutputStream( client.getOutputStream())) {

                JSONObject jsonParam = new JSONObject();

                for (Map.Entry<String, String> entry : data.entrySet()) {
                    jsonParam.put(entry.getKey(), entry.getValue());
                }

                wr.writeBytes(jsonParam.toString());
                StringBuilder sb = new StringBuilder();
                int HttpResult = client.getResponseCode();

                if (HttpResult == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    Log.e("Debug", "PostResponse " + sb.toString());
                    JSONObject Response = new JSONObject(sb.toString());

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

