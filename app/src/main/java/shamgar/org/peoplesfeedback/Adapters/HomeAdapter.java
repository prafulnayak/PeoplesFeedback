package shamgar.org.peoplesfeedback.Adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private Context ctx;
    ArrayList<News> newsListR;
    private boolean statusLike=false;
    private DatabaseReference dbRefLike;
    private FirebaseAuth mAuth;
    public HomeAdapter(ArrayList<News> newsList, Context newsFragment)
    {
        this.newsListR = newsList;
        this.ctx = newsFragment;
    }



    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeed_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();
        dbRefLike= FirebaseDatabase.getInstance().getReference().child("Posts");
        dbRefLike.keepSynced(true);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        final News news = newsListR.get(position);

        holder.mlaconstituency.setText(news.getConstituancy());
        holder.postTagname.setText(news.getTag());
        holder.postImageDescription.setText(news.getDescription());
        //method for status of the post i.e current user liked or not
        holder.setLikeButton(news.getPostId());
        //method for num of likes got for the particular post
        holder.setNumLikes(news.getPostId());

        Glide.with(ctx)
                .load(news.getImageUrl())
                .error(R.drawable.sai)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userpostimage);

        holder.imglike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusLike=true;
                    dbRefLike.child(news.getPostId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (statusLike) {
                                if (dataSnapshot.child("Likes").hasChild(mAuth.getCurrentUser().getUid())) {
                                    dbRefLike.child(news.getPostId()).child("Likes").child(mAuth.getCurrentUser().getUid()).removeValue();
                                    statusLike = false;
                                   // Toast.makeText(ctx,"disliked",Toast.LENGTH_LONG).show();
                                } else {
                                    dbRefLike.child(news.getPostId()).child("Likes").child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                    statusLike = false;
                                   // Toast.makeText(ctx,"liked",Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            }
        });

        //creating popup menu for the post
        holder.postsubmenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(ctx, v);
                pm.getMenuInflater().inflate(R.menu.post_popup_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
                pm.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return newsListR.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView username,mlaname,mlaconstituency,posttimestamp,postTagname,postImageDescription;
        private TextView postmlarating_perce,postlocation,num_views,num_likes,num_shares;
        private ImageView userpostimage;
        private ImageButton imglike,imgview,imgshare,postsubmenuOptions;
        CircularImageView userimage, mlaimage;

        private DatabaseReference databaseReferenceLike;
        private FirebaseAuth firebaseAuth;

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
            imglike=itemView.findViewById(R.id.imglikes);
            imgshare=itemView.findViewById(R.id.imgshares);
            imgview=itemView.findViewById(R.id.imgViews);
            postsubmenuOptions=itemView.findViewById(R.id.postsubmenuOptions);
            databaseReferenceLike=FirebaseDatabase.getInstance().getReference().child("Posts");
            firebaseAuth=FirebaseAuth.getInstance();
            databaseReferenceLike.keepSynced(true);
        }

        public void setLikeButton(final String post_key)
        {
            databaseReferenceLike.child(post_key).child("Likes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid()))
                    {
                        imglike.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else
                    {
                      imglike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setNumLikes(final String postId)
        {
            databaseReferenceLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                   int likes = (int) dataSnapshot.child(postId).child("Likes").getChildrenCount();
                    num_likes.setText((Integer.toString(likes)));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
