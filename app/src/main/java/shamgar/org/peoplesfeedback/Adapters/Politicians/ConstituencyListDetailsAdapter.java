package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.R;

public class ConstituencyListDetailsAdapter extends RecyclerView.Adapter<ConstituencyListDetailsAdapter.ConstituencyViewHolder> {
    private Context context;
    private ArrayList<String> constituencyList;

    public ConstituencyListDetailsAdapter(Context context, ArrayList<String> constituencyList) {
        this.context=context;
        this.constituencyList=constituencyList;


    }

    @NonNull
    @Override
    public ConstituencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.constituencylist_details_rv, parent, false);
        return new ConstituencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConstituencyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return constituencyList.size();
    }

    public class ConstituencyViewHolder extends RecyclerView.ViewHolder
    {

        public ConstituencyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
