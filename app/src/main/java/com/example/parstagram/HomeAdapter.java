package com.example.parstagram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.parstagram.fragments.ProfileFragment;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    public static final String KEY_PROFILE_PIC = "profilePicture";
    private static final String TAG = "HomeAdapter";
    private Context context;
    private List<Post> posts;

    public HomeAdapter(Context context, List<Post> allPosts) {
        this.context = context;
        this.posts = allPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts) {
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvItemViewComments;
        private ImageView ivPostImage;
        private ImageView ivPostProfilePic;
        private ImageView iconHeart;
        private LinearLayout llItem;
        private LinearLayout llUserInfo;
        private TextView tvTimeStamp;
        private Boolean isLiked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvItemViewComments = itemView.findViewById(R.id.tvItemViewComments);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            llItem = itemView.findViewById(R.id.llItem);
            ivPostProfilePic = itemView.findViewById(R.id.ivPostProfilePic);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            iconHeart = itemView.findViewById(R.id.iconHeart);
            llUserInfo = itemView.findViewById(R.id.lLuserInfo);
        }

        public void bind(final Post post) {
            Date createdAt = post.getCreatedAt();
            tvTimeStamp.setText(createdAt.toString());
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivPostImage);
            }

            ParseFile profileImage = post.getUser().getParseFile(KEY_PROFILE_PIC);
            if (image != null) {
                Glide.with(context).load(profileImage.getUrl()).into(ivPostProfilePic);
            }

            llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    Parcelable wrappedPost = Parcels.wrap(post);
                    intent.putExtra("post", wrappedPost);
                    context.startActivity(intent);
                }
            });

            tvItemViewComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeCommentActivity.class);
                    Parcelable wrappedPost = Parcels.wrap(post);
                    intent.putExtra("post", wrappedPost);
                    context.startActivity(intent);
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

            llUserInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment profile = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    Parcelable wrappedUser = Parcels.wrap(post.getUser());
                    bundle.putParcelable("user", wrappedUser);
                    profile.setArguments(bundle);
                    ((MainActivity) context).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.flContainer, profile).commit();
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
}
