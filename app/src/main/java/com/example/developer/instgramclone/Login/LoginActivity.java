package com.example.developer.instgramclone.Login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.developer.instgramclone.Home.HomeActivity;
import com.example.developer.instgramclone.Models.UserAccountSettings;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.SharedPrefHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Context mContext = LoginActivity.this;
    private ProgressBar loginProgress;
    private EditText email, password;
    private TextView pleaseWaitTxt, linkSignup;
    private Button logBtn;
    //firebase auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.userPassword);
        pleaseWaitTxt = findViewById(R.id.pleaseWait);
        loginProgress = findViewById(R.id.loginProgressbar);
        logBtn = findViewById(R.id.loginBtn);
        linkSignup = findViewById(R.id.linkSignup);

        hideProgressBar();
        loginAndSignUpProcess();
    }

    private void loginAndSignUpProcess() {
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals("")) {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        try {
                                            if (user.isEmailVerified()) {
                                                showProgressBar();
                                                saveDataIntoSharedPref(user.getUid());


                                            } else {
                                                hideProgressBar();
                                                showUnverifiedDialog();
                                                mAuth.signOut();
                                            }
                                        } catch (NullPointerException e) {
                                            Log.e("NullPointerException ", e.getMessage());
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(mContext, "Password Error ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else email.setError("write email ");
            }
        });
        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToActivity(RegisterActivity.class);
            }
        });
    }

    private void saveDataIntoSharedPref(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_name_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    UserAccountSettings accountSettings = ds.getValue(UserAccountSettings.class);
                    SharedPrefHelper.saveUserSetting(accountSettings.getUser_name(), accountSettings.getUser_id(), accountSettings.getProfile_photo(), LoginActivity.this);
                    navigateToActivity(HomeActivity.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showUnverifiedDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setMessage("Your Email Not verified");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void hideProgressBar() {
        loginProgress.setVisibility(View.GONE);
        pleaseWaitTxt.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        loginProgress.setVisibility(View.VISIBLE);
        pleaseWaitTxt.setVisibility(View.VISIBLE);
    }

    private void navigateToActivity(Class activity) {
        Intent intent = new Intent(mContext, activity);
        startActivity(intent);
    }

}
