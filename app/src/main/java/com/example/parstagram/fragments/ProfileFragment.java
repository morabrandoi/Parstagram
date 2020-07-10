package com.example.parstagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.ComposeProfilePicActivity;
import com.example.parstagram.LoginActivity;
import com.example.parstagram.Post;
import com.example.parstagram.ProfileAdapter;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    public static final String TAG = "Profile";
    public static final String USER_PFP_KEY = "profilePicture";
    public static final int PROFILE_PIC_REQUEST_CODE = 23;
    public static final int NUM_COLUMN = 3;
    private RecyclerView rvPosts;
    private Button btnLogOut;
    private ImageView ivProfilePic;
    private TextView tvUsername;
    private ProfileAdapter adapter;
    private List<Post> allPosts;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing class variables
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);

        // LogOut button stuffs
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                // Clears back stack when starting new login.
                getActivity().finishAffinity();
                startActivity(intent);
            }
        });

        // Profile Pic
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        ParseUser user = ParseUser.getCurrentUser();
        ParseFile image = user.getParseFile(USER_PFP_KEY);
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivProfilePic);
        }
        else{
            Glide.with(this).load("https://placedog.net/500/500").into(ivProfilePic);
        }
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ComposeProfilePicActivity.class);
                startActivityForResult(intent, PROFILE_PIC_REQUEST_CODE);
            }
        });

        // Username
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(user.getUsername());

        // Recycler View stuffs
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMN));
        queryPosts();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PROFILE_PIC_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {

            ParseUser user = ParseUser.getCurrentUser();
            ParseFile image = user.getParseFile(USER_PFP_KEY);
            if (image != null) {
                Glide.with(this).load(image.getUrl()).into(ivProfilePic);
            }
            else{
                Glide.with(this).load("https://placedog.net/500/500").into(ivProfilePic);
            }
//            Drawable profilePic = Parcels.unwrap(data.getParcelableExtra("profilePic"));
//            profilePic = data.getExtra;
//                    BitmapDrawable
//            ivProfilePic.setImageBitmap(profilePic);

        }
        else {
            Toast.makeText(getActivity(), "DEAR GOD I HOPE I DONT SEE THIS", Toast.LENGTH_SHORT).show();
        }

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20); // Number of posts to query at a time
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }


}
