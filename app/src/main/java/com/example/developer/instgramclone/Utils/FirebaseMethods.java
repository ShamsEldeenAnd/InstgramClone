package com.example.developer.instgramclone.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developer.instgramclone.Home.HomeActivity;
import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Like;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.User;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.Models.UserSettings;
import com.example.developer.instgramclone.Profile.AccountSettingActivity;
import com.example.developer.instgramclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class FirebaseMethods {

    //firebase
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private DatabaseReference myRef;


    //vars
    private Context mContext;
    private String append;
    private ProgressDialog dialog;


    private boolean currentUserLike;
    private String likesString = "";


    public FirebaseMethods(Context mContext) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        this.mContext = mContext;
    }


    //returning query for username  from firebase
    public Query getQueryUsernameResult(String userName) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.db_name_users))
                .orderByChild(mContext.getString(R.string.user_name)).equalTo(StringManipulation.condenseUsername(userName));
        return query;
    }

    //register new user with new Auth
    public void registerNewEmail(final String email, final String fullName, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = user.getUid();
                            getQueryUsernameResult(fullName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String newfullName = fullName;
                                    for (DataSnapshot singleValue : dataSnapshot.getChildren()) {
                                        if (singleValue.exists()) {
                                            append = myRef.push().getKey().substring(3, 10);
                                            newfullName = fullName + append;
                                        }
                                    }
                                    addNewUser(email, ""
                                            , StringManipulation.condenseUsername(newfullName),
                                            "", "");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //after registering sending email verification
                            sendVerificationEmail();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    //adding new user into realtime DB
    private void addNewUser(String email, String description, String userName, String website, String profile_photo) {
        User user = new User(userID, userName, email, 1);
        myRef.child(mContext.getString(R.string.db_name_users))
                .child(userID)
                .setValue(user);
        UserAccountSettings accountSettings = new UserAccountSettings(description, userName, 0, 0, 0, profile_photo, userName, website, userID);
        myRef.child(mContext.getString(R.string.db_name_user_account_settings))
                .child(userID)
                .setValue(accountSettings);
    }

    //sending verification to email
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext, "Verification sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "couldn't send verification email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //retrieve user_account_settings from DB
    public UserSettings getUseSettings(DataSnapshot dataSnapshot, String userId) {
        UserAccountSettings accountSettings = new UserAccountSettings();
        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.db_name_user_account_settings)))
                accountSettings = ds.child(userId).getValue(UserAccountSettings.class);

            if (ds.getKey().equals(mContext.getString(R.string.db_name_users)))
                user = ds.child(userId).getValue(User.class);
        }
        return new UserSettings(user, accountSettings);
    }


    public void updateUsername(String userName, String myUserId) {
        myRef.child(mContext.getString(R.string.db_name_users)).child(myUserId)
                .child(mContext.getString(R.string.user_name))
                .setValue(StringManipulation.condenseUsername(userName));

        myRef.child(mContext.getString(R.string.db_name_user_account_settings)).child(myUserId)
                .child(mContext.getString(R.string.user_name))
                .setValue(StringManipulation.condenseUsername(userName));
    }

    public void updateEmail(String email, String myUserId) {
        myRef.child(mContext.getString(R.string.db_name_users)).child(myUserId)
                .child(mContext.getString(R.string.user_email))
                .setValue(email);

    }

    //update user setting
    public void updateUserSetting(String displayName, String description, String website, long phoneNumber, String myUserId) {
        if (displayName != null) {
            myRef.child(mContext.getString(R.string.db_name_user_account_settings)).child(myUserId)
                    .child(mContext.getString(R.string.display_name))
                    .setValue(displayName);
        }
        if (website != null) {
            myRef.child(mContext.getString(R.string.db_name_user_account_settings)).child(myUserId)
                    .child(mContext.getString(R.string.user_website))
                    .setValue(website);
        }
        if (description != null) {
            myRef.child(mContext.getString(R.string.db_name_user_account_settings)).child(myUserId)
                    .child(mContext.getString(R.string.user_description))
                    .setValue(description);
        }
        if (phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.db_name_users)).child(myUserId)
                    .child(mContext.getString(R.string.phone_number))
                    .setValue(phoneNumber);
        }
    }


    //return total num of photos for one user
    public int getImageCount(DataSnapshot snapshot) {
        int count = 0;
        for (DataSnapshot ds : snapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(getUserID())
                .getChildren()
        ) {
            count++;
        }
        return count;
    }

    public void uploadNewPhoto(String photo_Type, final String caption, int imageCount, String imgUrl, Bitmap bm) {
        FilePath filePath = new FilePath();
        String U_ID = getUserID();
        final StorageReference refrence;
        Bitmap image = bm;
        byte[] bytes;
        final UploadTask uploadTask;


        if (photo_Type.equals(mContext.getString(R.string.new_photo))) {

            refrence = mStorageRef
                    .child(filePath.FIRE_BASE_IMAGE_STORAGE + "/" + U_ID + "/photo" + (imageCount + 1));

            //when coming from gallery fragment
            if (bm == null) {
                image = ImageManager.getBitmap(imgUrl);
            }

            bytes = ImageManager.getBytesFromBitmap(image, 100);

            uploadTask = refrence.putBytes(bytes);

            //set up the progressbar
            setUpProgress(uploadTask);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    return refrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Uri downloadUri = task.getResult();
                        //add photo url to firebase
                        addPhotoToDatabase(caption, downloadUri.toString());
                        //navigate to home screen
                        Intent homeScreen = new Intent(mContext, HomeActivity.class);
                        mContext.startActivity(homeScreen);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Upload photo filed !!", Toast.LENGTH_SHORT).show();
                }
            });


        } else if (photo_Type.equals(mContext.getString(R.string.profile_photo))) {

            refrence = mStorageRef
                    .child(filePath.FIRE_BASE_IMAGE_STORAGE + "/" + U_ID + "/profile_photo");

            //when coming from gallery fragment
            if (bm == null) {
                image = ImageManager.getBitmap(imgUrl);
            }


            bytes = ImageManager.getBytesFromBitmap(image, 100);

            uploadTask = refrence.putBytes(bytes);

            //set up the progressbar
            setUpProgress(uploadTask);
            //navigate to edit profile
            navigateToEditProfile();

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return refrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Uri downloadUri = task.getResult();
                        //add profile photo to database
                        setProfilePhoto(downloadUri.toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "Upload photo filed !!", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void navigateToEditProfile() {
        ((AccountSettingActivity) mContext).setupViewPager(((AccountSettingActivity) mContext).
                pagerAdapter.getFragmentNumber(mContext.getString(R.string.edit_profile)));
    }

    private void setUpProgress(UploadTask uploadTask) {
        dialog = new ProgressDialog(mContext);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setMessage("Uploading   " + String.format("%.0f", progress) + "%");
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }

    private void setProfilePhoto(String imageUrl) {
        myRef.child(mContext.getString(R.string.db_name_user_account_settings)).
                child(getUserID()).
                child(mContext.getString(R.string.profile_photo)).setValue(imageUrl);
    }

    private void addPhotoToDatabase(String caption, String fireBaseUrl) {
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        String tags = StringManipulation.getTags(caption);
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setTags(tags);
        photo.setUser_id(getUserID());
        photo.setImage_path(fireBaseUrl);
        photo.setPhoto_id(newPhotoKey);

        //insert to db
        insertPhotoInTable(photo, newPhotoKey);
    }

    private void insertPhotoInTable(Photo photo, String newPhotoKey) {
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(getUserID()).child(newPhotoKey).setValue(photo);
    }

    //insert new like  2 node to insert "photo "

    public void insertNewLike(Photo photo) {

        Like like = new Like();
        like.setUser_id(getUserID());
        String newLikeId = myRef.push().getKey();

        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeId)
                .setValue(like);

        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeId)
                .setValue(like);
    }

    public void removeMyLike(final Photo photo) {
        Query query = myRef.child(mContext.getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(Like.class).getUser_id().equals(getUserID())) {
                        // delete from photos table
                        myRef.child(mContext.getString(R.string.dbname_photos))
                                .child(photo.getPhoto_id())
                                .child(mContext.getString(R.string.field_likes))
                                .child(ds.getKey())
                                .removeValue();

                        //delete from user_photos table
                        myRef.child(mContext.getString(R.string.dbname_user_photos))
                                .child(photo.getUser_id())
                                .child(photo.getPhoto_id())
                                .child(mContext.getString(R.string.field_likes))
                                .child(ds.getKey())
                                .removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //retrieve  Likes To database
    public void getLikesFromDb(final Photo photo, final UserAccountSettings accountSettings, final TextView likesText,
                               final ImageView likeImageRed, final ImageView likeImageWhite) {
        final ArrayList<Like> likes = new ArrayList<>();

        Query query = myRef.child(mContext.getString(R.string.dbname_photos))
                .child(photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //case 1 user already liked the photo
                    likes.add(ds.getValue(Like.class));
                    //case  2 user don't like the photo
                }

                if (likes.size() > 0)
                    getLikeUserNames(likes, accountSettings, likesText, likeImageRed, likeImageWhite);
                else {
                    likesText.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getLikeUserNames(final ArrayList<Like> likes, final UserAccountSettings accountSettings
            , final TextView likestext, final ImageView likeImageRed, final ImageView likeImageWhite) {
        final StringBuilder usernames = new StringBuilder();

        //photo id
        for (int i = 0; i < likes.size(); i++) {
            Query query = myRef.child(mContext.getString(R.string.db_name_users))
                    .orderByChild(mContext.getString(R.string.field_user_id))
                    .equalTo(likes.get(i).getUser_id());


            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        usernames.append(ds.getValue(User.class).getUser_name());
                        usernames.append(",");

                    }


                    String[] splitUsers = usernames.toString().split(",");
                    // checking if current user like or not
                    if (usernames.toString().contains(accountSettings.getUser_name())) {

                        likeImageRed.setVisibility(View.VISIBLE);
                        likeImageWhite.setVisibility(View.GONE);
                    }
                    int userLength = splitUsers.length;

                    switch (userLength) {
                        case 0:
                            likesString = "";
                            break;
                        case 1:
                            likesString = "Likes by " + splitUsers[0];
                            break;
                        case 2:
                            likesString = "Likes by " + splitUsers[0] + " ," + splitUsers[1];
                            break;
                        case 3:
                            likesString = "Likes by " + splitUsers[0] + " ," + splitUsers[1] + " and" + splitUsers[2];
                            break;
                        default:
                            likesString = "Likes by " + splitUsers[0] + " ," + splitUsers[1] + " ," + splitUsers[2] + " and " + (userLength - 3) + " others";
                    }
                    if (splitUsers.length == likes.size()) {
                        likestext.setText(likesString);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //insert new comment
    public void insertNewComment(String newComment, String photo_id ,String user_id) {
        String commentID = myRef.push().getKey();
        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimeStamp());
        comment.setUser_id(getUserID());

        myRef = FirebaseDatabase.getInstance().getReference();
        //insert into photo node
        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(photo_id)
                .child(mContext.getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);
        //insert into user_photo node
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(user_id)
                .child(photo_id)
                .child(mContext.getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);
    }


    public void test(final String photo_id , Comment firstComment) {
        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(photo_id)
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //choose the photo for my user
                        Query query = myRef.child(mContext.getString(R.string.dbname_photos))
                                .orderByChild(mContext.getString(R.string.photo_id))
                                .equalTo(photo_id);

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

                                    List<Comment> comments = new ArrayList<>();
                                    for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field_comments)).getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                                        comment.setComment(ds.getValue(Comment.class).getComment());
                                        comment.setDate_created(ds.getValue(Comment.class).getDate_created());
                                        comments.add(comment);
                                    }

                                    photo.setComments(comments);
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

    private String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Africa/Cairo"));
        return dateFormat.format(new Date());
    }

    public String getUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
