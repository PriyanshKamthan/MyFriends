package com.example.socialmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmedia.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 22;
    private ImageView closeImageView;
    private ImageView saveImageView;
    private ImageView profilePicImageView;
    private EditText fullNameEditText;
    private EditText usernameEditText;
    private EditText bioEditText;
    private TextView changePicTextView;

    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private String imageURL;
    private FirebaseUser firebaseUser;

    private void init() {
        closeImageView = findViewById(R.id.back_button);
        saveImageView = findViewById(R.id.done_button);
        profilePicImageView = findViewById(R.id.profile_pic_imageview);
        fullNameEditText = findViewById(R.id.fullname_edittext);
        usernameEditText = findViewById(R.id.username_edittext);
        bioEditText = findViewById(R.id.bio_edittext);
        changePicTextView = findViewById(R.id.change_pic_button);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        fillValue();

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePicTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image from here....."), PICK_IMAGE_REQUEST);
            }
        });

        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                Toast.makeText(EditProfileActivity.this, "Your profile is updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateProfile() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", fullNameEditText.getText().toString());
        map.put("username", usernameEditText.getText().toString());
        map.put("bio", bioEditText.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profilePicImageView.setImageBitmap(bitmap);
            } catch (IOException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpeg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) throw task.getException();
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("imageUrl").setValue(url);
                        pd.dismiss();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void fillValue() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                fullNameEditText.setText(user.getName());
                usernameEditText.setText(user.getUsername());
                bioEditText.setText(user.getBio());
                Picasso.get().load(user.getImageUrl()).into(profilePicImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}