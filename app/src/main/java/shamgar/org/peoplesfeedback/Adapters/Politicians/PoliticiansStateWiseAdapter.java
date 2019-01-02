package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        holder.rating.setText(String.valueOf(details.getRating()));
        holder.votes.setText(String.valueOf(details.getVotes()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subist=new Intent(context, ConstituencyListActivity.class);
                context.startActivity(subist);
            }
        });

    }

    @Override
    public int getItemCount() {
        return stateList.size();
    }

    public class PoliticiansViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView politicianImage, stateImage;
        private TextView stateName,cmName,rating,votes;

        public PoliticiansViewHolder(View itemView) {
            super(itemView);

            politicianImage = itemView.findViewById(R.id.politiciansRecyclerViewImage);
            stateImage = itemView.findViewById(R.id.politicianFamousIcon);

            stateName = itemView.findViewById(R.id.txtStateName);
            cmName = itemView.findViewById(R.id.txtmlaName);
            rating = itemView.findViewById(R.id.txtMlaRating);
            votes = itemView.findViewById(R.id.txtMlaVotes);
        }
    }
}

