package com.akalizakeza.apps.ishusho.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akalizakeza.apps.ishusho.R;
import com.akalizakeza.apps.ishusho.comicmaker.ComicMakerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab3Create . OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab3Create # newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab3Create extends Fragment implements
        EasyPermissions.PermissionCallbacks,
        NewPostUploadTaskFragment.TaskCallbacks,
        View.OnClickListener {
    public static final String TAG = "Tab3Create";
    public static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";
    private static final int THUMBNAIL_MAX_DIMENSION = 640;
    private static final int FULL_SIZE_MAX_DIMENSION = 1280;


    private Button mOpenComicMakerButton;
    private Button mNewPostUpload;
    private Button mNewPostSubmit;

    private ImageView mImageView;
    private Uri mFileUri;
    private Bitmap mResizedBitmap;
    private Bitmap mThumbnail;

    private NewPostUploadTaskFragment mTaskFragment;

    private static final int TC_PICK_IMAGE = 101;
    private static final int RC_CAMERA_PERMISSIONS = 102;

    private static final String[] cameraPerms = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public Tab3Create() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab3_create, container, false);

        mOpenComicMakerButton = (Button) view.findViewById(R.id.openComicMaker);
        mNewPostUpload = (Button) view.findViewById(R.id.new_post_upload);
        mNewPostSubmit = (Button) view.findViewById(R.id.new_post_submit);

        mOpenComicMakerButton.setOnClickListener(this);
        mNewPostUpload.setOnClickListener(this);
        mNewPostSubmit.setOnClickListener(this);

        // find the retained fragment on activity restarts
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mTaskFragment = (NewPostUploadTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // create the fragment and data the first time
        if (mTaskFragment == null) {
            // add the fragment
            mTaskFragment = new NewPostUploadTaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        mImageView = (ImageView) getActivity().findViewById(R.id.new_post_picture);
        Bitmap selectedBitmap = mTaskFragment.getSelectedBitmap();
        Bitmap thumbnail = mTaskFragment.getThumbnail();
        if (selectedBitmap != null) {
            mImageView.setImageBitmap(selectedBitmap);
            mResizedBitmap = selectedBitmap;
        }
        if (thumbnail != null) {
            mThumbnail = thumbnail;
        }
        return view; // Inflate the layout for this fragment
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.openComicMaker:
                Intent openComicMaker = new Intent(getContext(), ComicMakerActivity.class);
                startActivity(openComicMaker);
                break;
            case R.id.new_post_upload:
                showImagePicker();
                break;
            case R.id.new_post_submit:
                final EditText descriptionText = (EditText) getActivity().findViewById(R.id.new_post_text);
                if (mResizedBitmap == null) {
                    Toast.makeText(getContext(), "Select an image first.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String postText = descriptionText.getText().toString();
                if (TextUtils.isEmpty(postText)) {
                    descriptionText.setError(getString(R.string.error_required_field));
                    return;
                }
                getActivity().findViewById(R.id.new_post_submit).setEnabled(false);

                Long timestamp = System.currentTimeMillis();

                String bitmapPath = "/" + FirebaseUtil.getCurrentUserId() + "/full/" + timestamp.toString() + "/";
                String thumbnailPath = "/" + FirebaseUtil.getCurrentUserId() + "/thumb/" + timestamp.toString() + "/";
                mTaskFragment.uploadPost(mResizedBitmap, bitmapPath, mThumbnail, thumbnailPath, mFileUri.getLastPathSegment(),
                        postText);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPostUploaded(final String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().findViewById(R.id.new_post_submit).setEnabled(true);
                if (error == null) {
                    Toast.makeText(getContext(), "Post created!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @AfterPermissionGranted(RC_CAMERA_PERMISSIONS)
    private void showImagePicker() {
        // Check for camera permissions
        if (!EasyPermissions.hasPermissions(getContext(), cameraPerms)) {
            EasyPermissions.requestPermissions(this,
                    "This sample will upload a picture from your Camera",
                    RC_CAMERA_PERMISSIONS, cameraPerms);
            return;
        }

        // Choose file storage location
        File file = new File(getActivity().getExternalCacheDir(), UUID.randomUUID().toString());
        mFileUri = Uri.fromFile(file);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam){
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            cameraIntents.add(intent);
        }

        // Image Picker
        Intent pickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(pickerIntent,
                getString(R.string.picture_chooser_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new
                Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, TC_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TC_PICK_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                final boolean isCamera;
                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }
                if (!isCamera) {
                    mFileUri = data.getData();
                }
                Log.d(TAG, "Received file uri: " + mFileUri.getPath());

                mTaskFragment.resizeBitmap(mFileUri, THUMBNAIL_MAX_DIMENSION);
                mTaskFragment.resizeBitmap(mFileUri, FULL_SIZE_MAX_DIMENSION);
            }
        }
    }

    @Override
    public void onDestroy() {
        // store the data in the fragment
        if (mResizedBitmap != null) {
            mTaskFragment.setSelectedBitmap(mResizedBitmap);
        }
        if (mThumbnail != null) {
            mTaskFragment.setThumbnail(mThumbnail);
        }
        super.onDestroy();
    }

    @Override
    public void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension) {
        if (resizedBitmap == null) {
            Log.e(TAG, "Couldn't resize bitmap in background task.");
            Toast.makeText(getContext().getApplicationContext(), "Couldn't resize bitmap.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMaxDimension == THUMBNAIL_MAX_DIMENSION) {
            mThumbnail = resizedBitmap;
        } else if (mMaxDimension == FULL_SIZE_MAX_DIMENSION) {
            mResizedBitmap = resizedBitmap;
            mImageView.setImageBitmap(mResizedBitmap);
        }

        if (mThumbnail != null && mResizedBitmap != null) {
            getActivity().findViewById(R.id.new_post_submit).setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
