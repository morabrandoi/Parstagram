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
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    private Post post;
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvTimeStamp;
    private TextView tvDetailViewComments;
    private ImageView ivProfileProfilePic;

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

    }
}