package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shamgar.org.peoplesfeedback.Model.PartyStateMla;
import shamgar.org.peoplesfeedback.R;

public class HorizantalPoliticianAdapter extends RecyclerView.Adapter<HorizantalPoliticianAdapter.HorizantalViewHolder> {

    private List<String> mDataList;
    private int mRowIndex = -1;
    private ArrayList<PartyStateMla> partyStateMlas = new ArrayList<>();
    public HorizantalPoliticianAdapter(ArrayList<PartyStateMla> partyStateMlas) {
        this.partyStateMlas = partyStateMlas;
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
        holder.name.setText(partyStateMla.getName());

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
        public HorizantalViewHolder(View itemView) {
            super(itemView);
            polImageView = itemView.findViewById(R.id.mla_img_in_hori_rv);
            name = itemView.findViewById(R.id.txt_mla_name_in_hori_rv);
            rating = itemView.findViewById(R.id.txt_mla_rating_in_hori_rv);

        }
    }
}
