package com.example.socialmedia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.MainActivity;
import com.example.socialmedia.Model.Comment;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> comments;
    private String postId;
    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> comments, String postId) {
        this.mContext = mContext;
        this.comments = comments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = comments.get(position);

        holder.commentTextView.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.usernameTextView.setText(user.getUsername());
                if(user.getImageUrl().equals("default"))
                    holder.profilePicImageView.setImageResource(R.drawable.profile_icon);
                else Picasso.get().load(user.getImageUrl()).into(holder.profilePicImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(comment.getPublisher().endsWith(firebaseUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete the comment");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                                    .child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(mContext, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    });
                    alertDialog.show();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePicImageView;
        private TextView usernameTextView;
        private TextView commentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicImageView = itemView.findViewById(R.id.profile_pic_imageview);
            usernameTextView = itemView.findViewById(R.id.username_textview);
            commentTextView = itemView.findViewById(R.id.comment_textview);
        }
    }
}
