package shamgar.org.peoplesfeedback.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import shamgar.org.peoplesfeedback.Adapters.HomeAdapter;
import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.Posts;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.CameraActivity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static shamgar.org.peoplesfeedback.ConstantName.NamesC.CONSTITUANCY;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.INDIA;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.POSTIDCON;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.POSTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {


    FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private SharedPreferenceConfig sharedPreference;
    HomeAdapter adapter;
    static List<String> list = new ArrayList<>();
    static ArrayList<News> newsList = new ArrayList<>();
    static ArrayList<String> nearbyConstuencies = new ArrayList<String>();
    LinearLayoutManager layoutManager;
    private SeekBar seekBar;
    private Switch switch1;
    private String switchStatus;
    private int seekbarPosition;
    private Parcelable recyclerViewState;
    int distance = 0;
    View view;
    private String bookskey;


    public Home() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sharedPreference = new SharedPreferenceConfig(getActivity());
//        getNewsKeyFromConstituancy();
//        getNewsDetailsFromPost();
//        new AsyncTaskForNews().execute();
//        new AsyncTaskForNews().execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreference = new SharedPreferenceConfig(getActivity());


        floatingActionButton = view.findViewById(R.id.floating_action);
        seekBar = view.findViewById(R.id.seekBar);
        switch1 = view.findViewById(R.id.switch1);

        Log.i("Home", " Home Fragment");
        Toast.makeText(getActivity(), "home", Toast.LENGTH_LONG).show();

        recyclerView = view.findViewById(R.id.home_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        adapter = new HomeAdapter(newsList, getActivity());
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(linearLayoutManager);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));

            }
        });

        //checking switch is on or off
        checkinSwtichStatus();
        getNewsKeyFromConstituancy();
        Toast.makeText(getActivity(), "home"+newsList.size(), Toast.LENGTH_LONG).show();

    }

    private void getNewsKeyFromConstituancy() {
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .child(POSTIDCON).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //if key exist no need to add or else add to the list
                    if(!list.contains(snapshot.getKey())){
                        list.add(snapshot.getKey());

                        Log.e("log: ","key in: "+dataSnapshot.getKey());

                    }
                }
                Collections.reverse(list);
                getNewsDetailsFromPost();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void getNewsDetailsFromPost()
    {

        for (int i=0; i<list.size();i++){
            final int p =i;
           FirebaseDatabase.getInstance().getReference().child(POSTS)
                    .child(list.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Log.e("Post",""+dataSnapshot);
                    if (dataSnapshot.exists())
                    {
                        Posts posts = dataSnapshot.getValue(Posts.class);
                        int likes =Integer.parseInt(String.valueOf(dataSnapshot.child("Likes").getChildrenCount()));
                        int share = Integer.parseInt(String.valueOf(dataSnapshot.child("Share").getChildrenCount()));
                        addOrUpdateNewsList(posts,dataSnapshot.getKey(), likes,share);
                      //  Log.e("Post",""+posts.getImageUrl());
                        Log.e("Post data snap key",""+likes+share);
                        Log.e("Post list ",""+list.get(p));
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Postaaa",""+databaseError.toString());
                }
            });
        }

    }

    private void addOrUpdateNewsList(Posts posts, String key, int likes, int share) {

        News news = new News(
                key,
                sharedPreference.readPhoneNo(),"url",posts.getHeading(),
                posts.getDescription(),sharedPreference.readConstituancy(),
                posts.getImageUrl(),Double.parseDouble(posts.getLatitude()),
                Double.parseDouble(posts.getLongitude()),posts.getAddress(),
                "mla","malImageUrl","100",
                posts.getTagId(),posts.getView(),likes,share,posts.getPostedOn(),1);
        Log.e("on Update","hhhhhh");
//        if(newsList.size()>0){
            Boolean isNewNews = true;
            for(int i=0;i<newsList.size();i++){
                if(newsList.get(i).getPostId().equals(key)){
                    Log.e("Postaaa",""+newsList.get(i).getPostId()+""+key);
                    updateNewsListItem(i,news);
                    isNewNews = false;
                }
            }
            if(isNewNews){

                newsList.add(news);
                recyclerView = view.findViewById(R.id.home_rv);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new HomeAdapter(newsList, getActivity());

                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }


//        }else
//            newsList.add(news);
//        adapter = new HomeAdapter(newsList, getActivity());
//        recyclerView.setAdapter(adapter);




    }

    private void updateNewsListItem(int i, News news)
    {
        newsList.get(i).setAll(news);

        adapter.notifyItemChanged(i);

    }

    @Override
    public void onResume() {
        super.onResume();
//        adapter.notifyDataSetChanged();
        recyclerView = view.findViewById(R.id.home_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        adapter = new HomeAdapter(newsList, getActivity());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();

//        new AsyncTaskForNews().execute();


    }

    private void checkinSwtichStatus()
    {

        seekBar.setEnabled(false);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked==true)
                {
                    switch1.setText("Off");
                    seekBar.setEnabled(true);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                        {
                            seekbarPosition =seekBar.getProgress();
                            gettingNearbyPosts(seekbarPosition);
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar)
                        {

                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar)
                        {

                        }
                    });
                }
                else
                {
                    switchStatus = switch1.getTextOff().toString();
                    seekBar.setEnabled(false);
                    switch1.setText("On");
                }
            }
        });

    }

    private void gettingNearbyPosts(int seekbarPosition)
    {

       int temp=0;

        if (seekbarPosition<5)
            temp=5;
        else if (seekbarPosition>5 && seekbarPosition<10)
            temp=10;
        else if(seekbarPosition>10 && seekbarPosition<15)
            temp=15;
        else
            temp=20;


        final int finalTemp = temp;
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child("East Godavari")
                .child(CONSTITUANCY)
                .child("Rajahmundry Urban")
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String lat =  dataSnapshot.child("latitude").getValue().toString();
                            final String  lon =  dataSnapshot.child("longitude").getValue().toString();

                            FirebaseDatabase.getInstance().getReference().child(INDIA)
                                    .child(sharedPreference.readState())
                                    .child("East Godavari")
                                    .child(CONSTITUANCY)
                                    .addValueEventListener(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.exists())
                                            {
                                                for (DataSnapshot constituences:dataSnapshot.getChildren())
                                                {
                                                    String nearLat=constituences.child("latitude").getValue().toString();
                                                    String nearLon=constituences.child("longitude").getValue().toString();

                                                    Location startPoint=new Location("locationA");
                                                    startPoint.setLatitude(Double.parseDouble(lat));
                                                    startPoint.setLongitude(Double.parseDouble(lon));

                                                    Location endPoint=new Location("locationA");
                                                    endPoint.setLatitude(Double.parseDouble(nearLat));
                                                    endPoint.setLongitude(Double.parseDouble(nearLon));

                                                    double dist=startPoint.distanceTo(endPoint);
                                                    distance = (int)(dist/1000);

                                                    Log.d("distance", String.valueOf(distance));

                                                    if(finalTemp > distance)
                                                    {
                                                        if (!nearbyConstuencies.contains(constituences.getKey()))
                                                        {
                                                            nearbyConstuencies.add(constituences.getKey());
                                                        }
                                                        Log.e("log: ","constituences in: "+nearbyConstuencies);

                                                            for (int i=0;i<nearbyConstuencies.size();i++)
                                                            {
                                                               FirebaseDatabase.getInstance().getReference().child(INDIA)
                                                                        .child(sharedPreference.readState())
                                                                        .child("East Godavari")
                                                                        .child(CONSTITUANCY)
                                                                        .child(nearbyConstuencies.get(i)).child("PostID")
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot)
                                                                            {
                                                                                for (DataSnapshot newlist:dataSnapshot.getChildren())
                                                                                {
                                                                                    if(!list.contains(newlist.getKey())) {
                                                                                        list.add(newlist.getKey());
                                                                                        Log.e("log: ","key in: "+newlist.getKey());
                                                                                    }

                                                                                }
                                                                                getNewsDetailsFromPost();

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError)
                                                                            {

                                                                            }
                                                                        });
                                                        }

                                                    }


                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });



//       Query query1=FirebaseDatabase.getInstance().getReference("Posts")
//                .orderByChild("lat");
//
//
//        final int finalTemp = temp;
//        query1.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                if (dataSnapshot.exists())
//                {
//                    for(DataSnapshot latLon : dataSnapshot.getChildren())
//                    {
//
//                        latLon.child("lat").getValue();
//
//                        Location startPoint=new Location("locationA");
//                        startPoint.setLatitude(Double.parseDouble(String.valueOf(lat)));
//                        startPoint.setLongitude(Double.parseDouble(String.valueOf(lon)));
//
//                        Location endPoint=new Location("locationA");
//                        endPoint.setLatitude(Double.parseDouble(String.valueOf(latLon.child("lat").getValue())));
//                        endPoint.setLongitude(Double.parseDouble(String.valueOf(latLon.child("lon").getValue())));
//
//                        double dist=startPoint.distanceTo(endPoint);
//
//
//
//                        distance[0] = (float) (dist/1000);
//
//                        Log.d("distance", String.valueOf(distance[0]));
//
//                        if(distance[0] < finalTemp)
//                        {
//                           // ids.add(latLon.getKey());
//
//                        }
//
//
//                    }
//
//
//
////                    for (int i=0;i<ids.size();i++) {
////                        query = FirebaseDatabase.getInstance().getReference("Posts")
////                                .orderByChild(ids.get(i));
////
////                        Toast.makeText(getActivity(), "dataSnapshot", Toast.LENGTH_LONG).show();
////                        query.addValueEventListener(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                Log.d("data", dataSnapshot.toString());
////
////                                firebseUIhome();
////                            }
////
////                            @Override
////                            public void onCancelled(DatabaseError databaseError) {
////
////                            }
////                        });
////
////                    }
//
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError)
//            {
//
//            }
//        });





    }



}