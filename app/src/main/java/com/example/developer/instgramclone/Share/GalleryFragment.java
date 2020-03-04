package com.example.developer.instgramclone.Share;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.developer.instgramclone.Profile.AccountSettingActivity;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.FilePath;
import com.example.developer.instgramclone.Utils.FileSearch;
import com.example.developer.instgramclone.Utils.GridImageAdapter;
import com.example.developer.instgramclone.Utils.UniversalImageLoader;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GalleryFragment extends Fragment {


    //decalaring views
    private ProgressBar gellaryProgress;
    private TextView nextBtn;
    private Spinner directorySpinner;
    private ImageView closeGellary, gellaryImage;
    private GridView gellaryGrid;

    //vars
    private ArrayList<String> directories;
    private static final int NUM_GRID_COLUMNS = 4;
    private static final String mAppend = "file:/";
    private String mSelectedImg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        gellaryProgress = view.findViewById(R.id.progressBar);
        nextBtn = view.findViewById(R.id.nextbtn);
        directorySpinner = view.findViewById(R.id.directorySpinner);
        closeGellary = view.findViewById(R.id.closeShare);
        gellaryGrid = view.findViewById(R.id.gridView);
        gellaryImage = view.findViewById(R.id.galleryImage);

        //hide progressbar
        hideProgressBar();
        //intit spinner and gridview
        init();
        //moving to the next screen


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRootActivity()) {
                    Intent nextAct = new Intent(getActivity(), NextActivity.class);
                    nextAct.putExtra(getString(R.string.selected_image), mSelectedImg);
                    startActivity(nextAct);
                } else {
                    Intent accountSetting = new Intent(getActivity(), AccountSettingActivity.class);
                    accountSetting.putExtra(getString(R.string.selected_image), mSelectedImg);
                    accountSetting.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                    startActivity(accountSetting);
                    getActivity().finish();
                }
            }
        });

        //closing the share
        closeGellary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
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


    private void init() {
        FilePath filePath = new FilePath();
        if (FileSearch.getDirectorypath(filePath.PICTURES) != null) {
            directories = FileSearch.getDirectorypath(filePath.PICTURES);
        }
        directories.add(filePath.CAMERA);
        directories.add(filePath.DOWNLOADS);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getDirectoriesNames(directories));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //clearing grid view
                gellaryGrid.setAdapter(null);
                //setup grid view with selected file
                setupGridView(directories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setupGridView(String selectedDir) {
        final ArrayList<String> imgUrls = FileSearch.getFilepath(selectedDir);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMNS;
        gellaryGrid.setColumnWidth(imageWidth);
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_image, mAppend, imgUrls);
        gellaryGrid.setAdapter(adapter);

        //set first image in view
        if (imgUrls.size() > 0) {
            UniversalImageLoader.setImage(imgUrls.get(0), gellaryImage, gellaryProgress, mAppend);
            mSelectedImg = imgUrls.get(0);
        }

        gellaryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UniversalImageLoader.setImage(imgUrls.get(i), gellaryImage, gellaryProgress, mAppend);
                mSelectedImg = imgUrls.get(i);

            }
        });

    }

    //show only the dir name
    private ArrayList<String> getDirectoriesNames(ArrayList<String> directories) {
        ArrayList<String> directiryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/");
            String name = directories.get(i).substring(index);
            directiryNames.add(name);
        }
        return directiryNames;
    }

    private void hideProgressBar() {
        gellaryProgress.setVisibility(View.GONE);
    }


}
