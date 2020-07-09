package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
    private ImageView ivImage;

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
        ivImage = findViewById(R.id.ivProfilePic);

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());

        Date createdAt = post.getCreatedAt();
        tvTimeStamp.setText(createdAt.toString());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }
    }
}