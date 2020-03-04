package com.example.developer.instgramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.Permissions;
import com.example.developer.instgramclone.Utils.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ShareActivity extends AppCompatActivity {

    //constants
    private static final int VERIFY_REQUEST_CODE = 1;

    private static final String TAG = "ShareActivity";
    private static final int ACTIVITY_NUM = 2;
    private Context mContext = ShareActivity.this;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            //call viewpager
            setupViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }


    public int getTask() {
        return getIntent().getFlags();
    }
    //-----------------------------check permission methods ----------------------------------

    private boolean checkPermissionsArray(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    //checking every single permission
    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            //no permission granted
            return false;
        } else {
            return true;
        }
    }

    private void verifyPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(ShareActivity.this, permissions, VERIFY_REQUEST_CODE);
    }


    //------------------------------------------------------------------------

    //setup bottm navigation View
    private void setupBottomNavView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    //setup view Pager
    private void setupViewPager() {
        SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new GalleryFragment());
        pagerAdapter.addFragment(new PhotoFragment());

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Gallery");
        tabLayout.getTabAt(1).setText("Photo");


    }

    public int getCurrentTabNumber() {
        return viewPager.getCurrentItem();
    }
}
