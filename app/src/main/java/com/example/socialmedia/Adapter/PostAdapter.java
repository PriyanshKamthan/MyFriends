package com.example.socialmedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.CommentActivity;
import com.example.socialmedia.MainActivity;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        Picasso.get().load(post.getImageurl()).into(holder.postImageView);
        holder.descriptionTextView.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (!user.getImageUrl().equals("default"))
                    Picasso.get().load(user.getImageUrl()).into(holder.profilePicImageView);
                holder.usernameTextView.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        isLiked(post.getPostId(), holder.likeButton);
        noOfLikes(post.getPostId(), holder.noOfLikesTextView);
        getComments(post.getPostId(),holder.noOfCommentTextview);
        isSaved(post.getPostId(),holder.saveButton);

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.likeButton.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.saveButton.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("SavedPosts")
                            .child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("SavedPosts")
                            .child(firebaseUser.getUid()).child(post.getPostId()).removeValue();
                }
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    private void isSaved(String postId, ImageView saveButton) {
        FirebaseDatabase.getInstance().getReference().child("SavedPosts").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    saveButton.setImageResource(R.drawable.saved_icon);
                    saveButton.setTag("saved");
                } else {
                    saveButton.setImageResource(R.drawable.save_icon);
                    saveButton.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void isLiked(String postId, ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.liked_icon);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.like_icon);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikes(String postId, TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getComments(String postId, TextView textView) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profilePicImageView;
        public ImageView postImageView;
        public ImageView likeButton;
        public ImageView commentButton;
        public ImageView saveButton;
        public ImageView moreButton;

        public TextView usernameTextView;
        public TextView noOfLikesTextView;
        public TextView noOfCommentTextview;
        public TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicImageView = itemView.findViewById(R.id.profile_pic_imageview);
            postImageView = itemView.findViewById(R.id.post_image);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            saveButton = itemView.findViewById(R.id.save_button);
            moreButton = itemView.findViewById(R.id.more_button);
            usernameTextView = itemView.findViewById(R.id.username_textview);
            noOfLikesTextView = itemView.findViewById(R.id.likes_count_textview);
            noOfCommentTextview = itemView.findViewById(R.id.comment_count_textview);
            descriptionTextView = itemView.findViewById(R.id.description_textview);

        }
    }
}
