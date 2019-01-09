package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.ConstituencyListActivity;

public class ViewAllPoliticiansAdapter extends RecyclerView.Adapter<ViewAllPoliticiansAdapter.ViewallViewHolder> {
    Context context;
    ArrayList<String> districtList;

    private TaglistAdapter taglistAdapter;
    private ConstituencyListDetailsAdapter constituencyListDetailsAdapter;

    public ViewAllPoliticiansAdapter(Context context, ArrayList<String> districtList) {
        this.context=context;
        this.districtList=districtList;
    }

    @NonNull
    @Override
    public ViewallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.viewall_recyclerview_items, parent, false);
        return new ViewallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewallViewHolder holder, int position) {

        holder.district_name_in_view_all_rv.setText(districtList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.taglistRecyclerView.getVisibility()==View.GONE)
                {
                    holder.taglistRecyclerView.setVisibility(View.VISIBLE);
                    holder.constituencyRecyclerview.setVisibility(View.VISIBLE);

                    constituencyListDetailsAdapter=new ConstituencyListDetailsAdapter(context);
                    taglistAdapter=new TaglistAdapter(context);
                    holder.taglistRecyclerView.setHasFixedSize(true);
                    holder.constituencyRecyclerview.setHasFixedSize(true);
                    holder.taglistRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    holder.taglistRecyclerView.setAdapter(taglistAdapter);
                    holder.constituencyRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    holder.constituencyRecyclerview.setAdapter(constituencyListDetailsAdapter);
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
