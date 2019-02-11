package shamgar.org.peoplesfeedback.UI;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.MpTabListAdapter;
import shamgar.org.peoplesfeedback.Adapters.Politicians.ViewAllPoliticiansAdapter;
import shamgar.org.peoplesfeedback.Adapters.TabsAccessorAdaptor;
import shamgar.org.peoplesfeedback.R;

import static android.widget.LinearLayout.VERTICAL;

public class ViewAllPoliticiansActivity extends AppCompatActivity {

    private RecyclerView viewAllRecyclerView;
    private TabLayout tabLayout;
    private Toolbar inner1Toolbar;
    private TextView pol_inner_title;


    private ViewAllPoliticiansAdapter allPoliticiansAdapter;
    private String state;
    private ArrayList<String> districtList=new ArrayList<>();
    private ArrayList<String> MPList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_politicians);

        tabLayout= (TabLayout) findViewById(R.id.listTabs);
        inner1Toolbar= (Toolbar) findViewById(R.id.inner1Toolbar);
        pol_inner_title=  findViewById(R.id.pol_inner_title);

        state = getIntent().getStringExtra("state");
        setSupportActionBar(inner1Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        pol_inner_title.setText(state);



        Toast.makeText(getApplicationContext(),"state "+ state,Toast.LENGTH_SHORT).show();


        viewAllRecyclerView=(RecyclerView)findViewById(R.id.viewAllRcyclerView);
//        gettingMlaList=(Button) findViewById(R.id.gettingMlaList);
//        gettingMPList=(Button) findViewById(R.id.gettingMPList);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
//        allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),districtList, state);
        viewAllRecyclerView.setHasFixedSize(true);
        viewAllRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
//        viewAllRecyclerView.setAdapter(allPoliticiansAdapter);
        viewAllRecyclerView.addItemDecoration(decoration);

        getDistricts();

//        gettingMlaList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),districtList, state);
//                getDistricts();
//            }
//        });
//        gettingMPList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                allPoliticiansAdapter=new ViewAllPoliticiansAdapter(getApplicationContext(),MPList,state);
//                getDistrictsMpList();
//            }
//        });

        MpTabListAdapter adaptor=new MpTabListAdapter(getSupportFragmentManager());
        tabLayout.setTabsFromPagerAdapter(adaptor);

        TabLayout.Tab tab=tabLayout.getTabAt(0);
        tab.select();
        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#c2185b"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    getDistricts();
                }
                if (tab.getPosition()==1){
                    getDistrictsMpList();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
