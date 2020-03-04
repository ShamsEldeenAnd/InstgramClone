package com.example.developer.instgramclone.Home;

import android.content.Context;

import com.example.developer.instgramclone.CommentsView.CommentViewFragment;
import com.example.developer.instgramclone.Login.LoginActivity;
import com.example.developer.instgramclone.Models.Photo;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.SectionPagerAdapter;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private Context mContext = HomeActivity.this;
    private static final int HOME_FRAGMENT = 1;
    //firebase auth
    private FirebaseAuth mFirebaseAuth;


    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mViewPager = findViewById(R.id.container);
        mFrameLayout = findViewById(R.id.home_container);
        mRelativeLayout = findViewById(R.id.parent_rel);

        initImageLoader();
        setupBottomNavView();

    }


    //setup view Pager
    private void setupViewPager() {
        SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new CameraFragment());
        pagerAdapter.addFragment(new HomeFragment());
        pagerAdapter.addFragment(new MessagesFragment());

        mViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_logo);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_message);




    }

    //setup bottm navigation View
    private void setupBottomNavView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //we should initialize image loader with configs cause it instance base
    private void initImageLoader() {
        UniversalImageLoader imageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }

    //-----------------------firebase staff--------------------------------


    //if user not sign in return to login screen
    private void navigateToLogin() {
        Intent login = new Intent(mContext, LoginActivity.class);
        startActivity(login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if ((currentUser != null) && currentUser.isEmailVerified()) {
            setupViewPager();
            mViewPager.setCurrentItem(HOME_FRAGMENT);

        } else {
            navigateToLogin();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mFrameLayout.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }

    public void showLayout() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }
    public void hideLayout(){
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity) {
        CommentViewFragment fragment = new CommentViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, fragment);
        transaction.addToBackStack(getString(R.string.view_comment_fragment));
        transaction.commit();

    }
}
