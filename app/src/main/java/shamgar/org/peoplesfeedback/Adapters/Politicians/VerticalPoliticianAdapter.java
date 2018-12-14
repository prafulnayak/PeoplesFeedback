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

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.ViewAllPoliticiansActivity;

public class VerticalPoliticianAdapter extends RecyclerView.Adapter<VerticalPoliticianAdapter.VerticalViewHolder> {

    private final Context mContext;
    private static RecyclerView horizontalList;

    public VerticalPoliticianAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_constituency_list, parent, false);
        return new VerticalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder holder, int position) {
        holder.politicians_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAll=new Intent(mContext, ViewAllPoliticiansActivity.class);
                mContext.startActivity(viewAll);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder
    {
        private HorizantalPoliticianAdapter horizontalAdapter;
        private Button politicians_view_all;

        public VerticalViewHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            horizontalList = (RecyclerView) itemView.findViewById(R.id.horizontal_recycler_view_politicians);
            politicians_view_all = (Button) itemView.findViewById(R.id.politicians_view_all);
            horizontalList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            horizontalAdapter = new HorizantalPoliticianAdapter();
            horizontalList.setAdapter(horizontalAdapter);
        }
    }
}
