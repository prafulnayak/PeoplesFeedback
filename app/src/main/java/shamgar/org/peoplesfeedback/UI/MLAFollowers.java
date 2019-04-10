package shamgar.org.peoplesfeedback.UI;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.FollowersAdapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;

import static android.widget.LinearLayout.VERTICAL;

public class MLAFollowers extends AppCompatActivity {

    private RecyclerView MLAfollowersRecyclerView;
    private String mlaName,status;
    private FollowersAdapter followAdapter;
    private ArrayList<String> images;
    private ArrayList<String> names;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mlafollowers);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Followers");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
        getSupportActionBar().setElevation(0);

        MLAfollowersRecyclerView=findViewById(R.id.MLAfollowersRecyclerView);
        images=new ArrayList<>();
        names=new ArrayList<>();

        mlaName=getIntent().getExtras().getString("follower");
        status=getIntent().getExtras().getString("status");

        if (mlaName!=null && status.contains("1")){
            getFollowers("Politicians");
        }else if (mlaName!=null && status.contains("2")){
            getFollowers("Party");

        }


    }

    private void getFollowers(String politicians) {
        Query query= FirebaseDatabase.getInstance().getReference().child(politicians).child(mlaName);

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Followers")){
                        images.clear();
                        names.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.child("Followers").getChildren()){
                            Log.e("follower",dataSnapshot1.getKey());
                            gettingImagesAndNames(dataSnapshot1.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addValueEventListener(valueEventListener);
    }

    private void gettingImagesAndNames(String key) {
        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(key);

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("desc").getValue().toString()!=null && dataSnapshot.child("name").getValue().toString()!=null ) {
                        images.add(dataSnapshot.child("desc").getValue().toString());
                        names.add(dataSnapshot.child("name").getValue().toString());

                        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),VERTICAL);
                        followAdapter = new FollowersAdapter(getApplicationContext(), images, names);
                        MLAfollowersRecyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        MLAfollowersRecyclerView.setLayoutManager(layoutManager);
                        MLAfollowersRecyclerView.setAnimation(null);
                        MLAfollowersRecyclerView.addItemDecoration(decoration);
                        MLAfollowersRecyclerView.setAdapter(followAdapter);
                        MLAfollowersRecyclerView.setNestedScrollingEnabled(false);
                        followAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }
}
