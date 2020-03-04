package com.example.developer.instgramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.developer.instgramclone.CommentsView.CommentViewFragment;
import com.example.developer.instgramclone.Home.HomeFragment;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.PostView.ViewPostFragment;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Search.ViewProfileFragment;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListner, ViewPostFragment.OnCommentThreadSelectedListner, ViewProfileFragment.OnGridImageSelectedListner{

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private Context mContext = ProfileActivity.this;
    private ProgressBar progressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        //comming from search activity
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            ViewProfileFragment viewProfileFragment = new ViewProfileFragment();
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.user), intent.getParcelableExtra(getString(R.string.user)));
            viewProfileFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, viewProfileFragment);
            transaction.addToBackStack(getString(R.string.view_profile_fragment));
            transaction.commit();
        } else {
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, profileFragment);
            transaction.commit();
        }
    }


    @Override
    public void OnGridImageSelected(Photo photo, int activityNo, UserAccountSettings settings) {

        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNo);
        args.putParcelable(getString(R.string.settings), settings);
        viewPostFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewPostFragment);
        transaction.addToBackStack(getString(R.string.PostViewFragment));
        transaction.commit();
    }

    @Override
    public void OnCommentThreadSelected(Photo photo) {
        CommentViewFragment commentViewFragment = new CommentViewFragment();
        Bundle args = new Bundle();

        args.putParcelable(getString(R.string.photo), photo);

        commentViewFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, commentViewFragment);
        transaction.addToBackStack(getString(R.string.view_comment_fragment));
        transaction.commit();
    }

}
