package shamgar.org.peoplesfeedback.Adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.Contants;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewholder>
{
    private ArrayList<String> url=new ArrayList<>();
    private ArrayList<String> description=new ArrayList<>();
    private ArrayList<String> tag=new ArrayList<>();
    private ArrayList<String> mla=new ArrayList<>();
    private ArrayList<String> lat=new ArrayList<>();
    private ArrayList<String> lon=new ArrayList<>();
    private ArrayList<String> postedby=new ArrayList<>();
    private ArrayList<String> status=new ArrayList<>();
    private ArrayList<String> publishedon=new ArrayList<>();
    private ArrayList<String> constituency=new ArrayList<>();
    private ArrayList<String> mlaid=new ArrayList<>();
    private ArrayList<String> userImage=new ArrayList<>();
    private ArrayList<String> mlaImage=new ArrayList<>();
    private ArrayList<String> username=new ArrayList<>();
    private ArrayList<String> rating=new ArrayList<>();

    private Context context;


    public PostsAdapter(Context context,ArrayList<String> url, ArrayList<String> description, ArrayList<String> tag, ArrayList<String> mla, ArrayList<String> lat, ArrayList<String> lon, ArrayList<String> postedby, ArrayList<String> status, ArrayList<String> publishedon, ArrayList<String> constituency, ArrayList<String> mlaid, ArrayList<String> userImage, ArrayList<String> mlaImage, ArrayList<String> username, ArrayList<String> rating) {
        this.url = url;
        this.description = description;
        this.tag = tag;
        this.mla = mla;
        this.lat = lat;
        this.lon = lon;
        this.postedby = postedby;
        this.status = status;
        this.publishedon = publishedon;
        this.constituency = constituency;
        this.mlaid = mlaid;
        this.userImage = userImage;
        this.mlaImage = mlaImage;
        this.username = username;
        this.rating = rating;
        this.context = context;
    }

    public PostsAdapter()
    {

    }

    @NonNull
    @Override
    public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeed_layout,parent,false);
        return new PostViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewholder holder, int i)
    {

        holder.mlaconstituency.setText(String.format("Constituency: %s", constituency.get(i)));
        holder.postTagname.setText(String.format("@%s", tag.get(i)));
        holder.posttimestamp.setText(publishedon.get(i));
//            userpostimage.setImageBitmap(Bitmap.createBitmap(R.drawable.user));

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        Glide.with(context)
                .load(url.get(i))
                .placeholder(circularProgressDrawable)
                .error(R.drawable.sai)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userpostimage);

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

//        try {
//            addresses = geocoder.getFromLocation(lat.get(i), lon.get(i), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//        if (lat==0 || lon==0) {
//            holder.postlocation.setText("location unknown");
//        }
//        else
//            holder.postlocation.setText(addresses.get(0).getAddressLine(0));

    }

    @Override
    public int getItemCount()
    {
        return constituency.size();
    }

    public class PostViewholder extends RecyclerView.ViewHolder
    {
        private TextView username,mlaname,mlaconstituency,posttimestamp,postTagname,postImageDescription;
        private TextView postmlarating_perce,postlocation,num_views,num_likes,num_shares;
        private ImageView userpostimage;
        CircularImageView userimage, mlaimage;
        public PostViewholder(@NonNull View itemView)
        {
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
