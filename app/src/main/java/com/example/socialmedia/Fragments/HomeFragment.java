package com.example.socialmedia.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Adapter.PostAdapter;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> posts;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        postRecyclerView = view.findViewById(R.id.post_recycler_view);
        postRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        postRecyclerView.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),posts);
        postRecyclerView.setAdapter(postAdapter);
        followingList = new ArrayList<>();

        checkFollowingUsers();

        return view;
    }

    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    followingList.add(snapshot1.getKey());
                }
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPost() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    for(String id:followingList) {
                        if(post.getPublisher().equals(id)) {
                            posts.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}