package com.example.developer.instgramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Like;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.User;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.Models.UserSettings;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.GridImageAdapter;
import com.example.developer.instgramclone.Utils.SharedPrefHelper;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    //interface for gridimage click listner
    public interface OnGridImageSelectedListner {
        void OnGridImageSelected(Photo photo, int activityNo , UserAccountSettings settings);
    }

    OnGridImageSelectedListner onGridImageSelectedListner;

    private static final int ACTIVITY_NUM = 4;

    private TextView mFollowes, mFolllowings, mPosts, mUsername, mDisplayname, mWebsite, mDescription, mEditProfile;
    private ProgressBar mProgressbar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationViewEx;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseMethods firebaseMethods;
    private String userID;


    //vars
    private static final int NUM_GRID_COLUMNS = 3;
    private  UserAccountSettings accountSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mFolllowings = view.findViewById(R.id.tvFollowing);
        mFollowes = view.findViewById(R.id.tvFollowers);
        mPosts = view.findViewById(R.id.tvPosts);
        mDisplayname = view.findViewById(R.id.displayName);
        mUsername = view.findViewById(R.id.profileName);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mProgressbar = view.findViewById(R.id.profileProgressbar);
        mProfilePhoto = view.findViewById(R.id.profilePhoto);
        gridView = view.findViewById(R.id.gridView);
        toolbar = view.findViewById(R.id.profileToolbar);
        profileMenu = view.findViewById(R.id.profileMenu);
        bottomNavigationViewEx = view.findViewById(R.id.bottomNavViewBar);
        mEditProfile = view.findViewById(R.id.textEditProfile);
        firebaseMethods = new FirebaseMethods(getActivity());

        setupBottomNavView();
        setupToolbar();
        setupFirebase();
        setupGridView();

        //edit profile listener
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
            }
        });
        return view;

    }

    @Override
    public void onAttach(Context context) {
        try {
            onGridImageSelectedListner = (OnGridImageSelectedListner) getActivity();
        } catch (ClassCastException e) {

        }
        super.onAttach(context);
    }

    // setup bottom navigation View
    private void setupBottomNavView() {
        BottomNavigationViewHelper.setupBottomNavView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getActivity(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    //setup toolbar
    private void setupToolbar() {
        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);
        //navigate to account setting
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);

                startActivity(intent);
            }
        });

    }

    private void setProfileWidget(UserSettings userSettings) {

         accountSettings = userSettings.getSettings();

        UniversalImageLoader.setImage(accountSettings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayname.setText(accountSettings.getDisplay_name());
        mWebsite.setText(accountSettings.getWebsite());
        mUsername.setText(accountSettings.getUser_name());
        mDescription.setText(accountSettings.getDescription());
        mPosts.setText(String.valueOf(accountSettings.getPosts()));
        mFollowes.setText(String.valueOf(accountSettings.getFollowers()));
        mFolllowings.setText(String.valueOf(accountSettings.getFollowing()));


    }

    private void setupGridView() {
        final ArrayList<Photo> photos = new ArrayList<>();

        //choose the photo for my user
        Query query = reference.child(getActivity().getString(R.string.dbname_user_photos))
                .child(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //solving the hashmap pro
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();


                    photo.setCaption(objectMap.get("caption").toString());

                    photo.setDate_created(objectMap.get("date_created").toString());

                    photo.setImage_path(objectMap.get("image_path").toString());

                    photo.setPhoto_id(objectMap.get("photo_id").toString());

                    photo.setTags(objectMap.get("tags").toString());

                    photo.setUser_id(objectMap.get("user_id").toString());

                    photo.setComments(new ArrayList<Comment>());

                    List<Like> likes = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.child(getString(R.string.field_likes)).getChildren()) {
                        Like like = new Like();
                        like.setUser_id(ds.getValue(Like.class).getUser_id());
                        likes.add(like);
                    }

                    photo.setLikes(likes);
                    photos.add(photo);
                }
                //setup image in grid view
                setupImageGrid(getImageUrls(photos));
                setUpClickListner(photos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpClickListner(final ArrayList<Photo> photos) {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onGridImageSelectedListner.OnGridImageSelected(photos.get(position), ACTIVITY_NUM,null);
            }
        });
    }

    private ArrayList<String> getImageUrls(ArrayList<Photo> photos) {
        //image urls
        ArrayList<String> urls = new ArrayList<>();

        for (int i = 0; i < photos.size(); i++) {
            urls.add(photos.get(i).getImage_path());
        }
        return urls;
    }

    //setup image gridvew
    private void setupImageGrid(ArrayList<String> imgUrls) {
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_image, "", imgUrls);
        gridView.setAdapter(adapter);
    }

//  --------------------  firebase staff-----------------------

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidget(firebaseMethods.getUseSettings(dataSnapshot, userID));
                hideProgressbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hideProgressbar() {
        mProgressbar.setVisibility(View.GONE);
    }
}
