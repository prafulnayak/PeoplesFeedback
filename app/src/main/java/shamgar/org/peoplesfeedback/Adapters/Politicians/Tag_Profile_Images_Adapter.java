package shamgar.org.peoplesfeedback.Adapters.Politicians;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.R;

public class Tag_Profile_Images_Adapter extends RecyclerView.Adapter<Tag_Profile_Images_Adapter.TagViewHolder> {
    Context context;
    ArrayList<String> images;


    public Tag_Profile_Images_Adapter(Context context, ArrayList<String> images) {
        this.context=context;
        this.images=images;


    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_tag_grid_images,parent,false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {

        
        Glide.with(context).load(images.get(position)).placeholder(R.drawable.profile).into(holder.imagesFromFirebase);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagesFromFirebase;
        public TagViewHolder(View itemView) {
            super(itemView);
            imagesFromFirebase=itemView.findViewById(R.id.imagesFromFirebase);
        }
    }
}
