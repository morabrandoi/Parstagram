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
    private boolean isLiked;

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

        isLiked = false;
        checkIfLiked(post);
        if (isLiked) {
            iconHeart.setImageResource(R.drawable.ufi_heart_active);
        }
        else{
            iconHeart.setImageResource(R.drawable.ufi_heart);
        }

        iconHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked){
                    Like like = new Like();
                    like.setUser(ParseUser.getCurrentUser());
                    like.setPostObj(post);
                    like.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            iconHeart.setImageResource(R.drawable.ufi_heart);
                        }
                    });
                } else {
                    Like like = new Like();
                    like.setUser(ParseUser.getCurrentUser());
                    like.setPostObj(post);
                    like.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            iconHeart.setImageResource(R.drawable.ufi_heart_active);
                        }
                    });

                }

            }
        });

    }

    private void checkIfLiked(Post post) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.include(Like.KEY_USER);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Like.KEY_POST, post);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting likes", e);
                    isLiked = likes.size() > 0;
                    return;
                }

            }
        });
    }
}