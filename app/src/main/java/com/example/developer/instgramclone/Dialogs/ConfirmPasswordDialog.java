package com.example.developer.instgramclone.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.Validation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmPasswordDialog extends DialogFragment {

    //by that we are able to send vars from dialog direct to the fragment
    public interface OnConfirmPasswordListner {
        public void onConfirmPassword(String password);
    }

    OnConfirmPasswordListner monConfirmPasswordListner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_dialog_layout, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        TextView cancelDialog = view.findViewById(R.id.dialogCancel);
        TextView confirmDialog = view.findViewById(R.id.dialogConfirm);
        final EditText confirmPassword = view.findViewById(R.id.confirmPassword);

        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = confirmPassword.getText().toString();
                if (Validation.checkInput(password)) {
                    monConfirmPasswordListner.onConfirmPassword(password);
                    getDialog().dismiss();
                } else {
                    confirmPassword.setError("Password Field Empty !!");
                }
            }
        });

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            monConfirmPasswordListner = (OnConfirmPasswordListner) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e("error", e.getMessage());
        }
    }

}
