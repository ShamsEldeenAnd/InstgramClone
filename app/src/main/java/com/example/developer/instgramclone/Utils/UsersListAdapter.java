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
import com.example.developer.instgramclone.Models.User;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Objects.*;

public class UsersListAdapter extends ArrayAdapter<User> {


    private LayoutInflater inflater;
    private int layoutResource;
    private Context mContext;

    public UsersListAdapter(@NonNull Context context, int resource, @NonNull List<User> users) {
        super(context, resource, users);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }


    private static class ViewHolder {
        TextView username, email;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.username = convertView.findViewById(R.id.username);
            viewHolder.email = convertView.findViewById(R.id.email);
            viewHolder.profileImage = convertView.findViewById(R.id.user_profile_image);
            //store the view in memory for fast scroll
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.username.setText(getItem(position).getUser_name());
        viewHolder.email.setText(getItem(position).getEmail());

        //setting image
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        Query query = myRef
                .child(mContext.getString(R.string.db_name_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UniversalImageLoader.setImage(ds.getValue(UserAccountSettings.class).getProfile_photo(), viewHolder.profileImage
                            , null, "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convertView;
    }


}
