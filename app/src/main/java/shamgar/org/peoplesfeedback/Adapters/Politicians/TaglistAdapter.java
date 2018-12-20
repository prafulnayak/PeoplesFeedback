package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.Profile_TagActivity;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tags_profile=new Intent(context, Profile_TagActivity.class);
                context.startActivity(tags_profile);

            }
        });

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
