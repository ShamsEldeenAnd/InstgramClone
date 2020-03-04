package com.example.developer.instgramclone.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developer.instgramclone.Dialogs.ConfirmPasswordDialog;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.Models.UserSettings;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Share.ShareActivity;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.StringManipulation;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListner {

    private ImageView backArrow, profilePhoto, saveImage;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseMethods firebaseMethods;
    private String userID;

    private EditText mDisplayname, mUsername, mPhonenumber, mWebsite, mDescription, mEmail;
    private TextView changeProfilePhoto;
    //global vars
    private UserSettings mUsersettings;

    //update user email
    @Override
    public void onConfirmPassword(String password) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    //the email already in use
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getSignInMethods().size() == 1) {
                                            Toast.makeText(getActivity(), "The Email Already in Use !!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            user.updateEmail(mEmail.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                firebaseMethods.updateEmail(mEmail.getText().toString(), userID);
                                                                Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        profilePhoto = view.findViewById(R.id.profilePhoto);
        backArrow = view.findViewById(R.id.backarrow);
        saveImage = view.findViewById(R.id.saveChanges);
        mDescription = view.findViewById(R.id.settingDescription);
        mDisplayname = view.findViewById(R.id.settingDisplayName);
        mPhonenumber = view.findViewById(R.id.settingPhone);
        mWebsite = view.findViewById(R.id.settingWebsite);
        mEmail = view.findViewById(R.id.settingEmail);
        mUsername = view.findViewById(R.id.settingUsername);
        changeProfilePhoto = view.findViewById(R.id.changeProfilePhoto);

        firebaseMethods = new FirebaseMethods(getActivity());
        setupFirebase();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileSettings();
            }
        });
        return view;

    }


    //add new changes to db with check that every thing is uniqe
    private void saveProfileSettings() {
        final String displayName = mDisplayname.getText().toString();
        final String email = mEmail.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String userName = mUsername.getText().toString();
        final long phoneNumber = Long.parseLong(mPhonenumber.getText().toString());

        //listen only once
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //checking using our vars mUsersetting and perform query to db

                if (!mUsersettings.getUser().getUser_name().equals(userName)) {
                    checkIfUserNameExists(userName);
                }
                if (!mUsersettings.getUser().getEmail().equals(email)) {

                    ConfirmPasswordDialog passwordDialog = new ConfirmPasswordDialog();
                    passwordDialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
                    passwordDialog.setTargetFragment(EditProfileFragment.this, 1);
                }
                if (!(mUsersettings.getUser().getPhone_number() == phoneNumber)) {
                    firebaseMethods.updateUserSetting(null, null, null, phoneNumber, userID);
                }
                if (!mUsersettings.getSettings().getDisplay_name().equals(displayName)) {
                    firebaseMethods.updateUserSetting(displayName, null, null, 0, userID);
                }
                if (!mUsersettings.getSettings().getDescription().equals(description)) {
                    firebaseMethods.updateUserSetting(null, description, null, 0, userID);

                }
                if (!mUsersettings.getSettings().getWebsite().equals(website)) {
                    firebaseMethods.updateUserSetting(null, null, website, 0, userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkIfUserNameExists(final String userName) {
        firebaseMethods.getQueryUsernameResult(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleValue : dataSnapshot.getChildren()) {
                    if (singleValue.exists()) {
                        Toast.makeText(getActivity(), "User Name Already Exists !!", Toast.LENGTH_SHORT).show();
                    }
                }
                firebaseMethods.updateUsername(userName, userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidget(UserSettings userSettings) {
        mUsersettings = userSettings;
        UserAccountSettings accountSettings = userSettings.getSettings();
        UniversalImageLoader.setImage(accountSettings.getProfile_photo(), profilePhoto, null, "");
        mDisplayname.setText(accountSettings.getDisplay_name());
        mWebsite.setText(accountSettings.getWebsite());
        mUsername.setText(accountSettings.getUser_name());
        mDescription.setText(accountSettings.getDescription());
        mPhonenumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));
        mEmail.setText(userSettings.getUser().getEmail());


        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                startActivity(intent);
                getActivity().finish();
            }
        });
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
//                hideProgressbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//    private void hideProgressbar() {
//        mProgressbar.setVisibility(View.GONE);
//    }
}
