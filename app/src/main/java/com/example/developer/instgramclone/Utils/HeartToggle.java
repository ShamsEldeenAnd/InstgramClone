package com.example.developer.instgramclone.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HeartToggle {

    private ImageView heartWhite, heartRed;

    private static final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private static final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();

    public HeartToggle(ImageView heartWhite, ImageView heartRed) {
        this.heartWhite = heartWhite;
        this.heartRed = heartRed;
    }


    public void toggleLike() {
        AnimatorSet animatorSet = new AnimatorSet();
        if (heartRed.getVisibility() == View.VISIBLE) {
            Log.d("Visisble ", "toggleLike: visisble    ");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 1f, 0);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(accelerateInterpolator);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 1f, 0);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(accelerateInterpolator);

            heartRed.setVisibility(View.GONE);
            heartWhite.setVisibility(View.VISIBLE);
            animatorSet.playTogether(scaleDownY, scaleDownX);

        } else if (heartRed.getVisibility() == View.GONE) {
            Log.d("UnVisisble ", "toggleLike: UnVisisble    ");

            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(decelerateInterpolator);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(decelerateInterpolator);

            heartRed.setVisibility(View.VISIBLE);
            heartWhite.setVisibility(View.GONE);
            animatorSet.playTogether(scaleDownY, scaleDownX);

        }
        animatorSet.start();
    }
}

