package com.example.parstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.parstagram.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> allComments) {
        this.context = context;
        this.comments = allComments;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Comment> comments) {
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCommentUsername;
        private TextView tvCommentContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
        }

        public void bind(final Comment comment) {
            tvCommentUsername.setText(comment.getUser().getUsername());
            tvCommentContent.setText(comment.getContents());
        }
    }
}
