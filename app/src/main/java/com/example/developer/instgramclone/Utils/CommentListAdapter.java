package com.example.developer.instgramclone.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.developer.instgramclone.Models.Comment;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<Comment> {


    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;


    public CommentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> comments) {
        super(context, resource, comments);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder {
        TextView comment, timestamp, username, reply, likes;
        CircleImageView profileImage;
        ImageView like;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.comment = convertView.findViewById(R.id.comment_text);
            viewHolder.timestamp = convertView.findViewById(R.id.comment_time_posted);
            viewHolder.username = convertView.findViewById(R.id.comment_username);
            viewHolder.reply = convertView.findViewById(R.id.comment_reply);
            viewHolder.likes = convertView.findViewById(R.id.comment_likes);
            viewHolder.profileImage = convertView.findViewById(R.id.comment_profile_image);
            viewHolder.like = convertView.findViewById(R.id.comment_like_image);

            //store the view in memory for fast scroll
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        //setting comment
        viewHolder.comment.setText(getItem(position).getComment());


        //getting time stamp
        String timeStampDiff = StringManipulation.getTimeStampDifference(getItem(position).getDate_created());

        if (!timeStampDiff.equals("0")) {
            viewHolder.timestamp.setText(timeStampDiff + " DAYS AGO");
        } else {
            viewHolder.timestamp.setText("TODAY");
        }



        //setting username and image
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef
                .child(mContext.getString(R.string.db_name_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    viewHolder.username.setText(ds.getValue(UserAccountSettings.class).getUser_name());
                    UniversalImageLoader.setImage(ds.getValue(UserAccountSettings.class).getProfile_photo(),viewHolder.profileImage
                    ,null,"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //the first comment
        if (position == 0) {
            viewHolder.like.setVisibility(View.GONE);
            viewHolder.likes.setVisibility(View.GONE);
            viewHolder.reply.setVisibility(View.GONE);

        }


        return convertView;
    }
}
