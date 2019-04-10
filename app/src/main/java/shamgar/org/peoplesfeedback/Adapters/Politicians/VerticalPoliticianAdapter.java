package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.PartyStateMla;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.ViewAllPoliticiansActivity;

public class VerticalPoliticianAdapter extends RecyclerView.Adapter<VerticalPoliticianAdapter.VerticalViewHolder> {

    private final Context mContext;
    private static RecyclerView horizontalList;
    private HorizantalPoliticianAdapter horizontalAdapter;
    private ArrayList<ArrayList<PartyStateMla>> masterPartyStateMlas = new ArrayList<ArrayList<PartyStateMla>>();

    private String state;

    public VerticalPoliticianAdapter(Context mContext, ArrayList<ArrayList<PartyStateMla>> masterPartyStateMlas,String state) {
        this.mContext = mContext;
        this.masterPartyStateMlas = masterPartyStateMlas;
        this.state=state;
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_constituency_list, parent, false);
        return new VerticalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, int position) {
        final ArrayList<PartyStateMla> partyStateMlas = masterPartyStateMlas.get(position);
        switch (position){
            case 0:
                holder.headingRV.setText("State Party");
                break;
            case 1:
                holder.headingRV.setText("State Party Head");
                break;
            default:
                holder.headingRV.setText(partyStateMlas.get(0).getHeading()+" Politicians");
        }
        holder.politicians_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAll=new Intent(mContext, ViewAllPoliticiansActivity.class);
                viewAll.putExtra("state",state);
                mContext.startActivity(viewAll);
            }
        });

        horizontalList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new HorizantalPoliticianAdapter(partyStateMlas,mContext,position);
        horizontalList.setAdapter(horizontalAdapter);

    }

    @Override
    public int getItemCount() {
        return masterPartyStateMlas.size();
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder
    {
        //        private HorizantalPoliticianAdapter horizontalAdapter;
        private Button politicians_view_all;
        private TextView headingRV;

        public VerticalViewHolder(View itemView) {
            super(itemView);
            headingRV = itemView.findViewById(R.id.feedbackHeading);
            Context context = itemView.getContext();
            horizontalList = (RecyclerView) itemView.findViewById(R.id.horizontal_recycler_view_politicians);
            politicians_view_all = (Button) itemView.findViewById(R.id.politicians_view_all);

        }
    }
}