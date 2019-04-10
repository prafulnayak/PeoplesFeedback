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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shamgar.org.peoplesfeedback.Model.PartyStateMla;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.PartyProfile;
import shamgar.org.peoplesfeedback.UI.Profile_mla_Activity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

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

        if (pos==0){
            Glide.with(context).load(partyStateMla.getImageUrl()+partyStateMla.getName()+".jpg").into(holder.polImageView);
        }
        if (pos==1){
            Glide.with(context).load(partyStateMla.getImageUrl()+partyStateMla.getName()+".jpg").into(holder.polImageView);

        }
        if (pos==2){
            Glide.with(context).load(partyStateMla.getImageUrl()).into(holder.polImageView);
        }

        holder.polImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos==0){
                        checkParty(partyStateMla.getName());
                }
                if(pos==1){
                    Log.e("tag","state party head");
                    partyHeads(partyStateMla.getName(),partyStateMla.getImageUrl(),partyStateMla.getHeading());
                }
                if(pos==2){
                    Log.e("tag","district mla");
                    checkingMla(partyStateMla.getName(),partyStateMla.getImageUrl());
                }
            }
        });

        holder.name.setText(partyStateMla.getName());
        if (!TextUtils.isEmpty(partyStateMla.getRating())){
            holder.pol_rating.setRating(Integer.parseInt(partyStateMla.getRating())*5/100);
        }
        holder.rating.setText(partyStateMla.getRating());



    }

    private void partyHeads(final String name, final String imageUrl, final String heading) {
        Query query= FirebaseDatabase.getInstance().getReference().child("Politicians");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(name)){
                        Intent mlaProfile=new Intent(context, Profile_mla_Activity.class);
                        mlaProfile.putExtra("mlaName",name);
                        mlaProfile.putExtra("mlaConstituency",dataSnapshot.child(name).child("con").getValue().toString());
                        mlaProfile.putExtra("state",dataSnapshot.child(name).child("state").getValue().toString());
                        mlaProfile.putExtra("district",dataSnapshot.child(name).child("dist").getValue().toString());
                        mlaProfile.putExtra("mla_image",imageUrl+name+".jpg");
                        mlaProfile.putExtra("status","1");
                        context.startActivity(mlaProfile);
                    }
                    else {
                        updatePoliticiansNode(name,heading);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addValueEventListener(valueEventListener);
    }

    private void updatePoliticiansNode(String name, String heading) {
        Map<String, Object> updates = new HashMap<String,Object>();
        updates.put("con","no constituency");
        updates.put("created_on","no data");
        updates.put("description","no data" );
        updates.put("dist","no data" );
        updates.put("dob","no data" );
        updates.put("email","no data" );
        updates.put("gender","no data" );
        updates.put("heading","no data" );
        updates.put("image_url","no data" );
        updates.put("name",name );
        updates.put("party",heading );
        updates.put("phone_no","no data" );
        updates.put("state","no data" );

        FirebaseDatabase.getInstance().getReference().child("Politicians").child(name)
                .updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("update","update successful");
                }
            }
        });
    }

    private void checkParty(final String name) {

        Query query= FirebaseDatabase.getInstance().getReference().child("Party");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(name)){
                        Intent mlaProfile=new Intent(context, PartyProfile.class);
                        mlaProfile.putExtra("partyName",name);
                        mlaProfile.putExtra("established_by",dataSnapshot.child(name).child("established_by").getValue().toString());
                        mlaProfile.putExtra("established_on",dataSnapshot.child(name).child("established_on").getValue().toString());
                        mlaProfile.putExtra("status","1");
                        context.startActivity(mlaProfile);
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

    private void checkingMla(final String name, final String imageUrl) {

        final SharedPreferenceConfig config=new SharedPreferenceConfig(context);

        Query query= FirebaseDatabase.getInstance().getReference().child("Politicians");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(name)){
                        Intent mlaProfile=new Intent(context, Profile_mla_Activity.class);
                        mlaProfile.putExtra("mlaName",name);
                        mlaProfile.putExtra("mlaConstituency",config.readConstituancy());
                        mlaProfile.putExtra("state",config.readState());
                        mlaProfile.putExtra("district",config.readDistrict() );
                        mlaProfile.putExtra("mla_image",imageUrl );
                        mlaProfile.putExtra("status","1");
                        context.startActivity(mlaProfile);
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