package shamgar.org.peoplesfeedback.Adapters;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private Context ctx;
    ArrayList<News> newsListR;
    private boolean  statusLike=false;
    private boolean statusShare=false;
    private boolean statusView=false;
    private DatabaseReference dbRefLike,dbRefShare;
    private FirebaseAuth mAuth;
    private SharedPreferenceConfig sharedPreference;
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
        sharedPreference = new SharedPreferenceConfig(ctx);

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
        holder.setLikeButton(news.getPostId());
        holder.posttimestamp.setText(news.getPostedDate());
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pm.setGravity(Gravity.END);
                }
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
                    dbRefShare.child(news.getPostId()).child("Share").push().child(mAuth.getCurrentUser().getUid()).setValue(sharedPreference.readPhoneNo());
                    statusShare=false;
                }

            }
        });

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(ctx, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(news.getLatitude(), news.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); //

        holder.postlocation.setText(address);


    }
    @Override
    public int getItemCount() {
        return newsListR.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView username,mlaname,mlaconstituency,posttimestamp,postTagname,postImageDescription;
        private TextView postmlarating_perce,postlocation,num_views,num_likes,num_shares;
        private ImageView userpostimage;
        private ImageButton imgview,imgshare,postsubmenuOptions;
        CircularImageView userimage, mlaimage;
        TextView imglike;
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
        public String getEmojiByUnicode(int unicode){
            return new String(Character.toChars(unicode));
        }

        public void setLikeButton(final String post_key)
        {
          databaseReference.child(post_key).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
                public void onDataChange(DataSnapshot dataSnapshot)
               {
                        if (dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid()))
                        {
                            imglike.setText(getEmojiByUnicode(0x270A));
                        }
                       else
                       {
                           imglike.setText(getEmojiByUnicode(0x1F44D));
                       }
                  }

                @Override
              public void onCancelled(DatabaseError databaseError) {
               }
           });

       }
        
    }
}
