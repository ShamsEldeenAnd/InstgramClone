package com.example.developer.instgramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.developer.instgramclone.Profile.AccountSettingActivity;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.Permissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


//this fragment as reusable fragment
public class PhotoFragment extends Fragment {

    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GELLARY_FRAGMENT_NUM = 0;
    private static final int CAMERA_REQUEST_CODE = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        ImageButton openCamera = view.findViewById(R.id.btnLunchCamera);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((ShareActivity) getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM) {
                    if (((ShareActivity) getActivity()).checkPermissions(Permissions.PERMISSIONS[2])) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    } else {
                        //navigate to share activity
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

    private boolean isRootActivity() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //there is image captured
        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap bitmap;
            //getting image from camera
            bitmap = (Bitmap) data.getExtras().get("data");
            if (isRootActivity()) {
                try {

                    Intent nextActivity  = new Intent(getActivity(), NextActivity.class);
                    nextActivity.putExtra(getString(R.string.selected_bitmap), bitmap);
                    startActivity(nextActivity);
                    getActivity().finish();

                } catch (NullPointerException e) {

                }
            } else {
                try {

                    Intent accountSetting = new Intent(getActivity(), AccountSettingActivity.class);
                    accountSetting.putExtra(getString(R.string.selected_bitmap), bitmap);
                    accountSetting.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                    startActivity(accountSetting);
                    getActivity().finish();

                } catch (NullPointerException e) {

                }
            }
        }
    }
}
