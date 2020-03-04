package com.example.developer.instgramclone.PostView;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Like;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.HeartToggle;
import com.example.developer.instgramclone.Utils.SharedPrefHelper;
import com.example.developer.instgramclone.Utils.SquareImageView;
import com.example.developer.instgramclone.Utils.StringManipulation;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;


public class ViewPostFragment extends Fragment {


    public interface OnCommentThreadSelectedListner {
        void OnCommentThreadSelected(Photo photo);
    }


    OnCommentThreadSelectedListner onCommentThreadSelectedListner;
    //  widgets
    private ImageView profilePhoto, likePhotoRed, commentPhoto, likePhotowhite, backarrow;
    private TextView profileName, postLikes, postCaption, commentLink, postTime;
    private SquareImageView postPhoto;
    private BottomNavigationViewEx bottomNavigationViewEx;
    //vars
    private Photo mPhoto;
    private int activityNum;
    private FirebaseMethods firebaseMethods;
    private UserAccountSettings accountSettings;


    //detect motion of clicking
    private GestureDetector gestureDetector;
    private HeartToggle heartToggle;

    //constructor
    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        profilePhoto = view.findViewById(R.id.profile_photo);
        postPhoto = view.findViewById(R.id.post_image);
        likePhotoRed = view.findViewById(R.id.image_heart_red);

        likePhotowhite = view.findViewById(R.id.image_heart_white);
        commentPhoto = view.findViewById(R.id.comment_image);
        backarrow = view.findViewById(R.id.backarrow);

        profileName = view.findViewById(R.id.profile_name);
        postLikes = view.findViewById(R.id.text_likes);
        postCaption = view.findViewById(R.id.text_caption);
        commentLink = view.findViewById(R.id.text_comment_link);
        postTime = view.findViewById(R.id.text_time_posted);

        bottomNavigationViewEx = view.findViewById(R.id.bottomNavViewBar);

        gestureDetector = new GestureDetector(getActivity(), new GestListner());

        likePhotoRed.setVisibility(View.GONE);
        likePhotowhite.setVisibility(View.VISIBLE);
        heartToggle = new HeartToggle(likePhotowhite, likePhotoRed);
        firebaseMethods = new FirebaseMethods(getActivity());


        setUpPhoto();
        setupBottomNavView();
        setupWidgets();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onCommentThreadSelectedListner = (OnCommentThreadSelectedListner) getActivity();
        } catch (ClassCastException e) {
        }
    }

    private void setUpPhoto() {
        // getting photo info from bundle
        try {
            mPhoto = getPhotoInfo();
            activityNum = getActivityNum();
        } catch (NullPointerException e) {
        }
    }


    //--------------getting info from bundle ----------------------
    private int getActivityNum() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getActivity().getString(R.string.activity_number));
        } else {
            return 0;
        }
    }

    private Photo getPhotoInfo() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getActivity().getString(R.string.photo));
        } else {
            return null;
        }
    }

    private UserAccountSettings getSettings() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getActivity().getString(R.string.settings));
        } else {
            return null;
        }
    }

    //----------setting navigation view ---------
    private void setupBottomNavView() {
        BottomNavigationViewHelper.setupBottomNavView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getActivity(), bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(activityNum);
        menuItem.setChecked(true);
    }


    //---------setupwidgets-----------

    private void setupWidgets() {
        //set post photo
        UniversalImageLoader.setImage(mPhoto.getImage_path(), postPhoto, null, "");

        //to view posts
        accountSettings = new UserAccountSettings();
        accountSettings.setUser_name(SharedPrefHelper.getUserName(getActivity()));
        accountSettings.setProfile_photo(SharedPrefHelper.getUserImage(getActivity()));
        accountSettings.setUser_id(SharedPrefHelper.getUserID(getActivity()));

        //there is 2 case
        //1- when coming from view profile fragment
        if (getSettings() != null) {
            profileName.setText(getSettings().getUser_name());
            UniversalImageLoader.setImage(getSettings().getProfile_photo(), profilePhoto, null, "");
        } else {
            //2-when coming from  profile fragment
            profileName.setText(SharedPrefHelper.getUserName(getActivity()));
            UniversalImageLoader.setImage(SharedPrefHelper.getUserImage(getActivity()), profilePhoto, null, "");
        }


        postCaption.setText(mPhoto.getCaption());

        // setting post date
        String timeStampDiff = StringManipulation.getTimeStampDifference(mPhoto.getDate_created());

        if (!timeStampDiff.equals("0")) {
            postTime.setText(timeStampDiff + " DAYS AGO");
        } else {
            postTime.setText("TODAY");
        }


        commentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommentThreadSelectedListner.OnCommentThreadSelected(mPhoto);
            }
        });
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        commentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommentThreadSelectedListner.OnCommentThreadSelected(mPhoto);
            }
        });


        refreshLike();
        toggleLikeBtn();


    }

    private void refreshLike() {
        Query query = FirebaseDatabase.getInstance().getReference().child(getActivity().getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(mPhoto.getPhoto_id())) {


                        List<Comment> comments = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                            comment.setComment(ds.getValue(Comment.class).getComment());
                            comment.setDate_created(ds.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }
                        mPhoto.setComments(comments);

                        List<Like> likes = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(ds.getValue(Like.class).getUser_id());
                            likes.add(like);
                        }
                        mPhoto.setLikes(likes);
                        firebaseMethods.getLikesFromDb(mPhoto,
                                accountSettings, postLikes, likePhotoRed, likePhotowhite);

                        setUpCommentsCount();
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setUpCommentsCount() {
        //setting comments number
        if (mPhoto.getComments() != null) {
            if (mPhoto.getComments().size() == 1)
                commentLink.setText("View " + mPhoto.getComments().size() + " Comment");
            else if (mPhoto.getComments().size() > 1)
                commentLink.setText("View all " + mPhoto.getComments().size() + " Comments");
        }
    }

    private void toggleLikeBtn() {
        likePhotowhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        likePhotoRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private class GestListner extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //case 1 if not liked --> insert new like
            //case 2 not liked
            if (likePhotoRed.getVisibility() == View.VISIBLE) {
                firebaseMethods.removeMyLike(mPhoto);
                heartToggle.toggleLike();
            } else {
                firebaseMethods.insertNewLike(mPhoto);
                heartToggle.toggleLike();
            }
            refreshLike();
            return true;
        }
    }
}
