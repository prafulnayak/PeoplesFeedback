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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.StateCm;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.ConstituencyListActivity;

public class PoliticiansStateWiseAdapter extends RecyclerView.Adapter<PoliticiansStateWiseAdapter.PoliticiansViewHolder>
{
    Context context;
    private ArrayList<StateCm> stateList;

    public PoliticiansStateWiseAdapter(ArrayList<StateCm> stateList, Context context) {
        this.context=context;
        this.stateList = stateList;
    }

    @NonNull
    @Override
    public PoliticiansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.politicians_list_statewise,parent,false);

        PoliticiansViewHolder myholder=new PoliticiansViewHolder(view);

        return myholder;
    }

    @Override
    public void onBindViewHolder(@NonNull PoliticiansViewHolder holder, int position) {

        final StateCm details = stateList.get(position);
        holder.stateName.setText(details.getStateName());
        holder.cmName.setText(details.getCM());
        holder.rating.setText(String.valueOf(details.getRating())+"%");
        holder.votes.setText(String.valueOf("Votes : "+details.getVotes()));
        holder.ratingBar.setRating(details.getRating()*5/100);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ConstituencyListActivity.class);
                intent.putExtra("state",details.getStateName());
                context.startActivity(intent);
            }
        });
        Glide.with(context)
                .load(details.getCm_url())
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.politicianImage);

    }

    @Override
    public int getItemCount() {
        return stateList.size();
    }

    public class PoliticiansViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView politicianImage, stateImage;
        private TextView stateName,cmName,rating,votes;
        private RatingBar ratingBar;

        public PoliticiansViewHolder(View itemView) {
            super(itemView);

            politicianImage = itemView.findViewById(R.id.politiciansRecyclerViewImage);
            stateImage = itemView.findViewById(R.id.politicianFamousIcon);

            stateName = itemView.findViewById(R.id.txtStateName);
            cmName = itemView.findViewById(R.id.txtmlaName);
            rating = itemView.findViewById(R.id.txtMlaRating);
            ratingBar = itemView.findViewById(R.id.rating);
            votes = itemView.findViewById(R.id.txtMlaVotes);
        }
    }
}

