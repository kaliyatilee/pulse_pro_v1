package com.algebratech.pulse_wellness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.adapters.LikeAdapter;
import com.algebratech.pulse_wellness.adapters.addFriendAdapter;
import com.algebratech.pulse_wellness.models.LikeModel;
import com.algebratech.pulse_wellness.models.addFriendModel;

import java.util.ArrayList;
import java.util.List;

public class AddFriendScreen extends AppCompatActivity {
    private Toolbar toolbarPolicy;
    private EditText searchFriend;
    private ImageView search_fri;
    private RecyclerView recycle_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_screen);
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        searchFriend = findViewById(R.id.search_friend);
        search_fri = findViewById(R.id.search_fri);
        recycle_friend = findViewById(R.id.recycle_friend);
        setSupportActionBar(toolbarPolicy);
        setTitle("Add Friend");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        search_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        addFriendModel[] addFriendModels = new  addFriendModel[]
                {
                        new addFriendModel("Karan Shah", R.drawable.placeholder),
                        new addFriendModel("Deep Amin", R.drawable.placeholder),
                        new addFriendModel("Jalpa Panchal", R.drawable.placeholder),
                        new addFriendModel("Sohan Vadhavaniya", R.drawable.placeholder),
                        new addFriendModel("Vandan Raval", R.drawable.placeholder),
                };

        RecyclerView recyclerView = findViewById(R.id.recycle_friend);
        addFriendAdapter adapter = new addFriendAdapter(addFriendModels,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


//        DividerItemDecoration itemDecoration =
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.layer));
//
//        recyclerView.addItemDecoration(itemDecoration);

        searchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence == null && charSequence.toString() == "")
                {
                    recycle_friend.setVisibility(View.GONE);
                }
                else
                    recycle_friend.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
}

