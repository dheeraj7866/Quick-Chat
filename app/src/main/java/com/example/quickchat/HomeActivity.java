package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    TextView profileName;
    CircleImageView profileImage;
   // Button noBtn,yesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        profileName=findViewById(R.id.profile_name);
        profileImage=findViewById(R.id.profile_image_home);

        DatabaseReference reference= database.getReference().child("user");

        usersArrayList=new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (!users.getUid().equals(auth.getUid())) {
                        usersArrayList.add(users);//here we keep the user outside the userList, because he can't send the message to himself.
                    } else {
                        profileName.setText(users.getName());
                        Picasso.get().load(users.imageUri).into(profileImage);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new GridLayoutManager(this,1));

        adapter=new UserAdapter(HomeActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

//        noBtn=findViewById(R.id.noBtn);
//        yesBtn=findViewById(R.id.yesBtn);

//        yesBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
//            }
//        });
//
//        noBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//   //             dialog.dismiss();
//            }
//        });

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.logout){
//            Dialog dialog = new Dialog(this);
//            dialog.setContentView(R.layout.dialouge_layout);
//            dialog.show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this,RegistrationActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}