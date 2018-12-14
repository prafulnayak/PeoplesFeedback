package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import shamgar.org.peoplesfeedback.R;

public class HorizantalPoliticianAdapter extends RecyclerView.Adapter<HorizantalPoliticianAdapter.HorizantalViewHolder> {

    private List<String> mDataList;
    private int mRowIndex = -1;
    public HorizantalPoliticianAdapter() {
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

    }

    @Override
    public int getItemCount() {
        return 5;
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

        public HorizantalViewHolder(View itemView) {
            super(itemView);
        }
    }
}
