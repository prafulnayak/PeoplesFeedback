package shamgar.org.peoplesfeedback.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.Politicians.ViewAllPoliticiansAdapter;
import shamgar.org.peoplesfeedback.R;

import static android.widget.LinearLayout.VERTICAL;

public class ViewAllPoliticiansActivity extends AppCompatActivity {

    private RecyclerView viewAllRecyclerView;
    private Button gettingMPList,gettingMlaList;

    private ViewAllPoliticiansAdapter allPoliticiansAdapter;
    private String state;
    private ArrayList<String> districtList=new ArrayList<>();
    private ArrayList<String> MPList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_politicians);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

         state = getIntent().getStringExtra("state");

        Toast.makeText(getApplicationContext(),"state "+ state,Toast.LENGTH_SHORT).show();


        viewAllRecyclerView=(RecyclerView)findViewById(R.id.viewAllRcyclerView);
        gettingMlaList=(Button) findViewById(R.id.gettingMlaList);
        gettingMPList=(Button) findViewById(R.id.gettingMPList);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
//        allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),districtList, state);
        viewAllRecyclerView.setHasFixedSize(true);
        viewAllRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
//        viewAllRecyclerView.setAdapter(allPoliticiansAdapter);
        viewAllRecyclerView.addItemDecoration(decoration);

        getDistricts();

        gettingMlaList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),districtList, state);
                getDistricts();
            }
        });
        gettingMPList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),MPList,state);
                getDistrictsMpList();
            }
        });

    }

    private void getDistrictsMpList()
    {
        MPList.clear();
        Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MP");


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(MPList.size() == 0){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Log.e("Mps", ""+snapshot.getKey());

//                    if(stateCm.getCM() != null){
                        MPList.add(snapshot.getKey().toString());
//                    }
//                    StateCm stateDetails = new StateCm(snapshot.getKey(),)
                        Log.e("districtsList", ""+MPList);
//                    DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
                        allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),MPList,state);
//                    viewAllRecyclerView.setHasFixedSize(true);
//                    viewAllRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        viewAllRecyclerView.setAdapter(allPoliticiansAdapter);
//                    viewAllRecyclerView.addItemDecoration(decoration);
                        allPoliticiansAdapter.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addValueEventListener(valueEventListener);

    }


    private void getDistricts() {
        districtList.clear();
        Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district");


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(districtList.size() == 0){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Log.e("districts", ""+snapshot.getKey());

//                    if(stateCm.getCM() != null){
                        districtList.add(snapshot.getKey().toString());
//                    }
//                    StateCm stateDetails = new StateCm(snapshot.getKey(),)
                        Log.e("districtsList", ""+districtList);
//                    DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
                        allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),districtList, state);
//                    viewAllRecyclerView.setHasFixedSize(true);
//                    viewAllRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        viewAllRecyclerView.setAdapter(allPoliticiansAdapter);
//                    viewAllRecyclerView.addItemDecoration(decoration);
                        allPoliticiansAdapter.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addValueEventListener(valueEventListener);

    }
}
