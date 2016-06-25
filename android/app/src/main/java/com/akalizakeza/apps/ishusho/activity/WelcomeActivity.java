package com.akalizakeza.apps.ishusho.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.akalizakeza.apps.ishusho.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;

public class WelcomeActivity extends BaseActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "WelcomeActivity";
    private static final int RC_SIGN_IN = 103; // TODO

    private FrameLayout welcomeActivityLayout;

    /**
     * #LANGUAGE
    private Button language_select_rw, language_select_en;
    private Locale myLocale;
     * #LANGUAGE */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize authentication and set up callbacks
        mAuth = FirebaseAuth.getInstance();

        // GoogleApiClient with Sign In
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .build())
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.explore_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if (FirebaseUtil.getCurrentUserId() != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

        this.welcomeActivityLayout = (FrameLayout)findViewById(R.id.welcomeActivityLayout);

/**
 * #LANGUAGE

        this.language_select_rw = (Button)findViewById(R.id.language_select_rw);
        this.language_select_en = (Button)findViewById(R.id.language_select_en);

        this.language_select_rw.setOnClickListener(this);
        this.language_select_en.setOnClickListener(this);

        loadLocale();
 * #LANGUAGE */

    }

    /**
     * #LANGUAGE

    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
    * #LANGUAGE */

    @Override
    public void onClick(View v) {
        /** #LANGUAGE String lang = "rw"; */
        int id = v.getId();
        switch (id) {
            case R.id.sign_in_button:
                launchSignInIntent();
                break;
            case R.id.explore_button:
                mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        currentUser.isAnonymous();
                        Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WelcomeActivity.this, "Unable to sign in anonymously.",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getMessage());
                    }
                });
                break;
/**
 * #LANGUAGE
            case R.id.language_select_rw:
                lang = "rw";
                Log.d(TAG, "#LANGUAGE Kinyarwanda language selected");
                break;
            case R.id.language_select_en:
                lang = "en";
                Log.d(TAG, "#LANGUAGE English language selected");
                break;
 * #LANGUAGE */
            default:
                break;
        }
        /**
         * #LANGUAGE
        changeLang(lang);
        recreate();
         * #LANGUAGE */
    }

    /**
     * #LANGUAGE

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    * #LANGUAGE */


    private void launchSignInIntent() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.getStatus());
        if (result.isSuccess()) {
            // Successful Google sign in, authenticate with Firebase.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            // Unsuccessful Google Sign In, show signed-out UI
            Log.d(TAG, "Google Sign-In failed.");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        showProgressDialog(getString(R.string.profile_progress_message));
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        handleFirebaseAuthResult(result);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "auth:onFailure:" + e.getMessage());
                        handleFirebaseAuthResult(null);
                    }
                });
    }

    private void handleFirebaseAuthResult(AuthResult result) {
        // TODO: This auth callback isn't being called after orientation change. Investigate.
        dismissProgressDialog();
        if (result != null) {
            Log.d(TAG, "handleFirebaseAuthResult:SUCCESS");
            Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSignedInUI() {
        Log.d(TAG, "Go to Profile");
        Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            dismissProgressDialog();
            showSignedInUI();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
    }
}