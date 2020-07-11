package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.Post;
import com.example.parstagram.HomeAdapter;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = "PostsFragment";
    public static final int QUERY_LIMIT = 10;

    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private HomeAdapter adapter;
    private List<Post> allPosts;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initializing class variables
        allPosts = new ArrayList<>();
        adapter = new HomeAdapter(getContext(), allPosts);

        // Swipe Container Code
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                queryPosts(0);
                allPosts.clear();

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // RecyclerView Code
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(layoutManager);
        queryPosts(0); // Filling in rvPosts;

        // Scroll Listener Code
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
    }

    protected void queryPosts(int page) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setSkip(QUERY_LIMIT * page);
        query.setLimit(QUERY_LIMIT); // Number of posts to query at a time
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                swipeContainer.setRefreshing(false);
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}