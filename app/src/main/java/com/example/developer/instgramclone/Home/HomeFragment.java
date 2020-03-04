package com.example.developer.instgramclone.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Like;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.MainfeedListAdapter;
import com.example.developer.instgramclone.Utils.SharedPrefHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {


    private ListView postsList;
    private ProgressBar postProgressBar;
    private String user_id, userName, profileImage;
    private MainfeedListAdapter adapter;
    private Context mContext;

    private UserAccountSettings settings;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        userName = SharedPrefHelper.getUserName(getActivity());
        user_id = SharedPrefHelper.getUserID(getActivity());
        profileImage = SharedPrefHelper.getUserImage(getActivity());
        settings = new UserAccountSettings();


        settings.setUser_id(user_id);
        settings.setUser_name(userName);
        settings.setProfile_photo(profileImage);

        mContext = getContext();
        postsList = view.findViewById(R.id.posts_list);
        postProgressBar = view.findViewById(R.id.postsProgressbar);
        retrieveFollowingIds();
        return view;
    }

    private void retrieveFollowingIds() {

        final ArrayList<String> ids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child(getActivity().getString(R.string.field_user_id)).getValue().toString();
                    ids.add(id);
                }
                getFollowingPosts(ids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingPosts(final ArrayList<String> followingUserId) {
         final ArrayList<Photo> mPhotos = new ArrayList<>();


        for (int i = 0; i < followingUserId.size(); i++) {
            final int count = i;
            //choose the photo for my user
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getActivity().getString(R.string.dbname_user_photos))
                    .child(followingUserId.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(followingUserId.get(i));

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
                        mPhotos.add(photo);
                    }
                    //setup image in grid view

                        setupPostAdapter(mPhotos);
                        hideProgressbar();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void setupPostAdapter(ArrayList<Photo> mPhotos) {

        adapter = new MainfeedListAdapter(mContext, R.layout.layout_mainfeed_listitem, mPhotos, settings);
        postsList.setAdapter(adapter);
    }


    private void hideProgressbar() {
        postProgressBar.setVisibility(View.GONE);
    }
}
