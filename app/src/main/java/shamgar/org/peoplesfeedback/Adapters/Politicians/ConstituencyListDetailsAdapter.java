package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.security.PublicKey;
import java.util.ArrayList;

import shamgar.org.peoplesfeedback.R;

public class ConstituencyListDetailsAdapter extends RecyclerView.Adapter<ConstituencyListDetailsAdapter.ConstituencyViewHolder> {
    private Context context;
    private ArrayList<String> constituencyList;
    private ArrayList<String> constituencyMlaImage;
    private ArrayList<String> constituencyMlaname;
    private ArrayList<String> constituencyMlaParty;
    private ArrayList<String> constituencyMlaRating;

    public ConstituencyListDetailsAdapter(Context context, ArrayList<String> constituencyList, ArrayList<String> constituencyMlaImage, ArrayList<String> constituencyMlaname, ArrayList<String> constituencyMlaParty, ArrayList<String> constituencyMlaRating) {
        this.constituencyList=constituencyList;
        this.context=context;
        this.constituencyMlaImage=constituencyMlaImage;
        this.constituencyMlaname=constituencyMlaname;
        this.constituencyMlaParty=constituencyMlaParty;
        this.constituencyMlaRating=constituencyMlaRating;
    }


    @NonNull
    @Override
    public ConstituencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.constituencylist_details_rv, parent, false);
        return new ConstituencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConstituencyViewHolder holder, int position) {

        holder.constituency_mla_name.setText(constituencyMlaname.get(position));
        holder.txtMlaConstituency.setText(constituencyList.get(position));
        holder.txt_MlaRating_In_ConstituencyList.setText(constituencyMlaRating.get(position));

        Glide.with(context).load("https://upload.wikimedia.org/wikipedia/commons/e/e1/Nuernberg-fronfeste-und-kettensteg-v-O.jpg").into(holder.constituency_mla_image);

    }

    @Override
    public int getItemCount() {
        return constituencyList.size();
    }

    public class ConstituencyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView constituency_mla_name,txtMlaConstituency,txt_MlaRating_In_ConstituencyList;
        private ImageView constituency_mla_image;


        public ConstituencyViewHolder(View itemView) {
            super(itemView);

            constituency_mla_image=itemView.findViewById(R.id.constituency_mla_image);
            constituency_mla_name=itemView.findViewById(R.id.constituency_mla_name);
            txtMlaConstituency=itemView.findViewById(R.id.txtMlaConstituency);
            txt_MlaRating_In_ConstituencyList=itemView.findViewById(R.id.txt_MlaRating_In_ConstituencyList);
        }
    }
}
