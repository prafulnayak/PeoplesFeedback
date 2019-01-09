package shamgar.org.peoplesfeedback.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Adapters.Politicians.PoliticiansStateWiseAdapter;

import shamgar.org.peoplesfeedback.Model.StateCm;
import shamgar.org.peoplesfeedback.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Politicians extends Fragment  {

    private RecyclerView politiciansRecyclerView;
    private PoliticiansStateWiseAdapter politiciansStateWiseAdapter;

    private CardView politianCadrview;
    private ArrayList<StateCm> stateList = new ArrayList<>();

    public Politicians() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        politiciansStateWiseAdapter=new PoliticiansStateWiseAdapter(stateList,getActivity());

        politiciansRecyclerView=view.findViewById(R.id.politiciansRecyclerView);
        politiciansRecyclerView.setHasFixedSize(true);
        politiciansRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        politiciansRecyclerView.setAdapter(politiciansStateWiseAdapter);

        politianCadrview=view.findViewById(R.id.politianCadrview);

        politianCadrview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("restricted_access/secret_document");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Object document = dataSnapshot.getValue();
                        Toast.makeText(getActivity(),"dfsdfgds",Toast.LENGTH_LONG).show();
                        System.out.println(document);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }
        });
        if(stateList.size()== 0)
            getStates();
    }

    private void getStates() {
        Query postQuery = FirebaseDatabase.getInstance().getReference().child("Politics")
                .child("india").child("state");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e("state", ""+snapshot.getKey());
                    StateCm stateCm = snapshot.getValue(StateCm.class);
//                    if(stateCm.getCM() != null){
                        Log.e("state cm", ""+stateCm.getCM()+snapshot.getValue().toString());
                        stateCm.setStateName(snapshot.getKey());
                        stateList.add(stateCm);
//                    }
//                    StateCm stateDetails = new StateCm(snapshot.getKey(),)
                }
                politiciansStateWiseAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addValueEventListener(valueEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_politicians, container, false);

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            Log.d("Politicians","visible");
        }else{
            // fragment is not visible
        }
    }




}
