package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    private Post post;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvTimeStamp;
    private TextView tvDetailViewComments;
    private ImageView ivProfileProfilePic;
    private ImageView iconHeart;
    private TextView tvLikeCounter;
    private boolean isLiked;
    private int likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent intent = getIntent();
        Parcelable wrappedPost = intent.getParcelableExtra("post");
        post = Parcels.unwrap(wrappedPost);
        Log.i(TAG, "Post caption: " + post.getDescription());
        bindView();
    }

    private void bindView() {
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        ivProfileProfilePic = findViewById(R.id.ivProfileProfilePic);
        tvDetailViewComments = findViewById(R.id.tvDetailViewComments);
        iconHeart = findViewById(R.id.iconHeart);
        tvLikeCounter = findViewById(R.id.tvLikeCounter);

        Date createdAt = post.getCreatedAt();
        tvTimeStamp.setText(createdAt.toString());
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivProfileProfilePic);
        }

        tvDetailViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context parent = PostDetailActivity.this;
                Intent intent = new Intent(parent, ComposeCommentActivity.class);
                Parcelable wrappedPost = Parcels.wrap(post);
                intent.putExtra("post", wrappedPost);
                parent.startActivity(intent);
            }
        });

        updateIfLiked(post);
        checkHowManyLikes(post);

        iconHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "post: " + post.getDescription());
                Log.i(TAG, "isLiked: " + isLiked);
                if (isLiked) {
                    isLiked = false;
                    iconHeart.setImageResource(R.drawable.ufi_heart);
                    ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
                    query.include(Like.KEY_USER);
                    query.include(Like.KEY_POST);
                    query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
                    query.whereEqualTo(Like.KEY_POST, post);
                    query.getFirstInBackground(new GetCallback<Like>() {
                        @Override
                        public void done(Like object, ParseException e) {
                            if (e != null) {
                                iconHeart.setImageResource(R.drawable.ufi_heart_active);
                                Log.e(TAG, "Couldn't find that object (very bad)", e);
                                return;
                            }
                            object.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        iconHeart.setImageResource(R.drawable.ufi_heart_active);
                                        Log.e(TAG, "Why is delete not working", e);
                                        isLiked = true;
                                    }
                                    Log.i(TAG, "delete worked I guess");

                                    updateLikes(-1);
                                }
                            });
                        }
                    });
                } else {
                    isLiked = true;
                    Like like = new Like();
                    like.setUser(ParseUser.getCurrentUser());
                    like.setPostObj(post);
                    iconHeart.setImageResource(R.drawable.ufi_heart_active);
                    Log.i(TAG, "Save getting executed");
                    like.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                isLiked = false;
                                iconHeart.setImageResource(R.drawable.ufi_heart);
                            }
                            updateLikes(1);
                        }
                    });

                }

            }
        });
    }

    private void checkHowManyLikes(final Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.include(Like.KEY_POST);
        query.whereEqualTo(Like.KEY_POST, post);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                likes = objects.size();
                updateLikes(0);
            }
        });
    }

    private void updateLikes(int change){
        likes += change;
        tvLikeCounter.setText("" + likes);
    }

    private void updateIfLiked(final Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.include(Like.KEY_USER);
        query.include(Like.KEY_POST);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Like.KEY_POST, post);
        query.getFirstInBackground(new GetCallback<Like>() {
            @Override
            public void done(Like object, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "post caption : " + post.getDescription());
                    Log.e(TAG, "Issue with getting likes", e);
                    isLiked = false;
                    return;
                } else {
                    isLiked = true;
                }


                if (isLiked) {
                    iconHeart.setImageResource(R.drawable.ufi_heart_active);
                } else {
                    iconHeart.setImageResource(R.drawable.ufi_heart);
                }
            }
        });

    }
}