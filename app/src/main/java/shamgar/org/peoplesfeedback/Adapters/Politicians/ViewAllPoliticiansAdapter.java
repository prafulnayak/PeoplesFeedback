package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.R;

import static android.widget.LinearLayout.VERTICAL;

public class ViewAllPoliticiansAdapter extends RecyclerView.Adapter<ViewAllPoliticiansAdapter.ViewallViewHolder> {
    Context context;
    ArrayList<String> districtList;
    ArrayList<String> TagList=new ArrayList<>();
    ArrayList<String> constituencyList=new ArrayList<>();



    private TaglistAdapter taglistAdapter;
    private ConstituencyListDetailsAdapter constituencyListDetailsAdapter;
    private String state;

    public ViewAllPoliticiansAdapter(Context context, ArrayList<String> districtList, String state) {
        this.context=context;
        this.districtList=districtList;
        this.state=state;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.taglistRecyclerView.getVisibility() == View.GONE) {

                    //getting Tag List
                    TagList.clear();
                    Query postQuery = FirebaseDatabase.getInstance().getReference().child("States")
                            .child(state).child("MLA").child("district").child(districtList.get(position));
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (!snapshot.getKey().equals("Constituancy")){
                                    TagList.add(snapshot.getKey().toString());
                                }

                            }
                            Log.e("taglist", "" + TagList);
                            holder.taglistRecyclerView.setVisibility(View.VISIBLE);
                            taglistAdapter = new TaglistAdapter(context, TagList);
                            holder.taglistRecyclerView.setHasFixedSize(true);
                            holder.taglistRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            holder.taglistRecyclerView.setAdapter(taglistAdapter);
                            taglistAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    postQuery.addValueEventListener(valueEventListener);

                    //getting Constituency List
                    constituencyList.clear();
                    Query postQuery2 = FirebaseDatabase.getInstance().getReference().child("States")
                            .child(state).child("MLA").child("district").child(districtList.get(position)).child("Constituancy");
                    ValueEventListener valueEventListener1 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                constituencyList.add(snapshot.getKey());
                                Log.e("sanpshot",snapshot.getKey());
                            }
                            holder.constituencyRecyclerview.setVisibility(View.VISIBLE);
                            constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context,constituencyList);
                            holder.constituencyRecyclerview.setHasFixedSize(true);
                            holder.constituencyRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            holder.constituencyRecyclerview.setAdapter(constituencyListDetailsAdapter);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    postQuery2.addValueEventListener(valueEventListener1);
                }
                else
                {
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
        private TextView district_name_in_view_all_rv;

        public ViewallViewHolder(View itemView) {
            super(itemView);
            taglistRecyclerView=itemView.findViewById(R.id.tagsList);
            constituencyRecyclerview=itemView.findViewById(R.id.constituencyListdetials);
            viewAllImg=itemView.findViewById(R.id.viewAllImg);
            district_name_in_view_all_rv=itemView.findViewById(R.id.district_name_in_view_all_rv);
        }



    }



}
