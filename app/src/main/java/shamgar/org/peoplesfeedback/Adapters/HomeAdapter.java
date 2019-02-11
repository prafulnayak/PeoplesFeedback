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
import android.view.Menu;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.SpamModel;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> {

    private Context ctx;
    ArrayList<News> newsListR;
    private boolean  statusLike=false;
    private boolean statusShare=false;
    private boolean statusView=false;
    private DatabaseReference dbRefLike,dbRefShare,contectsref,chatRequestRef,notificationRef;
    private FirebaseAuth mAuth;
    private SharedPreferenceConfig sharedPreference;

    private String senderUserId,receiverUserId,current_state;
    private String SEND_REQUEST = "Invite to Chat";
    private String CANCEL_REQUEST = "Cancel Invitation";
    private boolean requestStatus = false;
    private Menu menu;

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
        contectsref= FirebaseDatabase.getInstance().getReference().child("contacts");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("chat request");
        notificationRef= FirebaseDatabase.getInstance().getReference().child("Notifications");
        sharedPreference = new SharedPreferenceConfig(ctx);
        current_state="new";
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {

        final News news = newsListR.get(position);
        holder.username.setText(news.getName());
        holder.mlaconstituency.setText(news.getConstituancy());
        holder.postTagname.setText("@"+news.getTag());
        holder.postImageDescription.setText(news.getDescription());
        holder.num_likes.setText(String.valueOf(news.getLikes()));
        holder.num_shares.setText(String.valueOf(news.getShares()));
        setLikeButton(news.getPostId(),holder);
        holder.posttimestamp.setText(news.getPostedDate());
        holder.num_views.setText(String.valueOf(news.getViews()));
        holder.mlaname.setText(news.getMla()+" (MLA)");;
        holder.postmlarating_perce.setText(String.valueOf(news.getVotePercentage()+"%"));
        Glide.with(ctx)
                .load(news.getMlaImageUrl())
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.mlaimage);
        Glide.with(ctx)
                .load(news.getUserUrl())
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userimage);
        Glide.with(ctx)
                .load(news.getImageUrl())
                .error(R.drawable.ic_image_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userpostimage);

        holder.imglikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusLike=true;
                if (statusLike) {
                    dbRefLike.child(news.getPostId()).child("Likes").child(sharedPreference.readPhoneNo().substring(3)).setValue("1");
                    statusLike = false;
                }
            }
        });

        //creating popup menu for the post
        holder.postsubmenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu pm = new PopupMenu(ctx, v);
                pm.getMenuInflater().inflate(R.menu.post_popup_menu, pm.getMenu());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pm.setGravity(Gravity.END);
                }
                manageChatRequests(news.getReceiverUserId(),pm);
                makeSpam(news.getPostId(),pm,news.getConstituancy(),sharedPreference.readState(),news.getTag());
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
                    dbRefShare.child(news.getPostId()).child("Share").push().setValue(sharedPreference.readPhoneNo());
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
        String knownName = addresses.get(0).getFeatureName();

        holder.postlocation.setText(address);


    }

    public void setLikeButton(final String post_key, final RecyclerViewHolder holder)
    {
        dbRefLike.child(post_key).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild(sharedPreference.readPhoneNo().substring(3)))
                {
                    holder.imglikes.setImageResource(R.drawable.ic_like_enble_2);
                }
                else
                {
                    holder.imglikes.setImageResource(R.drawable.ic_like_dis_2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void makeSpam(final String postId, PopupMenu pm, final String constituancy, final String state, final String tag) {
        pm.getMenu().findItem(R.id.spam).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(ctx,"spam",Toast.LENGTH_SHORT).show();
               // SpamModel model=new SpamModel(state,constituancy,sharedPreference.readPhoneNo(),tag);
                dbRefLike.child(postId).child("Spam")
                        .child(sharedPreference.readPhoneNo().substring(3))
                        .setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ctx,"Spammed the post ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
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
        private ImageButton imgview,imgshare,postsubmenuOptions,imglikes;
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
            imglikes=itemView.findViewById(R.id.imglikes);
            imgshare=itemView.findViewById(R.id.imgshares);
            imgview=itemView.findViewById(R.id.imgViews);
            postsubmenuOptions=itemView.findViewById(R.id.postsubmenuOptions);
        }
    }

    //sending chat requests

    private void manageChatRequests(final String receiverUserId, final PopupMenu pm)
    {
        this.senderUserId=mAuth.getCurrentUser().getPhoneNumber();
        this.receiverUserId=receiverUserId;
        //checking if the user allready sent request or not
        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserId)) {
                            String request_type=dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                            if (request_type.equals("sent")) {
                                current_state="request sent";
                                pm.getMenu().findItem(R.id.invite).setTitle("Cancel Request");
                            }
                            else if (request_type.equals("received")) {
                                current_state="request received";
                            }
                        }
                        else {
                            contectsref.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiverUserId))
                                                current_state="friends";
                                           // pm.getMenu().findItem(R.id.invite).setVisible(false);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        if (!senderUserId.equals(receiverUserId)) {
            pm.getMenu().findItem(R.id.invite).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (current_state.equals("new")) {
                                sendchatREquest(senderUserId,pm);
                            }
                            if (current_state.equals("request sent")) {
                                cancelChatRequest(pm);
                            }

                            return true;
                }
            });
        }
        else {
            pm.getMenu().findItem(R.id.invite).setVisible(false);
        }

    }
    private void sendchatREquest(final String senderUserId, final PopupMenu pm) {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                //push notifications using firebase
                                                HashMap<String,String> chatNotificationsMap=new HashMap<>();
                                                chatNotificationsMap.put("from",senderUserId);
                                                chatNotificationsMap.put("type","request");

                                                notificationRef.child(receiverUserId.substring(3)).push()
                                                        .setValue(chatNotificationsMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    pm.getMenu().findItem(R.id.invite).setTitle("Cancel request");
                                                                    Toast.makeText(ctx,"request sent",Toast.LENGTH_LONG).show();
                                                                    current_state="request sent";
                                                                }

                                                            }
                                                        });


                                            }
                                        }
                                    });
                        }
                        else {
                            String message=task.getException().toString();
                            Toast.makeText(ctx,"Exception "+message,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void cancelChatRequest(final PopupMenu pm) {
        chatRequestRef.child(this.senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(HomeAdapter.this.senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                pm.getMenu().findItem(R.id.invite).setTitle("Invite");
                                                Toast.makeText(ctx,"Request cancelled",Toast.LENGTH_LONG).show();
                                                current_state="new";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }



}
