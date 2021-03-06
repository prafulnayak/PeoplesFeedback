package shamgar.org.peoplesfeedback.Fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shamgar.org.peoplesfeedback.Adapters.HomeAdapter;
import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.Posts;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Services.BackGroundServices;
import shamgar.org.peoplesfeedback.UI.CameraActivity;
import shamgar.org.peoplesfeedback.UI.HomeScreenActivity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.CONSTITUANCY;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.INDIA;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.Likes;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.POSTIDCON;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.POSTS;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.Share;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.viewC;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private static int jobId = 1;
    FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private SharedPreferenceConfig sharedPreference;
    HomeAdapter adapter;
    static List<String> list = new ArrayList<>();
    static ArrayList<News> newsList = new ArrayList<>();
    static ArrayList<String> nearbyConstuencies = new ArrayList<String>();
    static boolean top = true;
    private List<String> list5 = new ArrayList<>();
    LinearLayoutManager layoutManager;
    private BubbleSeekBar seekBar;
    private Switch switch1;
    private TextView displayLocationRange,set_DisTance_Range,locRanTxt;
    private String switchStatus;
    private int seekbarPosition;
    private Parcelable recyclerViewState;
    int distance = 0;
    View view;
    private String bookskey;
    private static int displayedposition = 0;
    private static String lastKey = "";
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RelativeLayout localitylayout;

    private JobScheduler jobScheduler;


    public Home() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false);
       // Log.i("Home", " onCreateView");
       // Toast.makeText(getActivity(), "onCreateView" + newsList.size(), Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
        }

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
      //  Log.i("Home", " onCreate");
//        Toast.makeText(getActivity(), "onCreate" + newsList.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreference = new SharedPreferenceConfig(getActivity());
        floatingActionButton = view.findViewById(R.id.floating_action);
        seekBar = view.findViewById(R.id.seekBar);
        switch1 = view.findViewById(R.id.switch1);
        set_DisTance_Range = view.findViewById(R.id.set_DisTance_Range);
        displayLocationRange = view.findViewById(R.id.displayLocationRange);
        locRanTxt = view.findViewById(R.id.locRanTxt);
        localitylayout = view.findViewById(R.id.localitylayout);

     //   Log.i("Home", " onViewCreated");
//        Toast.makeText(getActivity(), "onViewCreated", Toast.LENGTH_LONG).show();

//        recyclerView = view.findViewById(R.id.home_rv);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAnimation(null);
//        adapter = new HomeAdapter(newsList, getActivity());

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
        if(list.size() == 0)
            getPosts();
//        getNewsKeyFromConstituancy();
//        Toast.makeText(getActivity(), "onViewCreated" + newsList.size(), Toast.LENGTH_LONG).show();
    }

    private void getPosts() {
        Query postQuery = FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .child(POSTIDCON).orderByKey().limitToLast(1);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list5.clear();
                list.clear();
                if(!list.contains(dataSnapshot.getKey())){
                    list5.add(dataSnapshot.getKey());
                    lastKey = dataSnapshot.getKey();
                    list.add(0,dataSnapshot.getKey());
                    getNewsDetailsFromPost(true);
                  //  Log.e("log: ","key in: "+dataSnapshot.getKey());
                  //  Log.e("log: ","key in s: "+s);
                }
//                if(list5.size() == 5){
//                    lastKey = list5.get(0);
//                    Collections.reverse(list5);
//                    list.addAll(list5);
//                    Log.e("log: ","last key: "+dataSnapshot.getKey());
//                    Log.e("log: ","key in: "+list.get(list.size()-5));
//                    getNewsDetailsFromPost();
////                    getMorePosts();
//                }
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

    private void getMorePosts() {
        Query postQuery = FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .child(POSTIDCON).orderByKey().endAt(lastKey).limitToLast(5);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(!list5.contains(dataSnapshot.getKey())){
                    list5.add(dataSnapshot.getKey());
                    //post view count
                    postViewsCount(dataSnapshot.getKey());
                   // Log.e("log: ","key in: "+dataSnapshot.getKey());
                   // Log.e("log: ","key in s: "+s);
                }
                if(list5.size() == 5){
                    lastKey = list5.get(0);
                    Collections.reverse(list5);
                    list.addAll(list5);
                  //  Log.e("log: ","last key: "+dataSnapshot.getKey());
                  //  Log.e("log: ","key in: "+list.get(list.size()-5));
//                    getMorePosts();
//                    list5.clear();
                    getNewsDetailsFromPost(false);
                    loading=true;
                }
                if(lastKey.equalsIgnoreCase(dataSnapshot.getKey())){
                    lastKey = list5.get(0);
                    Collections.reverse(list5);
                    list.addAll(list5);
                  //  Log.e("log: ","last key: "+dataSnapshot.getKey());
//                    Log.e("log: ","key in: "+list.get(list.size()-5));
//                    getMorePosts();
//                    list5.clear();
                    getNewsDetailsFromPost(false);
                    loading=false;
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
//        postQuery.addValueEventListener(valueEventListener);
    }

    private void postViewsCount(String key) {
        String postKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob(key,sharedPreference.readPhoneNo(),viewC);
        }else {
            FirebaseDatabase.getInstance().getReference().child("Posts")
                    .child(key)
                    .child("View")
                    .child(postKey).setValue(sharedPreference.readPhoneNo())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e("View ","posted");
                        }
                    });
        }

    }

    private void getNewsKeyFromConstituancy() {
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .child(POSTIDCON).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!list.contains(dataSnapshot.getKey())){
                    list.add(dataSnapshot.getKey());

                  //  Log.e("log: ","key in: "+dataSnapshot.getKey());
                   // Log.e("log: ","key in s: "+s);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

              //  Log.e("log: ","changed key: "+dataSnapshot.getKey());
              //  Log.e("log: ","changed key s: "+s);

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
        });
//                addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    //if key exist no need to add or else add to the list
//                    if(!list.contains(snapshot.getKey())){
//                        list.add(snapshot.getKey());
//
//                        Log.e("log: ","key in: "+dataSnapshot.getKey());
//
//                    }
//                }
//
//                getNewsDetailsFromPost();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



    }

    private void getNewsDetailsFromPost(final boolean top)
    {
        for (int i=0;i<list5.size();i++){
            FirebaseDatabase.getInstance().getReference().child(POSTS)
                    .child(list5.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                   // Log.e("Post",""+dataSnapshot);
                    if (dataSnapshot.exists())
                    {
                        Posts posts = dataSnapshot.getValue(Posts.class);

                        int likes =Integer.parseInt(String.valueOf(dataSnapshot.child("Likes").getChildrenCount()));
                        int share = Integer.parseInt(String.valueOf(dataSnapshot.child("Share").getChildrenCount()));
                        int viewCount = Integer.parseInt(String.valueOf(dataSnapshot.child("View").getChildrenCount()));
//                        getUserPhotoUrl(posts,dataSnapshot.getKey(), likes,share,viewCount,top);
                        addOrUpdateNewsList(posts,dataSnapshot.getKey(), likes,share,viewCount,top);
                        //  Log.e("Post",""+posts.getImageUrl());
                     //   Log.e("Post data snap key",""+likes+share);
                        //  Log.e("Post list ",""+list.get(p));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Postaaa",""+databaseError.toString());
                }
            });
        }
        list5.clear();

    }


    private void setClickListnerFromFirebase(final News news) {

        DatabaseReference dbRefLike = FirebaseDatabase.getInstance().getReference().child("Posts");

        dbRefLike.child(news.getPostId()).child(Likes).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  Log.e("child count like",""+dataSnapshot.getChildrenCount());
                int likes = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                if(news.getLikes() < likes){
                    news.setLikes(likes);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRefLike.child(news.getPostId()).child(Share).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  Log.e("child count share",""+dataSnapshot.getChildrenCount());

                int shares = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                if(news.getShares() < shares){
                    news.setShares(shares);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRefLike.child(news.getPostId()).child(viewC).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  Log.e("child count view",""+dataSnapshot.getChildrenCount());

                int views = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                if(news.getViews() < views){
                    news.setViews(views);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getUserPhotoUrl(String user) {
        FirebaseDatabase.getInstance().getReference().child("people").child(user.substring(3))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String url = dataSnapshot.child("desc").getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addOrUpdateNewsList(Posts posts, String key, int likes, int share, int viewCount, boolean top) {
//        getUserPhotoUrl(posts.getUser());
        News news = new News(
                key,
                posts.getUser(),"Unknown Name","url",posts.getHeading(),
                posts.getDescription(),posts.getConstituancy(),
                posts.getImageUrl(),
                Double.parseDouble(posts.getLatitude()),
                Double.parseDouble(posts.getLongitude()),posts.getAddress(),
                "mla","malImageUrl","100",
                posts.getTagId(),viewCount,likes,share,posts.getPostedOn(),1,posts.getUser(),posts.getState(),posts.getDistrict());



        getUserUrl(news,key,top,posts.getState(),posts.getDistrict(),posts.getConstituancy());

      //  Log.e("on Update","hhhhhh");
//        if(newsList.size()>0){



//        }else
//            newsList.add(news);
//        adapter = new HomeAdapter(newsList, getActivity());
//        recyclerView.setAdapter(adapter);

//            loading = true;


    }

    private void getUserUrl(final News news, final String key, final boolean top, final String state, final String district, final String constituancy) {
        String url;
        if(news.getPostedBy().length() == 13){
            FirebaseDatabase.getInstance().getReference().child("people").child(news.getPostedBy().substring(3))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url = dataSnapshot.child("desc").getValue(String.class);
                            String name = dataSnapshot.child("name").getValue(String.class);
                            if(url != null){
                                news.setUserUrl(url);
                            }
                            if(name != null){
                                news.setName(name);
                            }
                            retrieveMlaNameAndImage(news,key,top,state,district,constituancy);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            retrieveMlaNameAndImage(news,key,top,state,district,constituancy);
                        }
                    });
        }else {
            retrieveMlaNameAndImage(news,key,top,state,district,constituancy);
        }


    }

    private void retrieveMlaNameAndImage(final News news, final String key, final boolean top, String state, String district, String constituancy) {
        if(state != null && district != null && constituancy != null){
            FirebaseDatabase.getInstance().getReference().child("States").child(state).child("MLA")
                    .child("district").child(district)
                    .child("Constituancy").child(constituancy).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String mlaUrl = dataSnapshot.child("mla_image").getValue(String.class);
                    String mlaName = dataSnapshot.child("mla_name").getValue(String.class);
                    long rating = 90;
                    try{
                        rating = dataSnapshot.child("rating").getValue(long.class);
                        news.setVotePercentage(String.valueOf(rating));
                       // Log.e("rating",""+rating);
                    }catch (NullPointerException e){

                    }

                    if(mlaUrl != null){
                        news.setMlaImageUrl(mlaUrl);
                    }
                    if(mlaName != null){
                        news.setMla(mlaName);
                    }

                    checkUpdates(news,key,top);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    checkUpdates(news,key,top);

                }
            });
        }else {
            news.setMla("Admin Post");
            if(constituancy == null){
                news.setConstituancy("Multiple Constituancy");
            }
            checkUpdates(news,key,top);
        }


    }

    private void checkUpdates(News news, String key, boolean top) {
        Boolean isNewNews = true;
        for(int i=0;i<newsList.size();i++){
            if(newsList.get(i).getPostId().equals(key)){
               // Log.e("Postaaa",""+newsList.get(i).getPostId()+""+key);
                updateNewsListItem(i,news);
                isNewNews = false;
            }
        }
        if(isNewNews){
            if(top){
                newsList.add(0,news);
            }else {
                newsList.add(news);
            }

//                recyclerView = view.findViewById(R.id.home_rv);

//                recyclerView.setHasFixedSize(true);
//                layoutManager = new LinearLayoutManager(getActivity());
//                recyclerView.setLayoutManager(layoutManager);
//                adapter = new HomeAdapter(newsList, getActivity());
//                recyclerView.setAdapter(adapter);
            setClickListnerFromFirebase(news);
            adapter.notifyDataSetChanged();
//                recyclerView.smoothScrollToPosition(0);
        }
    }

    private void updateNewsListItem(int i, News news)
    {
        newsList.get(i).setAll(news);

        adapter.notifyItemChanged(i);

    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = view.findViewById(R.id.home_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        adapter = new HomeAdapter(newsList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    if (scrollY>scrollX){
//                        localitylayout.setVisibility(View.GONE);
//                    }
//                    if (scrollY<scrollX){
//                        localitylayout.setVisibility(View.GONE);
//
//                    }
//                }
//            });
//        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
//                    set_DisTance_Range.setVisibility(View.GONE);
//                    displayLocationRange.setVisibility(View.GONE);
//                    seekBar.setVisibility(View.GONE);

                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                           // Log.v("...", "Last Item Wow !");

                            Toast.makeText(getActivity(),"Loading More..",Toast.LENGTH_SHORT).show();
//                            Do pagination.. i.e. fetch new data
                            getMorePosts();
//                            loading = true;
                        }
                    }
                }


            }
        });
        adapter.notifyDataSetChanged();
       // Log.i("Home", " onResume");
//        Toast.makeText(getActivity(), "onResume", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onStart() {
        super.onStart();
       // Log.i("Home", " onStart");
//        Toast.makeText(getActivity(), "onStart", Toast.LENGTH_LONG).show();
//        new AsyncTaskForNews().execute();


    }

    @Override
    public void onPause() {
        super.onPause();
      //  Log.i("Home", " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
      //  Log.i("Home", " onStop");
//        scheduleJob();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob(String postId, String phoneNo, String typePost) {
        ComponentName serviceName = new ComponentName(getActivity().getPackageName(),
                BackGroundServices.class.getName());

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("postId",postId);
        bundle.putString("phoneNo", phoneNo);
        bundle.putString("typePost",typePost);
        jobId++;
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(bundle);
//                .setRequiresDeviceIdle(false)
//                .setRequiresCharging(true);

        JobInfo myJobInfo = builder.build();
        jobScheduler.schedule(myJobInfo);
//        Toast.makeText(getActivity(), R.string.job_scheduled, Toast.LENGTH_SHORT)
//                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // Log.i("Home", " onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // Log.i("Home", " onDestroy");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      //  Log.i("Home", " onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // Log.i("Home", " onDetach");
    }


    private void checkinSwtichStatus()
    {

        locRanTxt.setTextColor(getResources().getColor(R.color.gray));
        seekBar.setEnabled(false);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked==true)
                {
                    locRanTxt.setTextColor(getResources().getColor(R.color.pink2));
                    set_DisTance_Range.setVisibility(View.VISIBLE);
                    displayLocationRange.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setEnabled(true);
                    seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                        @Override
                        public void onProgressChanged(int progress, float progressFloat) {
                            seekbarPosition =seekBar.getProgress();
                            displayLocationRange.setText("Location Range "+String.valueOf(seekbarPosition)+" Km");
                            gettingNearbyPosts(seekbarPosition);
                            locRanTxt.setText(String.valueOf(seekbarPosition)+" Km");
                        }

                        @Override
                        public void getProgressOnActionUp(int progress, float progressFloat) {
                            set_DisTance_Range.setVisibility(View.GONE);
                            displayLocationRange.setVisibility(View.GONE);
                            seekBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void getProgressOnFinally(int progress, float progressFloat) {

                        }
                    });
                }
                else
                {
                    locRanTxt.setText("0 Km");
                    locRanTxt.setTextColor(getResources().getColor(R.color.gray));
                    switchStatus = switch1.getTextOff().toString();
                    seekBar.setEnabled(false);

                    set_DisTance_Range.setVisibility(View.GONE);
                    displayLocationRange.setVisibility(View.GONE);
                    seekBar.setVisibility(View.GONE);
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
        else if(seekbarPosition>10 && seekbarPosition<20)
            temp=20;
        else
            temp=30;


        final int finalTemp = temp;
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists() && dataSnapshot.hasChild("latitude"))
                        {
                            final String lat =  dataSnapshot.child("latitude").getValue().toString();
                            final String  lon =  dataSnapshot.child("longitude").getValue().toString();

                            FirebaseDatabase.getInstance().getReference().child(INDIA)
                                    .child(sharedPreference.readState())
                                    .child(sharedPreference.readDistrict())
                                    .child(CONSTITUANCY)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot constituences:dataSnapshot.getChildren()) {
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

                                                   // Log.d("distance", String.valueOf(distance));

                                                    if(finalTemp > distance) {
                                                        if (!nearbyConstuencies.contains(constituences.getKey())) {
                                                            nearbyConstuencies.add(constituences.getKey());
                                                        }
                                                      //  Log.e("log: ","constituences in: "+nearbyConstuencies);

                                                        for (int i=0;i<nearbyConstuencies.size();i++) {

                                                            final int finalI = i;
                                                            FirebaseDatabase.getInstance().getReference().child(INDIA)
                                                                    .child(sharedPreference.readState())
                                                                    .child(sharedPreference.readDistrict())
                                                                    .child(CONSTITUANCY)
                                                                    .child(nearbyConstuencies.get(i)).child("PostID")
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            for (DataSnapshot newlist:dataSnapshot.getChildren()) {
                                                                                if(!list.contains(newlist.getKey())) {
                                                                                    list5.add(newlist.getKey());

                                                                                   // Log.e("log: ","key in: "+newlist.getKey());

                                                                                }
                                                                            }
                                                                            Collections.reverse(list5);
                                                                            getNewsDetailsFromPost(true);
                                                                        }
                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {
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