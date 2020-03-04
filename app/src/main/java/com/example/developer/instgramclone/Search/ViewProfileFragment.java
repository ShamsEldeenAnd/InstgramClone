package com.example.developer.instgramclone.Search;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Like;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.User;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.Models.UserSettings;
import com.example.developer.instgramclone.Profile.AccountSettingActivity;
import com.example.developer.instgramclone.Profile.ProfileActivity;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.GridImageAdapter;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileFragment extends Fragment {

    //interface for gridimage click listner
    public interface OnGridImageSelectedListner {
        void OnGridImageSelected(Photo photo, int activityNo, UserAccountSettings settings);
    }

    OnGridImageSelectedListner onGridImageSelectedListner;

    private static final int ACTIVITY_NUM = 4;

    private TextView mFollowes, mFolllowings, mPosts, mUsername, mDisplayname, mWebsite, mDescription, mfollow;
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


    //vars
    private static final int NUM_GRID_COLUMNS = 3;
    private UserAccountSettings accountSettings;
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;
    //step 1 get user from bundle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
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
        mfollow = view.findViewById(R.id.follow);
        firebaseMethods = new FirebaseMethods(getActivity());


        setupBottomNavView();
        setupToolbar();
        setupFirebase(getUserFrombundle().getUser_id());
        setupGridView();



        mfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mean i'm follow that user
                if (mfollow.getText().toString().equals("Follow")) {
                    insertFollow();
                } else if (mfollow.getText().toString().equals("Un Follow")) {
                    removeFollow();
                }
            }
        });
        return view;

    }


    private void removeFollow() {
        reference.child(getString(R.string.dbname_following))
                .child(mAuth.getUid())
                .child(getUserFrombundle().getUser_id())
                .removeValue();

        //mean i'm follower to him
        reference.child(getString(R.string.dbname_followers))
                .child(getUserFrombundle().getUser_id())
                .child(mAuth.getUid())
                .removeValue();

        setUnFollowing();
        //refresh view
        getFollowingCount();
        getFollowersCount();
        getPostsCount();
    }

    private void insertFollow() {
        reference.child(getString(R.string.dbname_following))
                .child(mAuth.getUid())
                .child(getUserFrombundle().getUser_id())
                .child(getString(R.string.field_user_id))
                .setValue(getUserFrombundle().getUser_id());

        //mean i'm follower to him
        reference.child(getString(R.string.dbname_followers))
                .child(getUserFrombundle().getUser_id())
                .child(mAuth.getUid())
                .child(getString(R.string.field_user_id))
                .setValue(mAuth.getUid());
        setFollowing();

        //refresh view
        getFollowingCount();
        getFollowersCount();
        getPostsCount();
    }

    private void setFollowing() {
        mfollow.setBackgroundResource(R.drawable.unfollow_btn);
        mfollow.setText("Un Follow");
    }

    private void setUnFollowing() {
        mfollow.setBackgroundResource(R.drawable.follow_btn);
        mfollow.setText("Follow");
    }

    private void isFollowing() {
        Query query = reference.child(getString(R.string.dbname_following))
                .child(mAuth.getUid()).orderByChild(getString(R.string.field_user_id)).equalTo(getUserFrombundle().getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    setFollowing();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getFollowersCount() {
        mFollowersCount = 0;
        Query query = reference.child(getString(R.string.dbname_followers))
                .child(getUserFrombundle().getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mFollowersCount++;
                }
                mFollowes.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount() {
        mFollowingCount = 0;
        Query query = reference.child(getString(R.string.dbname_following))
                .child(getUserFrombundle().getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mFollowingCount++;
                }

                mFolllowings.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getPostsCount() {
        mPostsCount = 0;
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(getUserFrombundle().getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mPostsCount++;
                }
                if (mPostsCount>0){
//                    hideProgressbar();
                    mPosts.setText(String.valueOf(mPostsCount));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        try {
            onGridImageSelectedListner = (OnGridImageSelectedListner) getActivity();
        } catch (ClassCastException e) {

        }
        super.onAttach(context);
    }

    //getting user info from bundle
    private User getUserFrombundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.user));
        } else {
            return null;
        }
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
                .child(getUserFrombundle().getUser_id());
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

                isFollowing();
                getFollowingCount();
                getFollowersCount();
                getPostsCount();
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
                onGridImageSelectedListner.OnGridImageSelected(photos.get(position), ACTIVITY_NUM, accountSettings);
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

    private void setupFirebase(final String user_id) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProfileWidget(firebaseMethods.getUseSettings(dataSnapshot, user_id));
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
