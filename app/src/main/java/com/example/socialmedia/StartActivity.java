package com.example.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private ImageView iconImageView;
    private Button loginButton;
    private Button registerButton;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        iconImageView = findViewById(R.id.app_logo_square);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        linearLayout = findViewById(R.id.linear_layout);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,LoginActivity.class));
            }
        });

        linearLayout.animate().alpha(0f).setDuration(1);

        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        iconImageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                iconImageView.clearAnimation();
                iconImageView.setVisibility(View.INVISIBLE);
                linearLayout.animate().alpha(1f).setDuration(700);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    }
}