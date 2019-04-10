package shamgar.org.peoplesfeedback.Fragments;


import android.app.DownloadManager;
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
import android.widget.DatePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.FollowAdapter;
import shamgar.org.peoplesfeedback.Adapters.HomeAdapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.widget.LinearLayout.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PloticiansFollow extends Fragment {
    
    private RecyclerView politicians_follow_recyclerview;
    private FollowAdapter followAdapter;
    private ArrayList<String> images;
    private ArrayList<String> names;
    private LinearLayoutManager layoutManager;

    private SharedPreferenceConfig config;
    private String number;
    public PloticiansFollow() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ploticians_follow, container, false);

        config=new SharedPreferenceConfig(getActivity());

        images=new ArrayList<>();
        names=new ArrayList<>();

        politicians_follow_recyclerview=view.findViewById(R.id.politicians_follow_recyclerview);
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
                    if (dataSnapshot.hasChild("pFollow")){
                        images.clear();
                        names.clear();
                        for (DataSnapshot dataSnapshot1:dataSnapshot.child("pFollow").getChildren()){
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
        Query query=FirebaseDatabase.getInstance().getReference().child("Politicians").child(key);

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("desc").getValue().toString()!=null && dataSnapshot.child("name").getValue().toString()!=null ) {
                        images.add(dataSnapshot.child("desc").getValue().toString());
                        names.add(dataSnapshot.child("name").getValue().toString());

                        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),VERTICAL);
                        followAdapter = new FollowAdapter(getActivity(), images, names);
                        politicians_follow_recyclerview.setHasFixedSize(true);
                        layoutManager = new LinearLayoutManager(getActivity());
                        politicians_follow_recyclerview.setLayoutManager(layoutManager);
                        politicians_follow_recyclerview.setAnimation(null);
                        politicians_follow_recyclerview.addItemDecoration(decoration);
                        politicians_follow_recyclerview.setAdapter(followAdapter);
                        politicians_follow_recyclerview.setNestedScrollingEnabled(false);
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
