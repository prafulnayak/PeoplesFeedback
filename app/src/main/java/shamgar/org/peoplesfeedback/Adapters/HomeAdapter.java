package shamgar.org.peoplesfeedback.Adapters;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private Context ctx;
    ArrayList<News> newsListR;
    private boolean  statusLike=false;
    private boolean statusShare=false;
    private DatabaseReference dbRefLike,dbRefShare;
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
        dbRefShare= FirebaseDatabase.getInstance().getReference().child("Posts");
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
        holder.num_likes.setText(String.valueOf(news.getLikes()));
        holder.num_shares.setText(String.valueOf(news.getShares()));
       // holder.setLikeButton(news.getPostId());
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
                if (statusLike) {
                    dbRefLike.child(news.getPostId()).child("Likes").child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                    statusLike = false;
                }
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

        //listener for sharing posts
        holder.imgshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusShare=true;
                if (statusShare) {
                    Toast.makeText(ctx, "shared", Toast.LENGTH_LONG).show();
                    dbRefShare.child(news.getPostId()).child("Share").push().child(mAuth.getCurrentUser().getUid()).setValue(news.getConstituancy());
                    statusShare=false;

                }

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
        private DatabaseReference databaseReference;
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
            firebaseAuth=FirebaseAuth.getInstance();
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Posts");

        }

//        public void setLikeButton(final String post_key)
//        {
//            databaseReference.child(post_key).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid()))
//                    {
//                        imglike.setImageResource(R.drawable.ic_favorite_black_24dp);
//                    }
//                    else
//                    {
//                      imglike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//        public void setNumLikes(final String postId)
//        {
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot)
//                {
//                   int likes = (int) dataSnapshot.child(postId).child("Likes").getChildrenCount();
//                    num_likes.setText((Integer.toString(likes)));
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
//
//        public void setShareButton(String postId, String constituancy)
//        {
//            databaseReference.child(postId).child("Share").orderByChild(firebaseAuth.getCurrentUser().getUid()).equalTo(constituancy).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.))
//                        imgshare.setImageResource(R.drawable.ic_share_black1_24dp);
//                    else
//                        imgshare.setImageResource(R.drawable.ic_share_black_24dp);
//
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//        public void setNumOfshares(final String postId)
//        {
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot)
//                {
//                    int likes = (int) dataSnapshot.child(postId).child("Share").getChildrenCount();
//                    num_shares.setText((Integer.toString(likes)));
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }
    }
}
