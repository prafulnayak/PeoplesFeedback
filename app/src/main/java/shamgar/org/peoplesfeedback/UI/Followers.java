package shamgar.org.peoplesfeedback.UI;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class Followers extends AppCompatActivity {

    private String number;
    private RecyclerView people_follow_recyclerview;
    private FollowersAdapter followAdapter;
    private ArrayList<String> images;
    private ArrayList<String> names;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        people_follow_recyclerview=findViewById(R.id.followersRecyclerView);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Followers");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
        getSupportActionBar().setElevation(0);

        number=getIntent().getExtras().get("number").toString();

        images=new ArrayList<>();
        names=new ArrayList<>();

        Log.e("follower",number);

        gettingImagesAndNames();
    }

    private void gettingImagesAndNames() {
        Query query= FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(number.substring(3));
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("followers")){
                        images.clear();
                        names.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.child("followers").getChildren()){
                            Log.e("names",dataSnapshot1.getKey());
                            imagesUrls(dataSnapshot1.getKey());
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

    private void imagesUrls(String key) {
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
                        people_follow_recyclerview.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        people_follow_recyclerview.setLayoutManager(layoutManager);
                        people_follow_recyclerview.setAnimation(null);
                        people_follow_recyclerview.addItemDecoration(decoration);
                        people_follow_recyclerview.setAdapter(followAdapter);
                        people_follow_recyclerview.setNestedScrollingEnabled(false);
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
