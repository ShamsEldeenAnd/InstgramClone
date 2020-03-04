package com.example.developer.instgramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.SectionStatePagerAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext = AccountSettingActivity.this;
    public SectionStatePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);

        viewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.rellayout1);

        setupBottomNavView();
        setupFragments();
        setupBackarrow();
        setupSettingsList();
        getIncomingActivity();
    }


    private void getIncomingActivity() {
        Intent intent = getIntent();

        //edit profile btn on profileActivity
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            setupViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile)));
        }
        if (intent.hasExtra(getString(R.string.selected_bitmap)) ||
                intent.hasExtra(getString(R.string.selected_image)))
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile))) {
                //coming from gallery
                if (intent.hasExtra(getString(R.string.selected_image))) {
                    FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0
                            , intent.getStringExtra(getString(R.string.selected_image)), null);
                }
                // coming from camera
                else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0, null
                            , (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }
            }
    }

    //setup fragments
    private void setupFragments() {
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile));
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out));
    }

    //setup viewPger
    public void setupViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }

    //setup backarrow Icon
    private void setupBackarrow() {
        ImageView back = findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    //setup setting list
    private void setupSettingsList() {
        ListView listView = findViewById(R.id.accountSettingsList);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add(getString(R.string.sign_out));
        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        //handle list item clicking
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setupViewPager(i);
            }
        });
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

}
