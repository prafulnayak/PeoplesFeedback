package shamgar.org.peoplesfeedback.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shamgar.org.peoplesfeedback.Model.HomeModel;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.CameraActivity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {


    private TextView seekbarprogress;
    FloatingActionButton floatingActionButton;
    private RecyclerView home_rv;
    private SeekBar location_seekbar;

    DatabaseReference databaseReference;
    private SharedPreferenceConfig sharedPreference;
    private ArrayList<String> result;
    private Query query,query1,query2;
    private float distance;
    private ArrayList<String> ids;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreference = new SharedPreferenceConfig(getActivity());

        ids = new ArrayList<>();


        Log.e("home","Home Fragment");

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
               startActivity(new Intent(getActivity(),CameraActivity.class));

            }
        });

        location_seekbar.setMax(100);
        location_seekbar.setProgress(10);

        location_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged=progress;

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
                },2000);

                seekbarprogress.setVisibility(View.VISIBLE);
                seekbarprogress.setText("you have set range upto "+progressChanged+" km");

            }
        });

        intialposts();

//        postsfromOtherConstituencies();

//        populateUi();

        return view;
    }

    private void populateUi() {
        Toast.makeText(getActivity(),"data: "+ids.size(),Toast.LENGTH_SHORT).show();
        for (int i=0;i<ids.size();i++) {
            query = FirebaseDatabase.getInstance().getReference("Posts")
                    .orderByChild(ids.get(i));

            Toast.makeText(getActivity(), "dataSnapshot"+ids.get(i), Toast.LENGTH_LONG).show();
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d("data", dataSnapshot.toString());
//
////                    firebseUIhome();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        }
        firebseUIhome();
    }

    private void intialposts() {
        /*
            Query for getting all the posts from registered constituency
        */
        query2=FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("constituency")
                .equalTo("Visakhapatnam East");
        Log.e("east",": "+query2);

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                firebseUIhome();
                Toast.makeText(getActivity(),"count: "+dataSnapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();
//                ids = dataSnapshot.getChildren();
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ids.add(dataSnapshot1.getKey().toString());
//                    Toast.makeText(getActivity(),"key : "+dataSnapshot1.getKey().toString(),Toast.LENGTH_SHORT).show();
                }
                postsfromOtherConstituencies();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void postsfromOtherConstituencies()
    {
//         ids=new ArrayList<>();

         final float temp=15.0000f;


       query1=FirebaseDatabase.getInstance().getReference("Posts")
               .orderByChild("lat");

       query1.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()) {
                   for(DataSnapshot latLon : dataSnapshot.getChildren()) {

                       latLon.child("lat").getValue();

                       Location startPoint=new Location("locationA");

                       startPoint.setLatitude(16.99587);
                       startPoint.setLongitude(81.80142);

                       Location endPoint=new Location("locationA");

                       endPoint.setLatitude(Double.parseDouble(String.valueOf(latLon.child("lat").getValue())));
                       endPoint.setLongitude(Double.parseDouble(String.valueOf(latLon.child("lon").getValue())));

                       double dist=startPoint.distanceTo(endPoint);

                       distance = (float) (dist/1000);

                       Log.d("distance", String.valueOf(distance));

                       if(distance<temp) {
                           ids.add(latLon.getKey());
                       }

                   }

                    Toast.makeText(getActivity(),"id"+ids.size(),Toast.LENGTH_SHORT).show();

//                   for (int i=0;i<ids.size();i++) {
//                       query = FirebaseDatabase.getInstance().getReference("Posts")
//                               .orderByChild(ids.get(i));
//
//                       Toast.makeText(getActivity(), "dataSnapshot", Toast.LENGTH_LONG).show();
//                       query.addValueEventListener(new ValueEventListener() {
//                           @Override
//                           public void onDataChange(DataSnapshot dataSnapshot) {
//                               Log.d("data", dataSnapshot.toString());
//
//                               firebseUIhome();
//                           }
//
//                           @Override
//                           public void onCancelled(DatabaseError databaseError) {
//
//                           }
//                       });
//
//                   }
                   populateUi();

               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError)
           {

           }
       });



    }

    private void firebseUIhome() {
        FirebaseRecyclerAdapter<HomeModel,Homeviewholder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<HomeModel, Homeviewholder>(
                HomeModel.class,
                R.layout.newsfeed_layout,
                Homeviewholder.class,
                query)
        {
            @Override
            protected void populateViewHolder(Homeviewholder viewHolder, HomeModel model, int position) {
                viewHolder.setposts(getActivity(),
                                    model.getUserImage()
                                    ,model.getMlaImage()
                                    ,model.getUsername()
                                    ,model.getMla()
                                    ,model.getConstituency()
                                    ,model.getPublishedon()
                                    ,model.getTag()
                                    ,model.getDescription()
                                    ,model.getLat()
                                    ,model.getLon()
                                    ,model.getUrl());
            }
        };

        home_rv.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        home_rv.invalidate();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            Log.d("home","visible");
        }else{
            // fragment is not visible
        }
    }

    public static class Homeviewholder  extends RecyclerView.ViewHolder {

        public Homeviewholder(View itemView) {
            super(itemView);
        }

        public void setposts(Context context,String userImage,String mlaImage,String userName,String mlaName,String constituency,String time,String tagname,
                             String desc,double lat,double lon,String  postimage) {

            CircularImageView userimage=itemView.findViewById(R.id.postuserImage);
            CircularImageView mlaimage=itemView.findViewById(R.id.postmlaImage);
            TextView username=itemView.findViewById(R.id.postusername);
            TextView mlaname=itemView.findViewById(R.id.postmlaname);
            TextView mlaconstituency=itemView.findViewById(R.id.postmla_consti);
            TextView posttimestamp=itemView.findViewById(R.id.posttimestamp);
            TextView postTagname=itemView.findViewById(R.id.postTagname);
            TextView postImageDescription=itemView.findViewById(R.id.postImageDescription);
            TextView postmlarating_perce=itemView.findViewById(R.id.postmlarating_perce);
            TextView postlocation=itemView.findViewById(R.id.postlocation);
            TextView num_views=itemView.findViewById(R.id.num_views);
            TextView num_likes=itemView.findViewById(R.id.num_likes);
            TextView num_shares=itemView.findViewById(R.id.num_shares);
            ImageView userpostimage=itemView.findViewById(R.id.userpost);

            mlaconstituency.setText(String.format("Constituency: %s", constituency));
            postTagname.setText(String.format("@%s", tagname));
            posttimestamp.setText(time);
//            userpostimage.setImageBitmap(Bitmap.createBitmap(R.drawable.user));

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();
            Glide.with(context)
                    .load(postimage)
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.sai)
                    // read original from cache (if present) otherwise download it and decode it
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(userpostimage);

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            if (lat==0 || lon==0) {
                postlocation.setText("location unknown");
            }
            else
            postlocation.setText(addresses.get(0).getAddressLine(0));

        }
    }



}
