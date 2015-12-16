package com.tryand.codepathtodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.tryand.R;

/**
 * Created by skammila on 11/22/15.
 */
public class Splash extends Activity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_layout);

        ImageView animationTarget = (ImageView) this.findViewById(R.id.splashscreen);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        animationTarget.startAnimation(animation);


        // New Handler to start the Main-Activity.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent that will start the Main-Activity.
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
