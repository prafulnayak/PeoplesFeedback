package shamgar.org.peoplesfeedback.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.R;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images;
    private ArrayList<String> names;

    public FollowersAdapter(Context context, ArrayList<String> images, ArrayList<String> names) {
        this.context = context;
        this.images = images;
        this.names = names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_custom_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.followerName.setText(names.get(position));
        Glide.with(context)
                .load(images.get(position))
                .error(R.drawable.ic_image_black_24dp)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.followerImage);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView followerImage;
        private TextView followerName,followerButton;
        public ViewHolder(View itemView) {
            super(itemView);
            followerButton=itemView.findViewById(R.id.followerButton);
            followerName=itemView.findViewById(R.id.followerName);
            followerImage=itemView.findViewById(R.id.followerImage);

        }
    }
}
