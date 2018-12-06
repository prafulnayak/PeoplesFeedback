package shamgar.org.peoplesfeedback.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import shamgar.org.peoplesfeedback.Adapters.HomeAdapter;
import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.Posts;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.CameraActivity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static shamgar.org.peoplesfeedback.ConstantName.NamesC.CONSTITUANCY;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.INDIA;
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
    LinearLayoutManager layoutManager;
    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreference = new SharedPreferenceConfig(getActivity());


        floatingActionButton = view.findViewById(R.id.floating_action);

        Log.i("Home", " Home Fragment");
        Toast.makeText(getActivity(),"home",Toast.LENGTH_LONG).show();

        recyclerView = view.findViewById(R.id.home_rv);
//        recyclerView.setHasFixedSize(true);


//        recyclerView.setLayoutManager(linearLayoutManager);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CameraActivity.class));

            }
        });
        new AsyncTaskForNews().execute();

        adapter = new HomeAdapter(newsList, getActivity());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void getNewsKeyFromConstituancy() {
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .child("PostID").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                //if key exist no need to add or else add to the list
                if(!list.contains(dataSnapshot.getKey())){
                    list.add(dataSnapshot.getKey());
                    Log.e("log: ","key in: "+dataSnapshot.getKey());

                }


                Log.e("log: ","Lkey: "+previousChildName);
                Log.e("log: ","L: "+list.size());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

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



    }

    private class AsyncTaskForNews extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            getNewsKeyFromConstituancy();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(list.size()>0){
                getNewsDetailsFromPost();
            }
        }
    }

    private void getNewsDetailsFromPost() {
        for (int i=0; i<list.size();i++){
            final int p =i;
            FirebaseDatabase.getInstance().getReference().child(POSTS)
                    .child(list.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                        Posts posts = dataSnapshot.getValue(Posts.class);
                        addOrUpdateNewsList(posts,dataSnapshot.getKey());
//                    }
                    Log.e("Post",""+posts.getImageUrl());
                    Log.e("Post data snap key",""+dataSnapshot.getKey());
                    Log.e("Post list ",""+list.get(p));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Postaaa",""+databaseError.toString());
                }
            });
        }

    }

    private void addOrUpdateNewsList(Posts posts, String key) {

        News news = new News(
                key,
                sharedPreference.readPhoneNo(),"url",posts.getHeading(),
                posts.getDescription(),sharedPreference.readConstituancy(),
                posts.getImageUrl(),Double.parseDouble(posts.getLatitude()),
                Double.parseDouble(posts.getLongitude()),posts.getAddress(),
                "mla","malImageUrl","100",
                posts.getTagId(),50,100,200,posts.getPostedOn(),1);
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
            }

//        }else
//            newsList.add(news);
        adapter = new HomeAdapter(newsList, getActivity());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void updateNewsListItem(int i, News news) {
        newsList.get(i).setAll(news);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}