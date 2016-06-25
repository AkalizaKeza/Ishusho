package com.akalizakeza.apps.ishusho.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab4Profile extends Fragment implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Tag4Profile";
    private ViewGroup mProfileUi;
    private ViewGroup mSignInUi;
    private FirebaseAuth mAuth;
    private CircleImageView mProfilePhoto;
    private TextView mProfileUsername;
    private GoogleApiClient mGoogleApiClient;
    private Button mLaunchSignInButton;
    private Button mShowGalleryButton;
    private Button mSignOutButton;


    private static final int RC_SIGN_IN = 103;


    public Tab4Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.tab4_profile, container, false);

        mAuth = FirebaseAuth.getInstance(); // Initialize authentication and set up callbacks

        // GoogleApiClient with Sign In
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .build())
                .build();

        mSignInUi = (ViewGroup) view.findViewById(R.id.sign_in_ui);
        mProfileUi = (ViewGroup) view.findViewById(R.id.profile);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_user_photo);
        mProfileUsername = (TextView) view.findViewById(R.id.profile_user_name);
        mLaunchSignInButton = (Button) view.findViewById(R.id.launch_sign_in);
        mShowGalleryButton = (Button) view.findViewById(R.id.show_gallery_button);
        mSignOutButton = (Button) view.findViewById(R.id.sign_out_button);

        mLaunchSignInButton.setOnClickListener(this);
        mShowGalleryButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        return view; // Inflate the layout for this fragment
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.launch_sign_in:
                launchSignInIntent();
                break;
            case R.id.sign_out_button:
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                showSignedOutUI();
                break;
            case R.id.show_gallery_button:
                Intent galleryIntent = new Intent(getContext(), Tab1Gallery.class);
                startActivity(galleryIntent);
                break;
        }
    }

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
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        handleFirebaseAuthResult(result);
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "auth:onFailure:" + e.getMessage());
                        handleFirebaseAuthResult(null);
                    }
                });
    }

    private void handleFirebaseAuthResult(AuthResult result) {
        // TODO: This auth callback isn't being called after orientation change. Investigate.
        if (result != null) {
            Log.d(TAG, "handleFirebaseAuthResult:SUCCESS");
            showSignedInUI(result.getUser());
        } else {
            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
            showSignedOutUI();
        }
    }

    private void showSignedInUI(FirebaseUser firebaseUser) {
        Log.d(TAG, "Showing signed in UI");
        mSignInUi.setVisibility(View.GONE);
        mProfileUi.setVisibility(View.VISIBLE);
        mProfileUsername.setVisibility(View.VISIBLE);
        mProfilePhoto.setVisibility(View.VISIBLE);
        if (firebaseUser.getDisplayName() != null) {
            mProfileUsername.setText(firebaseUser.getDisplayName());
            Log.d(TAG, "Is it here?");
        }

        if (firebaseUser.getPhotoUrl() != null) {
            GlideUtil.loadProfileIcon(firebaseUser.getPhotoUrl().toString(), mProfilePhoto);
        }
        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put("displayName", firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Anonymous");
        updateValues.put("photoUrl", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);

        FirebaseUtil.getPeopleRef().child(firebaseUser.getUid()).updateChildren(
                updateValues,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference databaseReference) {
                        if (firebaseError != null) {
                            Toast.makeText(getContext(),
                                    "Couldn't save user data: " + firebaseError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showSignedOutUI() {
        Log.d(TAG, "Showing signed out UI");
        Log.d(TAG, " Or here? mProfileUsername " + mProfileUsername);
        mProfileUsername.setText("");
        mSignInUi.setVisibility(View.VISIBLE);
        mProfileUi.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            showSignedInUI(currentUser);
        } else {
            showSignedOutUI();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}