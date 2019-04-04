package shamgar.org.peoplesfeedback.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.FollowAdapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.widget.LinearLayout.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFollow extends Fragment {
    private RecyclerView people_follow_recyclerview;
    private FollowAdapter followAdapter;
    private ArrayList<String> images;
    private ArrayList<String> names;
    private LinearLayoutManager layoutManager;

    private SharedPreferenceConfig config;
    String number;


    public PeopleFollow() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people_follow, container, false);

        people_follow_recyclerview=view.findViewById(R.id.people_follow_recyclerview);

        config=new SharedPreferenceConfig(getActivity());

        images=new ArrayList<>();
        names=new ArrayList<>();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle=this.getArguments();
        if (bundle!=null){
            number=bundle.getString("number","123");

            Log.e("follow",number);
            gettingImagesAndNames();
        }

    }

    private void gettingImagesAndNames() {
        Query query= FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(number.substring(3));
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("following")){
                        images.clear();
                        names.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.child("following").getChildren()){
                            Log.e("names",dataSnapshot1.getKey());
                            imagesUrls(dataSnapshot1.getKey());
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

    private void imagesUrls(String key) {
        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(key);

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("desc").getValue().toString()!=null && dataSnapshot.child("name").getValue().toString()!=null ) {
                        images.add(dataSnapshot.child("desc").getValue().toString());
                        names.add(dataSnapshot.child("name").getValue().toString());

                        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),VERTICAL);

                        followAdapter = new FollowAdapter(getActivity(), images, names);
                        people_follow_recyclerview.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getActivity());
                        people_follow_recyclerview.setLayoutManager(layoutManager);
                        people_follow_recyclerview.setAnimation(null);
                        people_follow_recyclerview.addItemDecoration(decoration);
                        people_follow_recyclerview.setAdapter(followAdapter);
                        people_follow_recyclerview.setNestedScrollingEnabled(false);
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
