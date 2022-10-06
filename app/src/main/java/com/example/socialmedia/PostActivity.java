package com.example.socialmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 22;
    private ImageView closeView;
    private ImageView postImage;
    private TextView errorTextView;
    private Button postButton;
    private Button selectImageButton;
    private EditText descriptionEditText;

    private Uri imageUri;
    private String imageURL;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image from here....."), PICK_IMAGE_REQUEST);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg();
            }
        });

        closeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void uploadImg() {

        if (imageUri != null) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading...");
            pd.show();
            StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            filePath.putFile(imageUri)
                    .continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            pd.dismiss();
                            postImage.setImageBitmap(null);
                            errorTextView.setText(R.string.image_upload_message);
                            errorTextView.setTextColor(Color.GREEN);

                            return filePath.getDownloadUrl();
                        }
                    }).
                    addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloadUri = task.getResult();
                            imageURL = downloadUri.toString();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            String postId = ref.push().getKey();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("postId", postId);
                            map.put("imageurl", imageURL);
                            map.put("description", descriptionEditText.getText().toString());
                            map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            ref.child(postId).setValue(map);

                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            errorTextView.setText(e.getMessage());
                            errorTextView.setTextColor(Color.RED);
                        }
                    });
        } else {
            errorTextView.setText(R.string.select_image_message);
            errorTextView.setTextColor(Color.RED);
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                postImage.setImageBitmap(bitmap);
            } catch (IOException ex) {
                errorTextView.setText(ex.getMessage());
                errorTextView.setTextColor(Color.RED);
            }
        }
    }

    public void init() {
        closeView = findViewById(R.id.back_button);
        postImage = findViewById(R.id.post_imageview);
        postButton = findViewById(R.id.post_button);
        selectImageButton = findViewById(R.id.select_image_button);
        errorTextView = findViewById(R.id.error_textview);
        descriptionEditText = findViewById(R.id.description_edittext);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }
}