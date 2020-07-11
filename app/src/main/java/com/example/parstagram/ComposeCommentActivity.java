package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ComposeCommentActivity extends AppCompatActivity {
    private static final int QUERY_LIMIT = 10;
    private static final String TAG = "ComposeCommentActivity";
    private List<Comment> allComments;
    private CommentsAdapter adapter;
    private RecyclerView rvComments;
    private Post headPost;
    private Button btnPostComment;
    private EditText etComposeComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_comment);

        rvComments = findViewById(R.id.rvComments);
        btnPostComment = findViewById(R.id.btnPostComment);
        etComposeComment = findViewById(R.id.etComposeComment);

        allComments = new ArrayList<Comment>();
        adapter = new CommentsAdapter(this, allComments);

        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        Parcelable post = intent.getParcelableExtra("post");
        headPost = (Post) Parcels.unwrap(post);

        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveComment();

            }
        });
        queryComments();
    }

    private void saveComment() {
        Comment comment = new Comment();
        comment.setContents(etComposeComment.getText().toString());
        comment.setPostObj(headPost);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e );
                    Toast.makeText(ComposeCommentActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post save was successful!");
                etComposeComment.setText("");
            }
        });
        allComments.add(comment);
        adapter.notifyItemInserted(allComments.size() - 1);
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereEqualTo("post", headPost);
//        query.setLimit(QUERY_LIMIT); // Number of posts to query at a time
//        query.addDescendingOrder();
        query.addAscendingOrder(Comment.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }

                allComments.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
