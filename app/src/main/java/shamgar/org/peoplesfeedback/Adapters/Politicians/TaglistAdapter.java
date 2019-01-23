package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Model.GovAgency;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.Profile_TagActivity;

public class TaglistAdapter extends RecyclerView.Adapter<TaglistAdapter.TaglistViewHolder> {

    private Context context;


    private ArrayList<GovAgency> govAgencies;



    public TaglistAdapter(Context context, ArrayList<GovAgency> govAgencies) {
        this.context = context;
        this.govAgencies = govAgencies;
    }


    @NonNull
    @Override
    public TaglistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.taglist_items_horizantal, parent, false);
        return new TaglistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaglistViewHolder holder, final int position) {

        final GovAgency govAgency = govAgencies.get(position);

        holder.district_name_in_tag_list.setText(govAgency.getDistrictName());
        holder.municipality_tag_tag_list.setText(govAgency.getGovAgencyName());
        holder.tag_rating_in_tag_list.setText("Rating "+String.valueOf(govAgency.getRating()));
        holder.votes_for_tag_in_tag_List.setText("Votes"+String.valueOf(govAgency.getVotes()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tags_profile=new Intent(context, Profile_TagActivity.class);
                tags_profile.putExtra("district",govAgency.getDistrictName());
                tags_profile.putExtra("tag",govAgency.getGovAgencyName());
                context.startActivity(tags_profile);
            }
        });
    }


    @Override
    public int getItemCount() {
        return govAgencies.size();
    }

    public class TaglistViewHolder extends RecyclerView.ViewHolder
    {
        public TextView district_name_in_tag_list,municipality_tag_tag_list,tag_rating_in_tag_list,votes_for_tag_in_tag_List;

        public TaglistViewHolder(View itemView) {
            super(itemView);

            district_name_in_tag_list=itemView.findViewById(R.id.district_name_in_tag_list);
            municipality_tag_tag_list=itemView.findViewById(R.id.municipality_tag_tag_list);
            tag_rating_in_tag_list=itemView.findViewById(R.id.tag_rating_in_tag_list);
            votes_for_tag_in_tag_List=itemView.findViewById(R.id.votes_for_tag_in_tag_List);
        }
    }
}
