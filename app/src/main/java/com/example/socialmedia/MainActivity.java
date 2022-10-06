package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.socialmedia.Fragments.HomeFragment;
import com.example.socialmedia.Fragments.NotificationFragment;
import com.example.socialmedia.Fragments.ProfileFragment;
import com.example.socialmedia.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    RelativeLayout welcomeLayout , mainLayout;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    private Animation fadeInAnimation , moveAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        animate();
        setBottomNavBar();
    }

    private void setBottomNavBar() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_bottom_home:
                        selectorFragment = new HomeFragment();
                        break;
                    case R.id.nav_bottom_search:
                        selectorFragment = new SearchFragment();
                        break;
                    case R.id.nav_bottom_add:
                        selectorFragment = null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;
                    case R.id.nav_bottom_activities:
                        selectorFragment = new NotificationFragment();
                        break;
                    case R.id.nav_bottom_profile:
                        selectorFragment = new ProfileFragment();
                        break;
                }
                if(selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectorFragment).commit();
                }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if(intent != null) {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_bottom_profile);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private void init() {
        welcomeLayout = findViewById(R.id.welcome_layout);
        mainLayout = findViewById(R.id.main_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_animation);
        moveAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_animation);
    }

    private void animate() {
        welcomeLayout.setAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                welcomeLayout.clearAnimation();
                welcomeLayout.setVisibility(View.INVISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }
}