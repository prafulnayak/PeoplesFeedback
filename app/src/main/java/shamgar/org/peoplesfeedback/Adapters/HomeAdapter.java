package shamgar.org.peoplesfeedback.Adapters;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.Toolbar;

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

import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.Model.News;
import shamgar.org.peoplesfeedback.Model.SpamModel;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Services.BackGroundServices;
import shamgar.org.peoplesfeedback.UI.Profile_mla_Activity;
import shamgar.org.peoplesfeedback.UI.User_profile_Activity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.RecyclerViewHolder> implements NamesC {
    static int jobId = 0;
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

    private JobScheduler jobScheduler;

    public HomeAdapter(ArrayList<News> newsList, Context newsFragment)
    {
        this.newsListR = newsList;
        this.ctx = newsFragment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler) ctx.getSystemService(JOB_SCHEDULER_SERVICE);
        }
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
        boolean liked = true;
        final News news = newsListR.get(position);
        holder.username.setText(news.getName());
        holder.mlaconstituency.setText(news.getConstituancy());
        holder.postTagname.setText("@"+news.getTag());
        holder.postImageDescription.setText(news.getDescription());
        holder.num_likes.setText(String.valueOf(news.getLikes()));
        holder.num_shares.setText(String.valueOf(news.getShares()));
//        holder.imglikes.setEnabled(true);
        setLikeButton(news,holder);
//        setClickListnerFromFirebase(news,position);
        holder.posttimestamp.setText(news.getPostedDate());
        holder.num_views.setText(String.valueOf(news.getViews()));
        holder.mlaname.setText(news.getMla());
        holder.mlaname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mlaProfile=new Intent(ctx, Profile_mla_Activity.class);
                mlaProfile.putExtra("mlaName",news.getMla());
                mlaProfile.putExtra("mlaConstituency",news.getConstituancy());
                mlaProfile.putExtra("state",news.getState());
                mlaProfile.putExtra("district",news.getDistrict());
                ctx.startActivity(mlaProfile);

            }
        });
        holder.mlaimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mlaProfile=new Intent(ctx, Profile_mla_Activity.class);
                mlaProfile.putExtra("mlaName",news.getMla());
                mlaProfile.putExtra("mlaConstituency",news.getConstituancy());
                mlaProfile.putExtra("state",news.getState());
                mlaProfile.putExtra("district",news.getDistrict());
                ctx.startActivity(mlaProfile);
            }
        });
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
        holder.userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Log.e("user num",news.getPostedBy());
                Intent profile=new Intent(ctx, User_profile_Activity.class);
                profile.putExtra("mobile",news.getPostedBy());
                ctx.startActivity(profile);
            }
        });
        Glide.with(ctx)
                .load(news.getImageUrl())
                .error(R.drawable.ic_image_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.userpostimage);



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
        holder.imgsharesWhatapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusShare=true;
                if (statusShare) {

                    if(!news.getImageUrl().isEmpty() && news.getImageUrl() != null){
                        Bitmap bitmap = getBitmapFromView(holder.userpostimage);
                        try {
                            File file = new File(ctx.getExternalCacheDir(),"logicchip.png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();


//                            file.setReadable(true, false);
                            final Intent intent = new Intent(Intent.ACTION_SEND);
                            Uri apkURI = FileProvider.getUriForFile(
                                    ctx,
                                    ctx.getApplicationContext()
                                            .getPackageName() + ".provider", file);
                            intent.setDataAndType(apkURI, Intent.normalizeMimeType("image/png"));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setPackage("com.whatsapp");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_TEXT, news.getDescription()+news.getAddress()+news.getMla());
                            intent.putExtra(Intent.EXTRA_STREAM, apkURI);
//                            intent.setType("image/png");
                          //  ctx.startActivity(Intent.createChooser(intent, "Share image via"));
                            ctx.startActivity(intent);
                            news.setShares(news.getShares()+1);
                            notifyDataSetChanged();
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(ctx,"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent shareIntent = new Intent();
//                        shareIntent.setAction(Intent.ACTION_SEND);
//
//                        //Add text and then Image URI
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, news.getDescription()+"\n"+news.getImageUrl() + "\n");
//                        shareIntent.setType("text/plain");
//
//
//                        try {
//                            ctx.startActivity(shareIntent);
//                        } catch (android.content.ActivityNotFoundException ex) {
//
//                            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//                        }
                    }else {
                        Toast.makeText(ctx, "Unable to share", Toast.LENGTH_SHORT).show();
                    }
                   // Toast.makeText(ctx, "shared", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        scheduleJob(news.getPostId(),sharedPreference.readPhoneNo(),Share);
                    }else {
                        dbRefShare.child(news.getPostId()).child(Share).push().setValue(sharedPreference.readPhoneNo());
                    }

                    statusShare=false;
                }

            }
        });

        //listener for sharing posts
        holder.imgshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusShare=true;
                if (statusShare) {

                    if(!news.getImageUrl().isEmpty() && news.getImageUrl() != null){
                        Bitmap bitmap = getBitmapFromView(holder.userpostimage);
                        try {
                            File file = new File(ctx.getExternalCacheDir(),"logicchip.png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();


//                            file.setReadable(true, false);
                            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            Uri apkURI = FileProvider.getUriForFile(
                                    ctx,
                                    ctx.getApplicationContext()
                                            .getPackageName() + ".provider", file);
                            intent.setDataAndType(apkURI, Intent.normalizeMimeType("image/png"));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Intent.EXTRA_TEXT, news.getDescription()+news.getAddress()+news.getMla());
                            intent.putExtra(Intent.EXTRA_STREAM, apkURI);
//                            intent.setType("image/png");
                            //  ctx.startActivity(Intent.createChooser(intent, "Share image via"));
                            ctx.startActivity(intent);

                            news.setShares(news.getShares()+1);
                            notifyDataSetChanged();

                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ctx,"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent shareIntent = new Intent();
//                        shareIntent.setAction(Intent.ACTION_SEND);
//
//                        //Add text and then Image URI
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, news.getDescription()+"\n"+news.getImageUrl() + "\n");
//                        shareIntent.setType("text/plain");
//
//
//                        try {
//                            ctx.startActivity(shareIntent);
//                        } catch (android.content.ActivityNotFoundException ex) {
//
//                            Toast.makeText(ctx, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//                        }
                    }else {
                        Toast.makeText(ctx, "Unable to share", Toast.LENGTH_SHORT).show();
                    }
                   // Toast.makeText(ctx, "shared", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        scheduleJob(news.getPostId(),sharedPreference.readPhoneNo(),Share);
                    }else {
                        dbRefShare.child(news.getPostId()).child(Share).push().setValue(sharedPreference.readPhoneNo());
                    }
                    statusShare=false;
                }

            }
        });

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(ctx, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(news.getLatitude(), news.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            holder.postlocation.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }




    }



    private Bitmap getBitmapFromView(ImageView view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public void setLikeButton(final News news, final RecyclerViewHolder holder)
    {
        dbRefLike.child(news.getPostId()).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    holder.imglikes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.imglikes.setImageResource(R.drawable.ic_like_enble_2);
                                holder.imglikes.setEnabled(false);
                                news.setLikes(news.getLikes()+1);
                                notifyDataSetChanged();
                                scheduleJob(news.getPostId(),sharedPreference.readPhoneNo().substring(3),"Likes");
                            }else {
                                dbRefLike.child(news.getPostId()).child("Likes").child(sharedPreference.readPhoneNo().substring(3)).setValue("1");
                            }



                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob(String postId, String phoneNo, String typePost) {
        ComponentName serviceName = new ComponentName(ctx.getPackageName(),
                BackGroundServices.class.getName());

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("postId",postId);
        bundle.putString("phoneNo", phoneNo);
        bundle.putString("typePost",typePost);
        jobId++;
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(bundle);
//                .setRequiresDeviceIdle(false)
//                .setRequiresCharging(true);

        JobInfo myJobInfo = builder.build();
        jobScheduler.schedule(myJobInfo);
//        Toast.makeText(ctx, R.string.job_scheduled, Toast.LENGTH_SHORT)
//                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void cancelJob(){
        if (jobScheduler!=null){
            jobScheduler.cancelAll();
            jobScheduler = null;
            Toast.makeText(ctx, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void makeSpam(final String postId, PopupMenu pm, final String constituancy, final String state, final String tag) {
        pm.getMenu().findItem(R.id.spam).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
              //  Toast.makeText(ctx,"spam",Toast.LENGTH_SHORT).show();
               // SpamModel model=new SpamModel(state,constituancy,sharedPreference.readPhoneNo(),tag);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scheduleJob(postId,sharedPreference.readPhoneNo().substring(3),"Spam");
                }else {
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
                }

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
        private ImageButton imgview,imgshare,postsubmenuOptions,imglikes,imgsharesWhatapp;
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
            imgsharesWhatapp=itemView.findViewById(R.id.imgshares_Whatapp);
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
