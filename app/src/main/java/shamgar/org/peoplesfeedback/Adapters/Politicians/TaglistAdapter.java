package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shamgar.org.peoplesfeedback.R;

public class TaglistAdapter extends RecyclerView.Adapter {

    private Context context;

    public TaglistAdapter(Context context) {
        this.context=context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.taglist_items_horizantal, parent, false);
        return new TaglistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class TaglistViewHolder extends RecyclerView.ViewHolder
    {

        public TaglistViewHolder(View itemView) {
            super(itemView);
        }
    }
}
