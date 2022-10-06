package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView registerTextView;
    private TextView describeTextView;
    private TextView errorTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RelativeLayout relativeLayout;

    private Animation fadeInAnimation;
    private Animation blinkAnimation;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        animate();

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    errorTextView.setText(R.string.empty_field_message);
                    errorTextView.startAnimation(blinkAnimation);
                } else if(password.length() <6) {
                    errorTextView.setText(R.string.short_password_message);
                    errorTextView.startAnimation(blinkAnimation);
                } else {
                    loginUser(email,password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorTextView.setText(e.getMessage());
                errorTextView.startAnimation(blinkAnimation);
            }
        });
    }

    private void init() {
        registerTextView = findViewById(R.id.login_textview);
        errorTextView = findViewById(R.id.error_textview);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.register_button);
        relativeLayout = findViewById(R.id.register_activity_layout);
        describeTextView = findViewById(R.id.describe_textview);

        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_animation);
        blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);

        mAuth = FirebaseAuth.getInstance();
    }

    private void animate() {
        describeTextView.setAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                describeTextView.clearAnimation();
                describeTextView.setVisibility(View.INVISIBLE);
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_animation);
                relativeLayout.startAnimation(animation2);
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }
}