package com.example.socialmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextView loginTextView;
    private TextView describeTextView;
    private TextView errorTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private EditText nameEditText;
    private Button registerButton;
    private RelativeLayout relativeLayout;
    private ProgressDialog progressDialog;

    private Animation fadeInAnimation;
    private Animation blinkAnimation;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        animate();

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    errorTextView.setText(R.string.empty_field_message);
                    errorTextView.startAnimation(blinkAnimation);
                } else if (password.length() < 6) {
                    errorTextView.setText(R.string.short_password_message);
                    errorTextView.startAnimation(blinkAnimation);
                } else {
                    registerUser(name, username, email, password);
                }
            }
        });
    }

    private void registerUser(String name, String username, String email, String password) {

        progressDialog.setMessage("Please wait!");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("username", username);
                map.put("name", name);
                map.put("email", email);
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageUrl", "default");

                mRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "We've Registered you " + name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        errorTextView.setText(e.getMessage());
                        errorTextView.startAnimation(blinkAnimation);
                    }
                });
            }
        });
    }

    private void init() {
        loginTextView = findViewById(R.id.login_textview);
        describeTextView = findViewById(R.id.describe_textview);
        errorTextView = findViewById(R.id.error_textview);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        usernameEditText = findViewById(R.id.username_edittext);
        nameEditText = findViewById(R.id.name_edittext);
        registerButton = findViewById(R.id.register_button);
        relativeLayout = findViewById(R.id.register_activity_layout);
        progressDialog = new ProgressDialog(this);

        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_animation);
        blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    private void animate() {
        describeTextView.setAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                describeTextView.clearAnimation();
                describeTextView.setVisibility(View.INVISIBLE);
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_animation);
                relativeLayout.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}