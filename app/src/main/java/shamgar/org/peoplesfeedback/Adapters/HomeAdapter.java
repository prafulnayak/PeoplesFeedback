package shamgar.org.peoplesfeedback.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private Context ctx;
    ArrayList<News> newsListR;
    public HomeAdapter(ArrayList<News> newsList, Context newsFragment) {
        newsListR = newsList;
        ctx = newsFragment;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeed_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        final News news = newsListR.get(position);

        holder.mlaconstituency.setText(news.getConstituancy());
        holder.postTagname.setText(news.getTag());

        Glide.with(ctx)
                .load(news.getImageUrl())
                .error(R.drawable.sai)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userpostimage);

    }

    @Override
    public int getItemCount() {
        return newsListR.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView username,mlaname,mlaconstituency,posttimestamp,postTagname,postImageDescription;
        private TextView postmlarating_perce,postlocation,num_views,num_likes,num_shares;
        private ImageView userpostimage;
        CircularImageView userimage, mlaimage;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            userimage=itemView.findViewById(R.id.postuserImage);
            mlaimage=itemView.findViewById(R.id.postmlaImage);
            username=itemView.findViewById(R.id.postusername);
            mlaname=itemView.findViewById(R.id.postmlaname);
            mlaconstituency=itemView.findViewById(R.id.postmla_consti);
            posttimestamp=itemView.findViewById(R.id.posttimestamp);
            postTagname=itemView.findViewById(R.id.postTagname);
            postImageDescription=itemView.findViewById(R.id.postImageDescription);
            postmlarating_perce=itemView.findViewById(R.id.postmlarating_perce);
            postlocation=itemView.findViewById(R.id.postlocation);
            num_views=itemView.findViewById(R.id.num_views);
            num_likes=itemView.findViewById(R.id.num_likes);
            num_shares=itemView.findViewById(R.id.num_shares);
            userpostimage=itemView.findViewById(R.id.userpost);
        }
    }
}
