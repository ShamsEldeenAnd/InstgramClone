package com.example.developer.instgramclone.Profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.developer.instgramclone.Login.LoginActivity;
import com.example.developer.instgramclone.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignOutFragment extends Fragment {

    private TextView tvProgressbar;
    private Button signOut;
    private ProgressBar signOutProgress;

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);
        auth = FirebaseAuth.getInstance();
        tvProgressbar = view.findViewById(R.id.tvsignOut);
        signOutProgress = view.findViewById(R.id.signOutProgressbar);
        signOut = view.findViewById(R.id.btnSignout);
        hideProgressBar();
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                auth.signOut();
                getActivity().finish();
                navigateToLogin();
            }
        });
        return view;
    }

    private void hideProgressBar() {
        signOutProgress.setVisibility(View.GONE);
        tvProgressbar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        signOutProgress.setVisibility(View.VISIBLE);
        tvProgressbar.setVisibility(View.VISIBLE);
    }

    //if user not sign in return to login screen
    private void navigateToLogin() {
        Intent login = new Intent(getActivity(), LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
    }
}
