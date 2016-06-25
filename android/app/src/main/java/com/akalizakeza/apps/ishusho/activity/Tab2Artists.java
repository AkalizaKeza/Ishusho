package com.akalizakeza.apps.ishusho.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akalizakeza.apps.ishusho.R;
import com.akalizakeza.apps.ishusho.models.Artist;
import com.akalizakeza.apps.ishusho.models.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2Artists extends Fragment {

    public static final String TAG = "Tab2Artists";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private int mRecyclerViewPosition = 0;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ArtistViewHolder> mAdapter;

    public Tab2Artists() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
            // TODO: RecyclerView only restores position properly for some tabs.
        }
        Log.d(TAG, "Restoring recycler view position (Artwork): " + mRecyclerViewPosition);
        Query allPostsQuery = FirebaseUtil.getPostsRef();
        mAdapter = getFirebaseRecyclerAdapter(allPostsQuery);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private FirebaseRecyclerAdapter<Post, ArtistViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Post, ArtistViewHolder>(
                Post.class, R.layout.activity_post_item, ArtistViewHolder.class, query) {
            @Override
            public void populateViewHolder(final ArtistViewHolder artistViewHolder,
                                           final Post post, final int position) {
                setupPost(artistViewHolder, post, position, null);
            }

            @Override
            public void onViewRecycled(ArtistViewHolder holder) {
                super.onViewRecycled(holder);
                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupPost(final ArtistViewHolder artistViewHolder, final Post post,
                           final int position, final String inPostKey) {
            artistViewHolder.setText(post.getText());
            Artist artist = post.getArtist();
            artistViewHolder.setAuthor(artist.getFull_name(), artist.getUid());
            artistViewHolder.setIcon(artist.getProfile_picture(), artist.getFull_name(), artist.getUid());
            artistViewHolder.hideItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    private int getRecyclerViewScrollPosition() {
        int scrollPosition = 0;
        // TODO: Is null check necessary?
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        return scrollPosition;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     */


    public interface OnFragmentInteractionListener {
    }
}