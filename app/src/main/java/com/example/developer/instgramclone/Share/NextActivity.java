package com.example.developer.instgramclone.Share;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class NextActivity extends AppCompatActivity {


    //Views declaration
    private ImageView backArrow, shareImage;
    private TextView shareBtn;
    private EditText description;

    //firebase staff
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseMethods firebaseMethods;
    private String userID;

    //vars
    private static final String mAppend = "file:/";
    private Context mContext = NextActivity.this;
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap shareBitmap;
//    private Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        firebaseMethods = new FirebaseMethods(mContext);

        backArrow = findViewById(R.id.returnShare);
        shareBtn = findViewById(R.id.shareBtn);
        shareImage = findViewById(R.id.shareImage);
        description = findViewById(R.id.description);


        setupFirebase();
        setupShareImage();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload to the fire base
                String caption = description.getText().toString();
                if (getIntent().hasExtra(getString(R.string.selected_image))) {
                    imgUrl = getIntent().getStringExtra(getString(R.string.selected_image));
                    firebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl, null);
                } else if (getIntent().hasExtra(getString(R.string.selected_bitmap))) {
                    shareBitmap = getIntent().getParcelableExtra(getString(R.string.selected_bitmap));
                    firebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, shareBitmap);

                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupShareImage() {
        if (getIntent().hasExtra(getString(R.string.selected_image))) {
            imgUrl = getIntent().getStringExtra(getString(R.string.selected_image));
            UniversalImageLoader.setImage(imgUrl, shareImage, null, mAppend);
        } else if (getIntent().hasExtra(getString(R.string.selected_bitmap))) {
            shareBitmap = getIntent().getParcelableExtra(getString(R.string.selected_bitmap));
            shareImage.setImageBitmap(shareBitmap);
        }
    }

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
                imageCount = firebaseMethods.getImageCount(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void testDownload() {

//        File localFile = null;
//        try {
//            localFile = File.createTempFile("images", "jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mStorageRef.getFile(localFile)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });
    }
}
