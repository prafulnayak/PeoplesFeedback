package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Model.GovAgency;
import shamgar.org.peoplesfeedback.Model.MLAModel;
import shamgar.org.peoplesfeedback.R;

public class ViewAllPoliticiansAdapter extends RecyclerView.Adapter<ViewAllPoliticiansAdapter.ViewallViewHolder> {
    Context context;
    ArrayList<String> districtList;
    ArrayList<String> constList;

    ArrayList<GovAgency> govAgencies = new ArrayList<>();
    ArrayList<MLAModel> mlaModels = new ArrayList<>();

    private TaglistAdapter taglistAdapter;
    private ConstituencyListDetailsAdapter constituencyListDetailsAdapter;
    private String state;



    public ViewAllPoliticiansAdapter(Context applicationContext, ArrayList<String> districtList, String state, ArrayList<String> constituencyCount) {
        this.context=applicationContext;
        this.districtList=districtList;
        this.state=state;
        this.constList=constituencyCount;
    }

    @NonNull
    @Override
    public ViewallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.viewall_recyclerview_items, parent, false);


        return new ViewallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewallViewHolder holder, final int position) {

        holder.district_name_in_view_all_rv.setText(districtList.get(position));
        holder.num_of_const_in_view_all_rv.setText(constList.get(position));
        holder.district_name_in_view_all_rv.setTextColor(Color.parseColor("#000000"));

        holder.viewAllImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.district_name_in_view_all_rv.setTextColor(Color.parseColor("#c2185b"));
                if (holder.taglistRecyclerView.getVisibility() == View.GONE) {

                    //getting Tag List

//                    govAgencies.clear();
                    holder.taglistRecyclerView.setVisibility(View.VISIBLE);
                    holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
                    Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                            .child(state).child("MLA").child("district").child(districtList.get(position));

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            govAgencies.clear();
                            mlaModels.clear();
                            if (!dataSnapshot.exists()){
                                Log.e("error","no data exists");
                                return;
                            }

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (!snapshot.exists()){
                                    Toast.makeText(context,"data not avaliable",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!snapshot.getKey().equals("Constituancy")){
                                    GovAgency govAgency = snapshot.getValue(GovAgency.class);
                                    govAgency.setGovAgencyName(snapshot.getKey());
                                    govAgency.setDistrictName(districtList.get(position));
                                    govAgencies.add(govAgency);
                                }else {
                                    for(DataSnapshot dataSnapshotC : snapshot.getChildren()){
                                       // Log.e("mla count", String.valueOf(dataSnapshotC.getChildrenCount()));
                                        MLAModel mlaModel = dataSnapshotC.getValue(MLAModel.class);
                                       // Log.e("mla con",dataSnapshotC.getKey()+mlaModel.getMla_name()+mlaModel.getRating()+mlaModel.getMla_image()+mlaModel.getVotes());

                                        mlaModel.setConstituancyName(dataSnapshotC.getKey());
                                        mlaModels.add(mlaModel);
                                    }
                                }

                            }

                           // Log.e("districtdfsadf", "" + districtList.get(position).toString());
//                            holder.taglistRecyclerView.setVisibility(View.VISIBLE);
//                            taglistAdapter = new TaglistAdapter(context, tagnames,rating,votes,districtList.get(position).toString());
                            taglistAdapter = new TaglistAdapter(context, govAgencies,state);
                            holder.taglistRecyclerView.setHasFixedSize(true);
                            holder.taglistRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            holder.taglistRecyclerView.setAdapter(taglistAdapter);
                            taglistAdapter.notifyDataSetChanged();

//                            holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
//                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,constituencyList,constituencyMlaImage,constituencyMlaname,constituencyMlaParty,constituencyMlaRating);
                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,mlaModels,state, districtList.get(position));
                            holder.constituencyRecyclerview.setHasFixedSize(true);
                            holder.constituencyRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            holder.constituencyRecyclerview.setAdapter(constituencyListDetailsAdapter);
                            constituencyListDetailsAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    postQuery.addValueEventListener(valueEventListener);

//                    //getting Constituency List
//                    Query postQuery2 = FirebaseDatabase.getInstance().getReference().child("States")
//                            .child(state).child("MLA").child("district").child(districtList.get(position)).child("Constituancy");
//                    ValueEventListener valueEventListener1 = new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                constituencyList.add(snapshot.getKey());
//                                constituencyMlaImage.add(snapshot.child("mla_image").getValue().toString());
//                                constituencyMlaname.add(snapshot.child("mla_name").getValue().toString());
//                                constituencyMlaParty.add(snapshot.child("party").getValue().toString());
//                                constituencyMlaRating.add(snapshot.child("rating").getValue().toString());
//                                Log.e("sanpshot",snapshot.getKey());
//                            }
//                            holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
//                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,constituencyList,constituencyMlaImage,constituencyMlaname,constituencyMlaParty,constituencyMlaRating);
//                            holder.constituencyRecyclerview.setHasFixedSize(true);
//                            holder.constituencyRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//                            holder.constituencyRecyclerview.setAdapter(constituencyListDetailsAdapter);
//                        }
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                        }
//                    };
//                    postQuery2.addValueEventListener(valueEventListener1);
                }
                else
                {
                    holder.district_name_in_view_all_rv.setTextColor(Color.parseColor("#000000"));
                    holder.taglistRecyclerView.setVisibility(View.GONE);
                    holder.constituencyRecyclerview.setVisibility(View.GONE);
                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.district_name_in_view_all_rv.setTextColor(Color.parseColor("#c2185b"));
                if (holder.taglistRecyclerView.getVisibility() == View.GONE) {

                    //getting Tag List

//                    govAgencies.clear();
                    holder.taglistRecyclerView.setVisibility(View.VISIBLE);
                    holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
                    Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                            .child(state).child("MLA").child("district").child(districtList.get(position));

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            govAgencies.clear();
                            mlaModels.clear();
                            if (!dataSnapshot.exists()){
                                return;
                            }
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (!snapshot.exists()){
                                    Toast.makeText(context,"data not avaliable",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!snapshot.getKey().equals("Constituancy")){
                                    GovAgency govAgency = snapshot.getValue(GovAgency.class);
                                    govAgency.setGovAgencyName(snapshot.getKey());
                                    govAgency.setDistrictName(districtList.get(position));
                                    govAgencies.add(govAgency);
                                }else {
                                    for(DataSnapshot dataSnapshotC : snapshot.getChildren()){

                                        MLAModel mlaModel = dataSnapshotC.getValue(MLAModel.class);
                                      //  Log.e("mla con",dataSnapshotC.getKey()+mlaModel.getMla_name()+mlaModel.getRating()+mlaModel.getMla_image()+mlaModel.getVotes());

                                        mlaModel.setConstituancyName(dataSnapshotC.getKey());
                                        mlaModels.add(mlaModel);
                                    }
                                }

                            }

                         //   Log.e("districtdfsadf", "" + districtList.get(position).toString());
//                            holder.taglistRecyclerView.setVisibility(View.VISIBLE);
//                            taglistAdapter = new TaglistAdapter(context, tagnames,rating,votes,districtList.get(position).toString());
                            taglistAdapter = new TaglistAdapter(context, govAgencies,state);
                            holder.taglistRecyclerView.setHasFixedSize(true);
                            holder.taglistRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            holder.taglistRecyclerView.setAdapter(taglistAdapter);
                            taglistAdapter.notifyDataSetChanged();

//                            holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
//                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,constituencyList,constituencyMlaImage,constituencyMlaname,constituencyMlaParty,constituencyMlaRating);
                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,mlaModels,state, districtList.get(position));
                            holder.constituencyRecyclerview.setHasFixedSize(true);
                            holder.constituencyRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            holder.constituencyRecyclerview.setAdapter(constituencyListDetailsAdapter);
                            constituencyListDetailsAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    postQuery.addValueEventListener(valueEventListener);

//                    //getting Constituency List
//                    Query postQuery2 = FirebaseDatabase.getInstance().getReference().child("States")
//                            .child(state).child("MLA").child("district").child(districtList.get(position)).child("Constituancy");
//                    ValueEventListener valueEventListener1 = new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                constituencyList.add(snapshot.getKey());
//                                constituencyMlaImage.add(snapshot.child("mla_image").getValue().toString());
//                                constituencyMlaname.add(snapshot.child("mla_name").getValue().toString());
//                                constituencyMlaParty.add(snapshot.child("party").getValue().toString());
//                                constituencyMlaRating.add(snapshot.child("rating").getValue().toString());
//                                Log.e("sanpshot",snapshot.getKey());
//                            }
//                            holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
//                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,constituencyList,constituencyMlaImage,constituencyMlaname,constituencyMlaParty,constituencyMlaRating);
//                            holder.constituencyRecyclerview.setHasFixedSize(true);
//                            holder.constituencyRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//                            holder.constituencyRecyclerview.setAdapter(constituencyListDetailsAdapter);
//                        }
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                        }
//                    };
//                    postQuery2.addValueEventListener(valueEventListener1);
                }
                else
                {
                    holder.district_name_in_view_all_rv.setTextColor(Color.parseColor("#000000"));
                    holder.taglistRecyclerView.setVisibility(View.GONE);
                    holder.constituencyRecyclerview.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return districtList.size();
    }

    public class ViewallViewHolder extends RecyclerView.ViewHolder
    {
        private RecyclerView taglistRecyclerView,constituencyRecyclerview;
        private ImageButton viewAllImg;
        private TextView district_name_in_view_all_rv,num_of_const_in_view_all_rv;

        public ViewallViewHolder(View itemView) {
            super(itemView);
            taglistRecyclerView=itemView.findViewById(R.id.tagsList);
            constituencyRecyclerview=itemView.findViewById(R.id.constituencyListdetials);
            viewAllImg=itemView.findViewById(R.id.viewAllImg);
            district_name_in_view_all_rv=itemView.findViewById(R.id.district_name_in_view_all_rv);
            num_of_const_in_view_all_rv=itemView.findViewById(R.id.num_of_const_in_view_all_rv);
        }



    }



}
