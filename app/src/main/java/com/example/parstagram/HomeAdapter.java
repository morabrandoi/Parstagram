package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    public static final String KEY_PROFILE_PIC = "profilePicture";
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

    public void clear(){
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> posts){
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvItemViewComments;
        private ImageView ivPostImage;
        private ImageView ivPostProfilePic;
        private LinearLayout llItem;

        private TextView tvTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvItemViewComments = itemView.findViewById(R.id.tvItemViewComments);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            llItem = itemView.findViewById(R.id.llItem);
            ivPostProfilePic = itemView.findViewById(R.id.ivPostProfilePic);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
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


        }
    }
}
