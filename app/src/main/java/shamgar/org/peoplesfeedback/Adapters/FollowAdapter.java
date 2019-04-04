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

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewwHolder> {

    private Context context;
    private ArrayList<String> images;
    private ArrayList<String> names;

    public FollowAdapter(Context context, ArrayList<String> images, ArrayList<String> names) {
        this.context = context;
        this.images = images;
        this.names = names;
    }

    @NonNull
    @Override
    public ViewwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_custom_layout,parent,false);

        return new ViewwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewwHolder holder, int position) {
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

    public class ViewwHolder extends RecyclerView.ViewHolder{

        private CircleImageView followerImage;
        private TextView followerName,followerButton;

        public ViewwHolder(View itemView) {
            super(itemView);

            followerButton=itemView.findViewById(R.id.followingButton);
            followerName=itemView.findViewById(R.id.follower_name);
            followerImage=itemView.findViewById(R.id.followingImage);
        }
    }
}
