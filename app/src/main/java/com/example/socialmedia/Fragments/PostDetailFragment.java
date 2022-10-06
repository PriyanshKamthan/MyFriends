package com.example.socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialmedia.Adapter.PostAdapter;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    private String postID;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        postID = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postID","none");
        postRecyclerView = view.findViewById(R.id.post_recycler_view);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postList);
        postRecyclerView.setAdapter(postAdapter);

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList.add(snapshot.getValue(Post.class));
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}