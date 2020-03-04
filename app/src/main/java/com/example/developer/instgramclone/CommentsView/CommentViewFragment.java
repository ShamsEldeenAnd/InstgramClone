package com.example.developer.instgramclone.CommentsView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.developer.instgramclone.Home.HomeActivity;
import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.CommentListAdapter;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentViewFragment extends Fragment {

    public CommentViewFragment() {
        super();
        setArguments(new Bundle());
    }

    //vars
    private Photo mPhoto;
    private ArrayList<Comment> comments;
    private FirebaseMethods firebaseMethods;
    private DatabaseReference myRef;
    private Context mContext;
    private CommentListAdapter listAdapter;
    //widgets
    private ImageView backArrow, checkMark;
    private EditText commentText;
    private ListView commentsList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);

        backArrow = view.findViewById(R.id.backarrow);
        checkMark = view.findViewById(R.id.ivPostComment);
        commentText = view.findViewById(R.id.comment);
        commentsList = view.findViewById(R.id.commentsList);

        firebaseMethods = new FirebaseMethods(getActivity());
        comments = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference();
        mContext = getActivity();


        setupWidgets();
        setupFireBaseAuth();

        return view;
    }

    private void setupFireBaseAuth() {

        //handle show the first comment
        if (mPhoto.getComments().size() == 0) {
            comments.clear();
            Comment firstComment = new Comment();
            firstComment.setComment(mPhoto.getCaption());
            firstComment.setUser_id(mPhoto.getUser_id());
            firstComment.setDate_created(mPhoto.getDate_created());
            comments.add(firstComment);
            mPhoto.setComments(comments);
            setupCommentAdapter();
        }

        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //choose the photo for my user
                        Query query = myRef.child(mContext.getString(R.string.dbname_photos))
                                .orderByChild(mContext.getString(R.string.photo_id))
                                .equalTo(mPhoto.getPhoto_id());

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

                                    comments.clear();
                                    Comment firstComment = new Comment();
                                    firstComment.setComment(mPhoto.getCaption());
                                    firstComment.setUser_id(mPhoto.getUser_id());
                                    firstComment.setDate_created(mPhoto.getDate_created());
                                    comments.add(firstComment);

                                    for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field_comments)).getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                                        comment.setComment(ds.getValue(Comment.class).getComment());
                                        comment.setDate_created(ds.getValue(Comment.class).getDate_created());
                                        comments.add(comment);
                                    }

                                    setupCommentAdapter();
                                    photo.setComments(comments);
                                    mPhoto = photo;

                                    //                    List<Like> likes = new ArrayList<>();
                                    //                    for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field_likes)).getChildren()) {
                                    //                        Like like = new Like();
                                    //                        like.setUser_id(ds.getValue(Like.class).getUser_id());
                                    //                        likes.add(like);
                                    //                    }
                                    //
                                    //                    photo.setLikes(likes);
                                    //                    photos.add(photo);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void resetComment() {
        commentText.setText("");
        //Todo
        //close the keyboard
    }

    private void setupWidgets() {

        // getting photo info from bundle
        try {
            mPhoto = getPhotoInfo();
        } catch (NullPointerException e) {
        }

        setupCommentAdapter();

        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!commentText.getText().toString().equals("")) {
                    firebaseMethods.insertNewComment(commentText.getText().toString(), mPhoto.getPhoto_id(), mPhoto.getUser_id());
                    resetComment();

                } else {
                    commentText.setError("Write comment !!");
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getCallingActivityFromBundle() != null) {
                    if (getCallingActivityFromBundle().equals(getString(R.string.home_activity))) {
                        getActivity().getSupportFragmentManager().popBackStack();
                        ((HomeActivity) getActivity()).showLayout();
                    } else {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });

    }

    private void setupCommentAdapter() {

        listAdapter = new CommentListAdapter(mContext, R.layout.layout_comment, comments);
        commentsList.setAdapter(listAdapter);


    }

    private Photo getPhotoInfo() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(mContext.getString(R.string.photo));
        } else {
            return null;
        }
    }

    private String getCallingActivityFromBundle() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getString(getString(R.string.home_activity));
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listAdapter.clear();
        comments.clear();
    }
}
