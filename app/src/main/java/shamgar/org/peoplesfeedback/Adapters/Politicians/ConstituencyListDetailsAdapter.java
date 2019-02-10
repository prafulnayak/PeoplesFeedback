package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Model.MLAModel;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.Profile_mla_Activity;

public class ConstituencyListDetailsAdapter extends RecyclerView.Adapter<ConstituencyListDetailsAdapter.ConstituencyViewHolder> {
    private Context context;
    private ArrayList<MLAModel> mlaModels;
    private String state;
    private String district;



    public ConstituencyListDetailsAdapter(Context context, ArrayList<MLAModel> mlaModels, String state, String district) {
        this.context = context;
        this.mlaModels = mlaModels;
        this.state=state;
        this.district=district;
    }


    @NonNull
    @Override
    public ConstituencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.constituencylist_details_rv, parent, false);
        return new ConstituencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConstituencyViewHolder holder, int position) {

        final MLAModel mlaModel = mlaModels.get(position);

        holder.constituency_mla_name.setText(mlaModel.getMla_name());
        holder.txtMlaConstituency.setText(mlaModel.getConstituancyName());
        holder.txt_MlaRating_In_ConstituencyList.setText(String.valueOf(mlaModel.getRating())+"%");
        holder.constituency_rating.setRating(mlaModel.getRating()*5/100);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mlaProfile=new Intent(context, Profile_mla_Activity.class);
                mlaProfile.putExtra("mlaName",mlaModel.getMla_name());
                mlaProfile.putExtra("mlaConstituency",mlaModel.getConstituancyName());
                mlaProfile.putExtra("state",state);
                mlaProfile.putExtra("district",district);
                mlaProfile.putExtra("rating",mlaModel.getRating());
                mlaProfile.putExtra("votes",mlaModel.getVotes());
                Toast.makeText(context,"rating "+mlaModel.getVotes(),Toast.LENGTH_SHORT).show();
                context.startActivity(mlaProfile);
            }
        });

        Glide.with(context).load("https://upload.wikimedia.org/wikipedia/commons/e/e1/Nuernberg-fronfeste-und-kettensteg-v-O.jpg").into(holder.constituency_mla_image);

    }

    @Override
    public int getItemCount() {
        return mlaModels.size();
    }

    public class ConstituencyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView constituency_mla_name,txtMlaConstituency,txt_MlaRating_In_ConstituencyList;
        private ImageView constituency_mla_image;
        private CircleImageView mla_party_img;
        private RatingBar constituency_rating;


        public ConstituencyViewHolder(View itemView) {
            super(itemView);

            constituency_mla_image=itemView.findViewById(R.id.constituency_mla_image);
            constituency_mla_name=itemView.findViewById(R.id.constituency_mla_name);
            txtMlaConstituency=itemView.findViewById(R.id.txtMlaConstituency);
            mla_party_img=itemView.findViewById(R.id.mla_party_img);
            txt_MlaRating_In_ConstituencyList=itemView.findViewById(R.id.txt_MlaRating_In_ConstituencyList);
            constituency_rating=itemView.findViewById(R.id.constituency_rating);
        }
    }
}
