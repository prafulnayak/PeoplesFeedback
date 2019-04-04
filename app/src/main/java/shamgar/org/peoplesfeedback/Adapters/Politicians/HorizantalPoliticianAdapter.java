package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import shamgar.org.peoplesfeedback.Model.PartyStateMla;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.Profile_mla_Activity;

public class HorizantalPoliticianAdapter extends RecyclerView.Adapter<HorizantalPoliticianAdapter.HorizantalViewHolder> {

    private List<String> mDataList;
    private int mRowIndex = -1;
    private Context context;
    private ArrayList<PartyStateMla> partyStateMlas = new ArrayList<>();
    int pos;
    public HorizantalPoliticianAdapter(ArrayList<PartyStateMla> partyStateMlas, Context mContext, int position) {
        this.partyStateMlas = partyStateMlas;
        this.context=mContext;
        this.pos=position;
    }


    @NonNull
    @Override
    public HorizantalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.horizantal_recyclerview_items_politicians, parent, false);
        HorizantalViewHolder holder = new HorizantalViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HorizantalViewHolder holder, int position) {

        final PartyStateMla partyStateMla = partyStateMlas.get(position);
        Glide.with(context).load(partyStateMla.getImageUrl()).into(holder.polImageView);

            holder.polImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(pos==0){
                            checkingMla(partyStateMla.getName());
                        }
                       if(pos==1){
                           Log.e("tag","state party head");
                       }
                       if(pos==2){
                           Log.e("tag","district mla");
                       }
                }
            });


        holder.name.setText(partyStateMla.getName());
        if (!TextUtils.isEmpty(partyStateMla.getRating())){
            holder.pol_rating.setRating(Integer.parseInt(partyStateMla.getRating())*5/100);
        }
        holder.rating.setText(partyStateMla.getRating());


    }

    private void checkingMla(final String name) {

        Query query= FirebaseDatabase.getInstance().getReference().child("Politicians");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(name)){
//                        Intent mlaProfile=new Intent(context, Profile_mla_Activity.class);
//                        mlaProfile.putExtra("mlaName",name);
//                        mlaProfile.putExtra("mlaConstituency",dataSnapshot.child(name).child(""));
//                        mlaProfile.putExtra("state",news.getState());
//                        mlaProfile.putExtra("district",news.getDistrict());
//                        mlaProfile.putExtra("status","1");
//                        context.startActivity(mlaProfile);
                        Log.e("tag","normal ui");
                    }
                    else {
                        Log.e("tag","change ui");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addValueEventListener(valueEventListener);
    }

    @Override
    public int getItemCount() {
        return partyStateMlas.size();
    }

    public void setData(List<String> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    public class HorizantalViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView polImageView;
        private TextView name;
        private TextView rating;
        private RatingBar pol_rating;
        public HorizantalViewHolder(View itemView) {
            super(itemView);
            polImageView = itemView.findViewById(R.id.mla_img_in_hori_rv);
            name = itemView.findViewById(R.id.txt_mla_name_in_hori_rv);
            rating = itemView.findViewById(R.id.txt_mla_rating_in_hori_rv);
            pol_rating = itemView.findViewById(R.id.pol_rating);


        }
    }
}
