package com.example.developer.instgramclone.Login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.FirebaseMethods;
import com.example.developer.instgramclone.Utils.Validation;



public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private Context mContext = RegisterActivity.this;
    private ProgressBar loginProgress;
    private EditText email, password, fullName;
    private TextView pleaseWaitTxt;
    private Button regBtn;


    //firebase auth
    private FirebaseMethods firebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseMethods = new FirebaseMethods(mContext);

        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.userPassword);
        fullName = findViewById(R.id.userName);
        pleaseWaitTxt = findViewById(R.id.pleaseWait);
        loginProgress = findViewById(R.id.loginProgressbar);
        regBtn = findViewById(R.id.regBtn);
        hideProgressBar();
        signUpProcess();
    }


    private void signUpProcess() {
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAll()) {
                    showProgressBar();
                    firebaseMethods.registerNewEmail(email.getText().toString(), fullName.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    private boolean checkAll() {
        if (!Validation.checkInput(email.getText().toString())) {
            email.setError("Please Fill That Field");
            return false;
        }
        if (!Validation.checkInput(fullName.getText().toString())) {
            fullName.setError("Please Fill That Field");
            return false;
        }
        if (!Validation.checkInput(password.getText().toString())) {
            password.setError("Please Fill That Field");
            return false;
        }
        return true;
    }

    private void hideProgressBar() {
        loginProgress.setVisibility(View.GONE);
        pleaseWaitTxt.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        loginProgress.setVisibility(View.VISIBLE);
        pleaseWaitTxt.setVisibility(View.VISIBLE);
    }


}
