package com.akalizakeza.apps.ishusho.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.akalizakeza.apps.ishusho.R;
import com.akalizakeza.apps.ishusho.comicmaker.ComicMakerActivity;

public class Tab3Create extends Fragment implements
        View.OnClickListener {
    public static final String TAG = "Tab3Create";


    private Button mOpenComicMakerButton;
    private Button mOpenNewPostButton;

    public Tab3Create() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab3_create, container, false);

        mOpenComicMakerButton = (Button) view.findViewById(R.id.openComicMaker);
        mOpenNewPostButton = (Button) view.findViewById(R.id.openNewPost);

        mOpenComicMakerButton.setOnClickListener(this);
        mOpenNewPostButton.setOnClickListener(this);

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
            case R.id.openNewPost:
                Intent openNewPost = new Intent(getContext(), NewPostActivity.class);
                startActivity(openNewPost);
                break;
            default:
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
