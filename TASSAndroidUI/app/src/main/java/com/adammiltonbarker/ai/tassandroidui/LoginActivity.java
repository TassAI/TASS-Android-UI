package com.adammiltonbarker.ai.tassandroidui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AppLoginTask         mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mAppIdView;
    private EditText             mAppPublicView;
    private EditText             mAppPrivateView;
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

        mAppPublicView = (EditText) findViewById(R.id.appPublic);
        mAppPublicView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mAppPrivateView = (EditText) findViewById(R.id.appPrivate);
        mAppPrivateView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.connect);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAppIdView.setError(null);
        mAppPublicView.setError(null);
        mAppPrivateView.setError(null);

        // Store values at the time of the login attempt.
        String appIdVal = mAppIdView.getText().toString();
        String appPublicVal = mAppPublicView.getText().toString();
        String appPrivateView = mAppPublicView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for App ID
        if (!TextUtils.isEmpty(appIdVal)) {
            mAppIdView.setError(getString(R.string.error_field_required));
            focusView = mAppIdView;
            cancel = true;
        }

        // Check for App Public Key
        if (!TextUtils.isEmpty(appPublicVal)) {
            mAppPublicView.setError(getString(R.string.error_field_required));
            focusView = mAppPublicView;
            cancel = true;
        }

        // Check for App Private Key
        if (!TextUtils.isEmpty(appPrivateView)) {
            mAppPrivateView.setError(getString(R.string.error_field_required));
            focusView = mAppPrivateView;
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
            mAuthTask = new AppLoginTask(appIdVal, appPublicVal, appPrivateView);
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
     * the user.
     */
    public class AppLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAppId;
        private final String mAppPublic;
        private final String mAppPrivate;

        AppLoginTask(String sAppID, String sAppPublic, String sAppPrivate) {
            mAppId = sAppID;
            mAppPublic = sAppPublic;
            mAppPrivate = sAppPrivate;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

