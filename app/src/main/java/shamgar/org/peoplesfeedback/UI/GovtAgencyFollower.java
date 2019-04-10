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

public class GovtAgencyFollower extends AppCompatActivity {
    private RecyclerView TagFollowersRecyclerView;
    private FollowersAdapter followAdapter;
    private ArrayList<String> images;
    private ArrayList<String> names;
    private LinearLayoutManager layoutManager;
    private String district,tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_govt_agency_follower);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Followers");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
        getSupportActionBar().setElevation(0);

        TagFollowersRecyclerView=findViewById(R.id.TagFollowersRecyclerView);
        images=new ArrayList<>();
        names=new ArrayList<>();

        district=getIntent().getExtras().getString("district");
        tag=getIntent().getExtras().getString("tag");

        if (district!=null && tag!=null){
            getFollowers();
        }

    }

    private void getFollowers() {
        Query query= FirebaseDatabase.getInstance().getReference().child("District").child(district).child(tag);
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Followers")){
                        images.clear();
                        names.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.child("Followers").getChildren()){
                            Log.e("tagFollower",dataSnapshot1.getKey());
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
                    if (dataSnapshot.child("desc").getValue().toString()!=null && dataSnapshot.child("name").getValue().toString()!=null ){
                        images.add(dataSnapshot.child("desc").getValue().toString());
                        names.add(dataSnapshot.child("name").getValue().toString());

                        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),VERTICAL);
                        followAdapter=new FollowersAdapter(getApplicationContext(),images,names);
                        TagFollowersRecyclerView.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        TagFollowersRecyclerView.setLayoutManager(layoutManager);
                        TagFollowersRecyclerView.setAnimation(null);
                        TagFollowersRecyclerView.addItemDecoration(decoration);
                        TagFollowersRecyclerView.setAdapter(followAdapter);
                        TagFollowersRecyclerView.setNestedScrollingEnabled(false);
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
