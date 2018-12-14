package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shamgar.org.peoplesfeedback.R;

public class ConstituencyListDetailsAdapter extends RecyclerView.Adapter<ConstituencyListDetailsAdapter.ConstituencyViewHolder> {
    private Context context;

    public ConstituencyListDetailsAdapter(Context context) {
        this.context=context;


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
        return 5;
    }

    public class ConstituencyViewHolder extends RecyclerView.ViewHolder
    {

        public ConstituencyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
