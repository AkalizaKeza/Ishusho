/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akalizakeza.apps.ishusho.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akalizakeza.apps.ishusho.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ArtistViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PostViewHolder";
    private final View mView;
    private PostClickListener mListener;
    public DatabaseReference mPostRef;
    public ValueEventListener mPostListener;

    public enum LikeStatus { LIKED, NOT_LIKED }
    private final ImageView mLikeIcon;
    private static final int POST_TEXT_MAX_LINES = 6;
    private LinearLayout mPostImageView;
    private LinearLayout mPostReactionView;
    private ImageView mPhotoView;
    private ImageView mIconView;
    private TextView mAuthorView;
    private TextView mPostTextView;
    private TextView mTimestampView;
    private TextView mNumLikesView;
    public String mPostKey;
    public ValueEventListener mLikeListener;

    public ArtistViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mPostImageView = (LinearLayout) mView.findViewById(R.id.post_image_details);
        mPostReactionView = (LinearLayout) mView.findViewById(R.id.post_reaction);
        mPhotoView = (ImageView) mView.findViewById(R.id.post_photo);
        mIconView = (ImageView) mView.findViewById(R.id.post_artist_icon);
        mAuthorView = (TextView) mView.findViewById(R.id.post_author_name);
        mPostTextView = (TextView) mView.findViewById(R.id.post_text);
        mTimestampView = (TextView) mView.findViewById(R.id.post_timestamp);
        mNumLikesView = (TextView) mView.findViewById(R.id.post_num_likes);

        itemView.findViewById(R.id.post_comment_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showComments();
            }
        });
        mLikeIcon = (ImageView) itemView.findViewById(R.id.post_like_icon);
        mLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.toggleLike();
            }
        });
    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, mPhotoView);
    }

    public void setIcon(String url, String author, final String authorId) {
        GlideUtil.loadProfileIcon(url, mIconView);

        final String finalAuthor = author;

        mIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(finalAuthor, authorId);
            }
        });
    }

    public void setAuthor(String author, final String authorId) {
        if (author == null || author.isEmpty()) {
            author = mView.getResources().getString(R.string.user_info_no_name);
        }

        mAuthorView.setText(author);
        final String finalAuthor = author;
        mAuthorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(finalAuthor, authorId);
            }
        });
    }

    private void showUserDetail(String author, String authorId) {

        Context context = mView.getContext();
        Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
        userDetailIntent.putExtra(UserDetailActivity.USER_EXTRA_NAME, author);
        userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, authorId);
        context.startActivity(userDetailIntent);
    }


    public void setText(final String text) {
        if (text == null || text.isEmpty()) {
            mPostTextView.setVisibility(View.GONE);
            return;
        } else {
            mPostTextView.setVisibility(View.VISIBLE);
            mPostTextView.setText(text);
            mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
            mPostTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPostTextView.getMaxLines() == POST_TEXT_MAX_LINES) {
                        mPostTextView.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
                    }
                }
            });
        }
    }
    public void hideItems() {
        mPostImageView.setVisibility(View.GONE);
        mPostReactionView.setVisibility(View.GONE);
    }


    public interface PostClickListener {
        void showComments();
        void toggleLike();
    }
}
