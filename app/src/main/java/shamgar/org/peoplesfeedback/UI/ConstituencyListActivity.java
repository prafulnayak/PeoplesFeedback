package shamgar.org.peoplesfeedback.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.Politicians.VerticalPoliticianAdapter;
import shamgar.org.peoplesfeedback.Model.PartyStateMla;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.widget.LinearLayout.VERTICAL;

public class ConstituencyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VerticalPoliticianAdapter adapter;
    private  Toolbar innerToolbar;
    private TextView pol_title;

    private ArrayList<PartyStateMla> stateMajorParty = new ArrayList<>();
    private ArrayList<PartyStateMla> stateMajorPartyHead = new ArrayList<>();
    private ArrayList<ArrayList<PartyStateMla>> masterPartyStateMlas = new ArrayList<ArrayList<PartyStateMla>>();

    SharedPreferenceConfig sharedPreferenceConfig;

    ArrayList<PartyStateMla> districtPoliticians = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency_list);

        String state = getIntent().getStringExtra("state");
        sharedPreferenceConfig = new SharedPreferenceConfig(this);
        recyclerView=(RecyclerView)findViewById(R.id.vertical_politicians_sub_list);
        innerToolbar=(Toolbar)findViewById(R.id.innerToolbar);
        pol_title=findViewById(R.id.pol_title);

        setSupportActionBar(innerToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        pol_title.setText(state);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),VERTICAL);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        adapter = new VerticalPoliticianAdapter(getApplicationContext(),masterPartyStateMlas,state);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);

        Toast.makeText(this, ""+state, Toast.LENGTH_SHORT).show();
        getPartyAndHeadDetails(state);
    }

    private void getPartyAndHeadDetails(final String state) {
        Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("Party");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(masterPartyStateMlas.size()== 0){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        PartyStateMla singleSnapParty = new PartyStateMla();
                        singleSnapParty.setName(snapshot.getKey());
                       // singleSnapParty.setRating("");
                        singleSnapParty.setHeading("State Party");
                        singleSnapParty.setImageUrl("http://sairaa.org/peopleFeedback/images/");
                        stateMajorParty.add(singleSnapParty);

                        PartyStateMla singleSnapPartyHead = new PartyStateMla();
                       // Log.e("child",snapshot.getValue().toString());
                        singleSnapPartyHead.setName(snapshot.getValue().toString());
                        singleSnapPartyHead.setImageUrl("http://sairaa.org/peopleFeedback/images/");
                        singleSnapPartyHead.setHeading(snapshot.getKey());
                       // singleSnapPartyHead.setRating("");
                        singleSnapParty.setHeading("State Party Head");
                        stateMajorPartyHead.add(singleSnapPartyHead);
                    }

                    masterPartyStateMlas.add(stateMajorParty);
                    masterPartyStateMlas.add(stateMajorPartyHead);
//                    getStateMlaList(state);
                    getOwnConstituancyMlaList(state);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        postQuery.addValueEventListener(valueEventListener);
    }

    private void getOwnConstituancyMlaList(String state){
        Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(sharedPreferenceConfig.readDistrict()).child("Constituancy");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){

//                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        PartyStateMla singleSnapPoliticians = new PartyStateMla();

                        singleSnapPoliticians.setHeading(sharedPreferenceConfig.readDistrict());
                        singleSnapPoliticians.setName(dataSnapshot.child("mla_name").getValue(String.class));
                        singleSnapPoliticians.setRating(String.valueOf(dataSnapshot.child("rating").getValue(Integer.class)));
                        singleSnapPoliticians.setImageUrl(dataSnapshot.child("mla_image").getValue().toString());

                       // Log.e("pol child",singleSnapPoliticians.getHeading());
                      //  Log.e("pol child",dataSnapshot.child("mla_name").getValue(String.class));

                        districtPoliticians.add(singleSnapPoliticians);
                      //  Log.e("con2",dataSnapshot.getKey());
//                    }
                    if(districtPoliticians.size()==1){
                        masterPartyStateMlas.add(districtPoliticians);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

      postQuery.addChildEventListener(childEventListener);
    }

}
