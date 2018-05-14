package com.sagsaguz.appograph;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        final ImageView appograph = (ImageView) findViewById(R.id.appograph);
        Glide.with(this).load(R.drawable.appograph_splash_screen).into(appograph);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.fadein);
                appograph.startAnimation(animation1);
                //appograph.setVisibility(View.GONE);
                startActivity(new Intent(SplashScreenActivity.this, AllFriendsActivity.class));
                finish();
            }
        },3500);

    }
}
