package shamgar.org.peoplesfeedback.Adapters.Politicians;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.Profile_mla_Activity;

public class Tag_Profile_Images_Adapter extends RecyclerView.Adapter<Tag_Profile_Images_Adapter.TagViewHolder> {
    Context context;
    ArrayList<String> images;

    boolean isImageFitToScreen;


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
    public void onBindViewHolder(@NonNull final TagViewHolder holder, int position) {


        Glide.with(context)
                .load(images.get(position))
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imagesFromFirebase);

        holder.imagesFromFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  fullScreen(v);
            }
        });
    }

    private void fullScreen(View v) {
        int uiOptions=(v.getSystemUiVisibility());
        int newuioptions=uiOptions;
        boolean isImmrsiveModeEnabled=
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)==uiOptions);
        if (isImmrsiveModeEnabled){
            Log.e("mode","turning immersive mode enabled off");
        }else {
            Log.e("mode","turning immersive mode enabled on");
        }

        if (Build.VERSION.SDK_INT>=14){
            newuioptions ^=View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT>=16){
            newuioptions ^=View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT>=18){
            newuioptions ^=View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        v.setSystemUiVisibility(newuioptions);

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
