package com.example.socialmedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.MainActivity;
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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = mUsers.get(position);

        holder.followButton.setVisibility(View.VISIBLE);
        holder.usernameTextView.setText(user.getUsername());
        holder.nameTextView.setText(user.getName());

        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.profile_icon).into(holder.userPicImageView);
        isFollowed(user.getId(), holder.followButton);

        if (user.getId().equals(firebaseUser.getUid()))
            holder.followButton.setVisibility(View.GONE);

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.followButton.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.userPicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    private void isFollowed(final String id, final Button followButton) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()) {
                    followButton.setText("following");
                } else {
                    followButton.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userPicImageView;
        public TextView nameTextView;
        public TextView usernameTextView;
        public Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userPicImageView = itemView.findViewById(R.id.user_pic_imageview);
            nameTextView = itemView.findViewById(R.id.name_textview);
            usernameTextView = itemView.findViewById(R.id.username_textview);
            followButton = itemView.findViewById(R.id.follow_button);
        }
    }
}
