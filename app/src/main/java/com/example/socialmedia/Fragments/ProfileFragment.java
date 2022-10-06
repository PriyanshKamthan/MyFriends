package com.example.socialmedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Adapter.PhotoAdapter;
import com.example.socialmedia.EditProfileActivity;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profilePicImageView;
    private ImageView optionButton;
    private TextView postCountTextView;
    private TextView followerCountTextView;
    private TextView followingCountTextView;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView bioTextView;
    private ImageView myPostImageView;
    private ImageView savedPostImageView;
    private LinearLayout postOptionLayout;
    private RecyclerView myPostsRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoPosts;
    private Button followButton;

    private FirebaseUser firebaseUser;
    private String profileId;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();

        myPostsRecyclerView.setHasFixedSize(true);
        myPostsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        ViewCompat.setNestedScrollingEnabled(myPostImageView, false);
        myPhotoPosts = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(),myPhotoPosts);
        myPostsRecyclerView.setAdapter(photoAdapter);

        myPhotos();

        savedPostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavedPosts();
            }
        });

        myPostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPhotos();
            }
        });

        profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        return view;
    }

    private void mySavedPosts() {
        savedPostImageView.setImageResource(R.drawable.saved_icon);
        myPostImageView.setBackgroundColor(Color.WHITE);
        savedPostImageView.setBackgroundResource(R.color.color4);
        List<String> savedPostIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("SavedPosts").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    savedPostIds.add(dataSnapshot.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                         myPhotoPosts.clear();
                         for(DataSnapshot dataSnapshot:snapshot1.getChildren()) {
                             Post post = dataSnapshot.getValue(Post.class);

                             for(String id:savedPostIds)
                                 if(post.getPostId().equals(id))
                                     myPhotoPosts.add(post);
                         }
                         photoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {
        savedPostImageView.setImageResource(R.drawable.save_icon);
        savedPostImageView.setBackgroundColor(Color.WHITE);
        myPostImageView.setBackgroundResource(R.color.color4);
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoPosts.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)) {
                        myPhotoPosts.add(post);
                    }
                }
                Collections.reverse(myPhotoPosts);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)) count++;
                }
                postCountTextView.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerCountTextView.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCountTextView.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")) profilePicImageView.setImageResource(R.drawable.profile_icon);
                else Picasso.get().load(user.getImageUrl()).into(profilePicImageView);
                nameTextView.setText(user.getName());
                usernameTextView.setText(user.getUsername());
                bioTextView.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId","none").apply();
    }

    private void init() {
        profilePicImageView = view.findViewById(R.id.profile_pic_imageview);
        optionButton = view.findViewById(R.id.back_button);
        postCountTextView = view.findViewById(R.id.post_count_textview);
        followerCountTextView = view.findViewById(R.id.follower_count_textview);
        followingCountTextView = view.findViewById(R.id.following_count_textview);
        nameTextView = view.findViewById(R.id.name_textview);
        usernameTextView = view.findViewById(R.id.username_textview);
        bioTextView = view.findViewById(R.id.bio_textview);
        myPostImageView = view.findViewById(R.id.my_posts_imageview);
        savedPostImageView = view.findViewById(R.id.saved_posts_imageview);
        myPostsRecyclerView = view.findViewById(R.id.my_posts_recyclerview);
        followButton = view.findViewById(R.id.follow_button);
        postOptionLayout = view.findViewById(R.id.post_option_linearlayout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");
        if(data.equals("none")) {
            profileId = firebaseUser.getUid();
            followButton.setVisibility(View.GONE);
            postOptionLayout.setVisibility(View.VISIBLE);
        } else {
            profileId = data;
            postOptionLayout.setVisibility(View.GONE);
            followButton.setVisibility(View.VISIBLE);
            checkFollowingStatus();
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(followButton.getText().toString().equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child((firebaseUser.getUid()))
                                .child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            });
        }
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()) followButton.setText(R.string.following);
                else { followButton.setText(R.string.follow); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}