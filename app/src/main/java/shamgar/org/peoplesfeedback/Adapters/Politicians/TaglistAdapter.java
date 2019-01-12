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

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.Profile_TagActivity;

public class TaglistAdapter extends RecyclerView.Adapter<TaglistAdapter.TaglistViewHolder> {

    private Context context;
    private ArrayList<String> tagnames;
    private ArrayList<String> rating;
    private ArrayList<String> votes;
    private String s;

    public TaglistAdapter(Context context, ArrayList<String> tagnames, ArrayList<String> rating, ArrayList<String> votes, String s) {
        this.context=context;
        this.tagnames=tagnames;
        this.rating=rating;
        this.votes=votes;
        this.s=s;
    }


    @NonNull
    @Override
    public TaglistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.taglist_items_horizantal, parent, false);
        return new TaglistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaglistViewHolder holder, final int position) {
        holder.district_name_in_tag_list.setText(s);
        holder.municipality_tag_tag_list.setText(tagnames.get(position).toString());
        holder.tag_rating_in_tag_list.setText(rating.get(position).toString());
        holder.votes_for_tag_in_tag_List.setText(votes.get(position).toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tags_profile=new Intent(context, Profile_TagActivity.class);
                tags_profile.putExtra("district",s);
                tags_profile.putExtra("tag",tagnames.get(position));
                context.startActivity(tags_profile);
            }
        });
    }


    @Override
    public int getItemCount() {
        return tagnames.size();
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
