package shamgar.org.peoplesfeedback.Adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;

public class ProfileImagesInListviewAdapter extends RecyclerView.Adapter<ProfileImagesInListviewAdapter.ProfileImagesViewHolder> {

    private Context context;
    private ArrayList<String> images;
    private ArrayList<String> postedOn;
    private ArrayList<String> lat;
    private ArrayList<String> lon;
    private ArrayList<String> tagId;
    private ArrayList<String> desc;
    private ArrayList<String> user;
    private ArrayList<String> keys;
    private String mlaNamr;
    private String mlaConstituency;

    public ProfileImagesInListviewAdapter(Context context, ArrayList<String> images, ArrayList<String> postedOn, ArrayList<String> lat, ArrayList<String> lon, ArrayList<String> tagId, ArrayList<String> user, ArrayList<String> desc, String mlaName, String mlaConstituency, ArrayList<String> keys) {
        this.context=context;
        this.images=images;
        this.postedOn=postedOn;
        this.lat=lat;
        this.lon=lon;
        this.tagId=tagId;
        this.desc=desc;
        this.user=user;
        this.mlaNamr=mlaName;
        this.mlaConstituency=mlaConstituency;
        this.keys=keys;
    }

    @NonNull
    @Override
    public ProfileImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_images_list_view,parent,false);
        return new ProfileImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileImagesViewHolder holder, int position) {

        Glide.with(context)
                .load(images.get(position))
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userpostimage);
        //holder.num_likes.setText(Likes.get(position));
      //  holder.num_shares.setText(shares.get(position));
       // holder.num_views.setText(views.get(position));
        holder.postTagname.setText(tagId.get(position));
        holder.posttimestamp.setText(postedOn.get(position));
        holder.postImageDescription.setText(desc.get(position));
        holder.mlaname.setText(mlaNamr);
        holder.mlaconstituency.setText(mlaConstituency);

        gettingUserNameAndImage(holder.username,holder.userimage,user.get(position));
        gettingLikesAndViews(holder.num_views,holder.num_shares,holder.num_likes,keys.get(position));
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        double lati= Double.parseDouble(lat.get(position));
        double longi= Double.parseDouble(lon.get(position));
        try {
            addresses = geocoder.getFromLocation(lati,longi , 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        holder.postlocation.setText(address);

    }

    private void gettingLikesAndViews(final TextView num_views, final TextView num_shares, final TextView num_likes, String s) {

        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.POSTS).child(s);
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Likes")){
                        num_likes.setText(String.valueOf(dataSnapshot.child("Likes").getChildrenCount()));
                    }
                    if (dataSnapshot.hasChild("Share")){
                        num_shares.setText(String.valueOf(dataSnapshot.child("Share").getChildrenCount()));
                    }
                    if (dataSnapshot.hasChild("View")){
                        num_views.setText(String.valueOf(dataSnapshot.child("View").getChildrenCount()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void gettingUserNameAndImage(final TextView username, final CircularImageView userimage, String s) {

        Query query= FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(s.substring(3));
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    username.setText(dataSnapshot.child("name").getValue().toString());
                    Glide.with(context)
                            .load(dataSnapshot.child("desc").getValue().toString())
                            .error(R.drawable.ic_account_circle_black)
                            // read original from cache (if present) otherwise download it and decode it
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(userimage);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addValueEventListener(valueEventListener);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ProfileImagesViewHolder extends RecyclerView.ViewHolder{
        private TextView username,mlaname,mlaconstituency,posttimestamp,postTagname,postImageDescription;
        private TextView postmlarating_perce,postlocation,num_views,num_likes,num_shares;
        private ImageView userpostimage;
        private ImageButton imgview,imgshare,postsubmenuOptions,imglikes,imgsharesWhatapp;
        CircularImageView userimage, mlaimage;
        public ProfileImagesViewHolder(View itemView) {
            super(itemView);
            userimage=itemView.findViewById(R.id.postuserImage);
            mlaimage=itemView.findViewById(R.id.postmlaImage);
            username=itemView.findViewById(R.id.postusername);
            mlaname=itemView.findViewById(R.id.postmlaname);
            mlaconstituency=itemView.findViewById(R.id.postmla_consti);
            posttimestamp=itemView.findViewById(R.id.posttimestamp);
            postTagname=itemView.findViewById(R.id.postTagname);
            postImageDescription=itemView.findViewById(R.id.postImageDescription);
            postlocation=itemView.findViewById(R.id.postlocation);
            num_views=itemView.findViewById(R.id.num_views);
            num_likes=itemView.findViewById(R.id.num_likes);
            num_shares=itemView.findViewById(R.id.num_shares);
            userpostimage=itemView.findViewById(R.id.userpost);
            imglikes=itemView.findViewById(R.id.imglikes);
            imgshare=itemView.findViewById(R.id.imgshares);
            imgview=itemView.findViewById(R.id.imgViews);
            postsubmenuOptions=itemView.findViewById(R.id.postsubmenuOptions);
        }
    }
}