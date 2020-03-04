package com.example.developer.instgramclone.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.recyclerview.widget.SnapHelper;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.developer.instgramclone.Models.User;
import com.example.developer.instgramclone.Profile.ProfileActivity;
import com.example.developer.instgramclone.R;
import com.example.developer.instgramclone.Utils.BottomNavigationViewHelper;
import com.example.developer.instgramclone.Utils.CommentListAdapter;
import com.example.developer.instgramclone.Utils.UsersListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = SearchActivity.this;


    private ListView usersListView;
    private EditText search;

    //vars
    private List<User> users;
    private UsersListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = findViewById(R.id.search);
        usersListView = findViewById(R.id.usersListView);


        initTextListener();
        setupBottomNavView();

    }

    //search function
    private void searchForMatch(String keyWord) {
        users.clear();
        if (keyWord.length() == 0) {

        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.db_name_users))
                    .orderByChild(getString(R.string.field_username))
                    .equalTo(keyWord);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        users.add(ds.getValue(User.class));
                    }
                    setupCommentAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //init text listener
    private void initTextListener() {
        users = new ArrayList<>();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = search.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(query);
            }
        });
    }

    private void setupCommentAdapter() {
        adapter = new UsersListAdapter(mContext, R.layout.layout_user_search, users);
        usersListView.setAdapter(adapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.user), users.get(i));
                startActivity(intent);
            }
        });
    }


    //setup bottm navigation View
    private void setupBottomNavView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
}
