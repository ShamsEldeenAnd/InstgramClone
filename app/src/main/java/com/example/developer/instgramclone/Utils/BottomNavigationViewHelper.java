package com.example.developer.instgramclone.Utils;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Home.HomeActivity;
import com.example.developer.instgramclone.Likes.LikesActivity;
import com.example.developer.instgramclone.Profile.ProfileActivity;
import com.example.developer.instgramclone.Search.SearchActivity;
import com.example.developer.instgramclone.Share.ShareActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    //Todo add animation to activities 64
    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.house:
                        Intent home = new Intent(context, HomeActivity.class);
                        context.startActivity(home);
                        break;
                    case R.id.profile:
                        Intent profile = new Intent(context, ProfileActivity.class);
                        context.startActivity(profile);
                        break;
                    case R.id.circle:
                        Intent share = new Intent(context, ShareActivity.class);
                        context.startActivity(share);
                        break;
                    case R.id.search:
                        Intent search = new Intent(context, SearchActivity.class);
                        context.startActivity(search);
                        break;
                    case R.id.like:
                        Intent likes = new Intent(context, LikesActivity.class);
                        context.startActivity(likes);
                        break;
                }
                return false;
            }
        });
    }
}
