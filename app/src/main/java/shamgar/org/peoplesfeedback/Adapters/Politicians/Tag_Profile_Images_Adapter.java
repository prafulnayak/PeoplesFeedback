package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shamgar.org.peoplesfeedback.R;

public class Tag_Profile_Images_Adapter extends RecyclerView.Adapter<Tag_Profile_Images_Adapter.TagViewHolder> {
    Context context;

    public Tag_Profile_Images_Adapter(Context context) {
        this.context=context;

    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_tag_grid_images,parent,false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class TagViewHolder extends RecyclerView.ViewHolder{

        public TagViewHolder(View itemView) {
            super(itemView);
        }
    }
}
