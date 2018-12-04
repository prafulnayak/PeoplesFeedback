package shamgar.org.peoplesfeedback.Fragments;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.PostsAdapter;
import shamgar.org.peoplesfeedback.Model.HomeModel;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.CameraActivity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainHome extends Fragment {

    private TextView seekbarprogress;
    FloatingActionButton floatingActionButton;
    private RecyclerView home_rv;
    private SeekBar location_seekbar;

    DatabaseReference databaseReference;
    private SharedPreferenceConfig sharedPreference;
    private ArrayList<String> result;
    private Query query, query2;
    private float distance;
    private ArrayList<String> ids;


    private ArrayList<String> url;
    private ArrayList<String> description;
    private ArrayList<String> tag;
    private ArrayList<String> mla;
    private ArrayList<String> lat;
    private ArrayList<String> lon;
    private ArrayList<String> postedby;
    private ArrayList<String> status;
    private ArrayList<String> publishedon;
    private ArrayList<String> constituency;
    private ArrayList<String> mlaid;
    private ArrayList<String> userImage;
    private ArrayList<String> mlaImage;
    private ArrayList<String> username;
    private ArrayList<String> rating;
    private ArrayList<String> latitudes;
    private ArrayList<String> longitudes;
    private ArrayList<String> nearbyconst;

    private PostsAdapter adapter;

    public MainHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        sharedPreference = new SharedPreferenceConfig(getActivity());
        latitudes = new ArrayList<>();
        nearbyconst = new ArrayList<String>();
        longitudes = new ArrayList<>();
        ids = new ArrayList<>();
        url = new ArrayList<>();
        description = new ArrayList<>();
        tag = new ArrayList<>();
        mla = new ArrayList<>();
        lat = new ArrayList<>();
        lon = new ArrayList<>();
        postedby = new ArrayList<>();
        status = new ArrayList<>();
        publishedon = new ArrayList<>();
        constituency = new ArrayList<>();
        mlaid = new ArrayList<>();
        userImage = new ArrayList<>();
        mlaImage = new ArrayList<>();
        username = new ArrayList<>();
        rating = new ArrayList<>();


        Log.e("home", "Home Fragment");

        floatingActionButton = view.findViewById(R.id.floating_action);
        location_seekbar = view.findViewById(R.id.locationseekBar);
        seekbarprogress = view.findViewById(R.id.seekbarprogress);
        home_rv = view.findViewById(R.id.home_rv);
        home_rv.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        home_rv.setLayoutManager(linearLayoutManager);
        home_rv.setNestedScrollingEnabled(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(home_rv.getContext(),
                linearLayoutManager.getOrientation());
        home_rv.addItemDecoration(dividerItemDecoration);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));

            }
        });

        location_seekbar.setMax(100);
        location_seekbar.setProgress(10);

        location_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        seekbarprogress.setVisibility(View.GONE);
                    }
                }, 2000);

                seekbarprogress.setVisibility(View.VISIBLE);
                seekbarprogress.setText("you have set range upto " + progressChanged + " km");

            }
        });

        intialposts();

        home_rv.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }


    private void intialposts() {
        /*
            Query for getting all the posts from registered constituency
        */
        ids.clear();
        query2 = FirebaseDatabase.getInstance().getReference("states/" + sharedPreference.readState() + "/" + sharedPreference.readDistrict() + "/con/" + sharedPreference.readConstituancy() + "/PostID");

        Log.e("east", ": " + query2);

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ids.add(dataSnapshot1.getKey());

                }

                populateUi();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void populateUi() {
        Toast.makeText(getActivity(), "data: " + ids, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < ids.size(); i++) {

            query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("id")
                    .equalTo(ids.get(i));


            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        url.add(String.valueOf(dataSnapshot1.child("url").getValue()));
                        description.add(String.valueOf(dataSnapshot1.child("description").getValue()));
                        tag.add(String.valueOf(dataSnapshot1.child("tag").getValue()));
                        mla.add(String.valueOf(dataSnapshot1.child("mla").getValue()));
                        lat.add(String.valueOf(dataSnapshot1.child("lat").getValue()));
                        lon.add(String.valueOf(dataSnapshot1.child("lon").getValue()));
                        postedby.add(String.valueOf(dataSnapshot1.child("postedby").getValue()));
                        status.add(String.valueOf(dataSnapshot1.child("status").getValue()));
                        publishedon.add(String.valueOf(dataSnapshot1.child("publishedon").getValue()));
                        constituency.add(String.valueOf(dataSnapshot1.child("constituency").getValue()));
                        mlaid.add(String.valueOf(dataSnapshot1.child("mlaid").getValue()));
                        userImage.add(String.valueOf(dataSnapshot1.child("userImage").getValue()));
                        mlaImage.add(String.valueOf(dataSnapshot1.child("mlaImage").getValue()));
                        username.add(String.valueOf(dataSnapshot1.child("username").getValue()));
                        rating.add(String.valueOf(dataSnapshot1.child("rating").getValue()));
                    }
                    Toast.makeText(getActivity(), "urls" + url, Toast.LENGTH_LONG).show();

                    adapter = new PostsAdapter(getActivity(), url, description, tag, mla, lat, lon, postedby, status, publishedon, constituency, mlaid, userImage, mlaImage, username, rating);
                    home_rv.setAdapter(adapter);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    private void gettingLatAndLon() {
        Query latquery = FirebaseDatabase.getInstance().getReference("states/" + sharedPreference.readState() + "/" + sharedPreference.readDistrict() + "/con");
        latquery.orderByChild("lat");
        latquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot lat : dataSnapshot.getChildren())
                    {
                        latitudes.add(lat.child("lat").getValue().toString());
                        longitudes.add(lat.child("lon").getValue().toString());
                        nearbyconst.add(lat.getKey().toString());
                    }

                    postsNEarby();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void postsNEarby()
    {
        final float temp = 15.0000f;
        Location startPoint = new Location("locationA");

        startPoint.setLatitude(17.737467);
        startPoint.setLongitude(83.321486);
        Location endPoint = new Location("locationA");

        for (int i = 0; i < latitudes.size(); i++) {
            endPoint.setLatitude(Double.parseDouble(String.valueOf(latitudes.get(i))));
            endPoint.setLongitude(Double.parseDouble(String.valueOf(longitudes.get(i))));

            double dist = startPoint.distanceTo(endPoint);

            distance = (float) (dist / 1000);

            Log.d("distance", String.valueOf(distance));

            if (distance<temp)
            {
//                query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("id")
//                        .equalTo(ids.get(i));
            }


        }


    }
}
