package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import shamgar.org.peoplesfeedback.Model.PartyStateMla;
import shamgar.org.peoplesfeedback.R;

public class HorizantalPoliticianAdapter extends RecyclerView.Adapter<HorizantalPoliticianAdapter.HorizantalViewHolder> {

    private List<String> mDataList;
    private int mRowIndex = -1;
    private Context context;
    private ArrayList<PartyStateMla> partyStateMlas = new ArrayList<>();
    public HorizantalPoliticianAdapter(Context mContext, ArrayList<PartyStateMla> partyStateMlas) {
        this.partyStateMlas = partyStateMlas;
        this.context = mContext;
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

        if (!TextUtils.isEmpty(partyStateMla.getRating())){
            holder.pol_rating.setRating(Integer.parseInt(partyStateMla.getRating())*5/100);
        }
        holder.rating.setText(partyStateMla.getRating());
        Glide.with(context)
                .load(partyStateMla.getImageUrl())
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.polImageView);

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
        private RatingBar pol_rating;
        public HorizantalViewHolder(View itemView) {
            super(itemView);
            polImageView = itemView.findViewById(R.id.mla_img_in_hori_rv);
            name = itemView.findViewById(R.id.txt_mla_name_in_hori_rv);
            rating = itemView.findViewById(R.id.txt_mla_rating_in_hori_rv);
            pol_rating = itemView.findViewById(R.id.pol_rating);


        }
    }
}
