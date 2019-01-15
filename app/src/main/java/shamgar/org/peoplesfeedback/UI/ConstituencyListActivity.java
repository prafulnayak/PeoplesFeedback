package shamgar.org.peoplesfeedback.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

import static android.widget.LinearLayout.VERTICAL;

public class ConstituencyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VerticalPoliticianAdapter adapter;

    private ArrayList<PartyStateMla> stateMajorParty = new ArrayList<>();
    private ArrayList<PartyStateMla> stateMajorPartyHead = new ArrayList<>();
    private ArrayList<ArrayList<PartyStateMla>> masterPartyStateMlas = new ArrayList<ArrayList<PartyStateMla>>();
//    private ArrayList<PartyStateMla> districtPoliticians = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency_list);

        String state = getIntent().getStringExtra("state");


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=(RecyclerView)findViewById(R.id.vertical_politicians_sub_list);
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
                        singleSnapParty.setHeading("State Party");
                        stateMajorParty.add(singleSnapParty);

                        PartyStateMla singleSnapPartyHead = new PartyStateMla();
                        Log.e("child",snapshot.getValue().toString());
                        singleSnapPartyHead.setName(snapshot.getValue().toString());
                        singleSnapParty.setHeading("State Party Head");
                        stateMajorPartyHead.add(singleSnapPartyHead);
                    }

                    masterPartyStateMlas.add(stateMajorParty);
                    masterPartyStateMlas.add(stateMajorPartyHead);
                    getStateMlaList(state);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        postQuery.addValueEventListener(valueEventListener);
    }

    private void getStateMlaList(String state) {
        Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                ArrayList<PartyStateMla> districtPoliticians = new ArrayList<>();

                if(snapshot.hasChild("Constituancy")){
                    DataSnapshot snap = snapshot.child("Constituancy");
                    for(DataSnapshot dataSnapshotP: snap.getChildren()){
                        PartyStateMla singleSnapPoliticians = new PartyStateMla();

                        singleSnapPoliticians.setHeading(snapshot.getKey());
                        singleSnapPoliticians.setName(dataSnapshotP.child("mla_name").getValue(String.class));

                        Log.e("pol child",singleSnapPoliticians.getHeading());
                        Log.e("pol child",dataSnapshotP.child("mla_name").getValue(String.class));

                        districtPoliticians.add(singleSnapPoliticians);

                    }
                    masterPartyStateMlas.add(districtPoliticians);
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

//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    ArrayList<PartyStateMla> districtPoliticians = new ArrayList<>();
//
//
//                    if(snapshot.hasChild("Constituancy")){
//                        DataSnapshot snap = snapshot.child("Constituancy");
//                        for(DataSnapshot dataSnapshotP: snap.getChildren()){
//                            PartyStateMla singleSnapPoliticians = new PartyStateMla();
//
//                            singleSnapPoliticians.setHeading(snapshot.getKey());
//                            singleSnapPoliticians.setName(dataSnapshotP.child("mla_name").getValue(String.class));
//
//                            Log.e("pol child",singleSnapPoliticians.getHeading());
//                            Log.e("pol child",dataSnapshotP.child("mla_name").getValue(String.class));
//
//                            districtPoliticians.add(singleSnapPoliticians);
//
//                        }
//                        masterPartyStateMlas.add(districtPoliticians);
//                        adapter.notifyDataSetChanged();
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        postQuery.addValueEventListener(valueEventListener);
        postQuery.addChildEventListener(childEventListener);
        adapter.notifyDataSetChanged();
    }

}
