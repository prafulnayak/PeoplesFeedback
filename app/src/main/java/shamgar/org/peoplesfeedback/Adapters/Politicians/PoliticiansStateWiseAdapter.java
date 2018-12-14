package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.ConstituencyListActivity;

public class PoliticiansStateWiseAdapter extends RecyclerView.Adapter<PoliticiansStateWiseAdapter.PoliticiansViewHolder>
{
    Context context;

    public PoliticiansStateWiseAdapter(Context context) {
        this.context=context;
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
        return 5;
    }

    public class PoliticiansViewHolder extends RecyclerView.ViewHolder
    {

        public PoliticiansViewHolder(View itemView) {
            super(itemView);
        }
    }
}

