package com.example.socialmedia;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Adapter.CommentAdapter;
import com.example.socialmedia.Model.Comment;
import com.example.socialmedia.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private RecyclerView commentRecyclerView;
    FirebaseUser firebaseUser;
    private EditText addComment;
    private ImageView profilePicImageView;
    private Button postButton;
    private ImageView backButton;
    private String postId;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addComment = findViewById(R.id.add_comment_edittext);
        profilePicImageView = findViewById(R.id.profile_pic_imageview);
        postButton = findViewById(R.id.post_button);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        commentRecyclerView = findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList,postId);
        commentRecyclerView.setAdapter(commentAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();
        getComment();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addComment.getText().toString().isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Oops! Please type something to comment.", Toast.LENGTH_SHORT).show();
                } else  {
                    putComment();
                }
            }
        });
    }

    public void getComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void putComment() {
        HashMap<String,Object> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id = ref.push().getKey();

        map.put("id",id);
        map.put("comment",addComment.getText().toString());
        map.put("publisher",firebaseUser.getUid());

        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this, "Your comment is added", Toast.LENGTH_SHORT).show();
                    addComment.setText("");
                }
                else Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if( ! user.getImageUrl().equals("default"))
                    Picasso.get().load(user.getImageUrl()).into(profilePicImageView);
                else profilePicImageView.setImageResource(R.drawable.profile_icon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}