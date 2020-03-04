package com.example.developer.instgramclone.Utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.developer.instgramclone.Home.HomeActivity;
import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.Like;
import com.example.developer.instgramclone.Models.Photo;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainfeedListAdapter extends ArrayAdapter<Photo> {

    //vars
    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    private UserAccountSettings settings;


    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> photos, UserAccountSettings settings) {
        super(context, resource, photos);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
        this.settings = settings;


    }

    static class ViewHolder {
        CircleImageView profileImage;
        TextView userName, caption, commentLink, likes, timePosted;
        ImageView imageLikesRed, imageLikesWhite, imageComment;
        SquareImageView postImage;

        FirebaseMethods firebaseMethods;
        HeartToggle heartToggle;
        GestureDetector detector;
        Photo photo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.caption = convertView.findViewById(R.id.text_caption);
            viewHolder.userName = convertView.findViewById(R.id.profile_name);
            viewHolder.commentLink = convertView.findViewById(R.id.text_comment_link);
            viewHolder.likes = convertView.findViewById(R.id.text_likes);
            viewHolder.timePosted = convertView.findViewById(R.id.text_time_posted);
            viewHolder.profileImage = convertView.findViewById(R.id.profile_photo);
            viewHolder.imageComment = convertView.findViewById(R.id.comment_image);
            viewHolder.imageLikesRed = convertView.findViewById(R.id.image_heart_red);
            viewHolder.imageLikesWhite = convertView.findViewById(R.id.image_heart_white);
            viewHolder.postImage = convertView.findViewById(R.id.post_image);


            viewHolder.heartToggle = new HeartToggle(viewHolder.imageLikesWhite, viewHolder.imageLikesRed);

            viewHolder.firebaseMethods = new FirebaseMethods(mContext);



            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.photo = getItem(position);

        viewHolder.caption.setText(viewHolder.photo.getCaption());

        String timeStampDiff = StringManipulation.getTimeStampDifference(viewHolder.photo.getDate_created());
        if (!timeStampDiff.equals("0")) {
            viewHolder.timePosted.setText(timeStampDiff + " DAYS AGO");
        } else {
            viewHolder.timePosted.setText("TODAY");
        }

        UniversalImageLoader.setImage(viewHolder.photo.getImage_path(), viewHolder.postImage
                , null, "");
        viewHolder.detector = new GestureDetector(mContext, new GestListner(viewHolder));


        getPostOwnerInfo(viewHolder, viewHolder.photo.getUser_id());
        viewHolder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) mContext).onCommentThreadSelected(getItem(position),
                        mContext.getString(R.string.home_activity));

                //going to need to do something else?
                ((HomeActivity) mContext).hideLayout();
            }
        });

        viewHolder.commentLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) mContext).onCommentThreadSelected(getItem(position),
                        mContext.getString(R.string.home_activity));

                //going to need to do something else?
                ((HomeActivity) mContext).hideLayout();
            }
        });

        toggleLikeBtn(viewHolder);
        refreshLike(viewHolder);

        return convertView;
    }

    private class GestListner extends GestureDetector.SimpleOnGestureListener {

        ViewHolder holder;

        public GestListner(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //case 1 if not liked --> insert new like
            //case 2 not liked
            if (holder.imageLikesRed.getVisibility() == View.VISIBLE) {
                holder.firebaseMethods.removeMyLike(holder.photo);
                holder.heartToggle.toggleLike();
            } else {
                holder.firebaseMethods.insertNewLike(holder.photo);
                holder.heartToggle.toggleLike();

            }
            refreshLike(holder);
            return true;
        }
    }

    private void refreshLike(final ViewHolder holder) {
        Query query = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(holder.photo.getPhoto_id())) {


                        List<Comment> comments = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(ds.getValue(Comment.class).getUser_id());
                            comment.setComment(ds.getValue(Comment.class).getComment());
                            comment.setDate_created(ds.getValue(Comment.class).getDate_created());

                            comments.add(comment);
                        }
                        holder.photo.setComments(comments);

                        List<Like> likes = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.child(mContext.getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(ds.getValue(Like.class).getUser_id());
                            likes.add(like);
                        }
                        holder.photo.setLikes(likes);
                        holder.firebaseMethods.getLikesFromDb(holder.photo,
                                settings, holder.likes, holder.imageLikesRed, holder.imageLikesWhite);

                        setUpCommentsCount(holder);
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostOwnerInfo(final ViewHolder holder, String userId) {
        Query query = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.db_name_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id)).equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    holder.userName.setText(ds.getValue(UserAccountSettings.class).getUser_name());
                    UniversalImageLoader.setImage(ds.getValue(UserAccountSettings.class).getProfile_photo(), holder.profileImage
                            , null, "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpCommentsCount(ViewHolder holder) {
        //setting comments number
        if (holder.photo.getComments() != null) {
            if (holder.photo.getComments().size() == 1)
                holder.commentLink.setText("View " + holder.photo.getComments().size() + " Comment");
            else if (holder.photo.getComments().size() > 1)
                holder.commentLink.setText("View all " + holder.photo.getComments().size() + " Comments");
        }
    }

    private void toggleLikeBtn(final ViewHolder holder) {
        holder.imageLikesWhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return holder.detector.onTouchEvent(motionEvent);
            }
        });

        holder.imageLikesRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return holder.detector.onTouchEvent(motionEvent);
            }
        });
    }
}



