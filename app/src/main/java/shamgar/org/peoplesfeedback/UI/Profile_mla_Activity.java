package shamgar.org.peoplesfeedback.UI;

import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.Adapters.ProfileImagesInListviewAdapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class Profile_mla_Activity extends AppCompatActivity {
    private String mlaName,mlaConstituency,tagRating,state,district;

    private TextView mlanametxt,txtmlaConstituency,followersForMlaCount,mlaRatingPercentage,overallVotesMla,overallRatingMla,mlaPartyName;
    private Button mlaFollowButton;
    private BubbleSeekBar mlaRatingSeekbar;
    private ImageView mla_gridViewImage;
    private RecyclerView profile_mla_gridImages_rv;
    private RatingBar mlaProfile_rating;
    private CircleImageView mlaProfileImage;
    private ImageView listViewImagesMLa;
    private NestedScrollView scrollViewMLa;

    private SharedPreferenceConfig sharedPreferenceConfig;
    private ArrayList<String> images;
    private ArrayList<String> postedOn;
    private ArrayList<String> lat;
    private ArrayList<String> lon;
    private ArrayList<String> tagId;
    private ArrayList<String> desc;
    private ArrayList<String> user;
    private ArrayList<String> keys;
    private Tag_Profile_Images_Adapter adapter;
    private ProfileImagesInListviewAdapter imagesInListviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_mla_);

        sharedPreferenceConfig=new SharedPreferenceConfig(this);

        images=new ArrayList<>();
        postedOn=new ArrayList<>();
        lat=new ArrayList<>();
        lon=new ArrayList<>();
        tagId=new ArrayList<>();
        desc=new ArrayList<>();
        user=new ArrayList<>();
        keys=new ArrayList<>();


        mlanametxt=findViewById(R.id.mlaDistrictName);
        txtmlaConstituency=findViewById(R.id.mlaName);
        mlaFollowButton=findViewById(R.id.mlaFollowButton);
        followersForMlaCount=findViewById(R.id.followersForMlaCount);
        mlaRatingSeekbar=findViewById(R.id.mlaRatingSeekbar);
        mlaRatingPercentage=findViewById(R.id.mlaRatingPercentage);
        overallRatingMla=findViewById(R.id.overallRatingMla);
        overallVotesMla=findViewById(R.id.overallVotesMla);
        mla_gridViewImage = findViewById(R.id.mla_grid_image);
        profile_mla_gridImages_rv=findViewById(R.id.profile_mla_gridImages_rv);
        mlaProfile_rating=findViewById(R.id.mlaProfile_rating);
        mlaProfileImage=findViewById(R.id.mlaProfileImage);
        mlaPartyName=findViewById(R.id.mlaPartyName);
        listViewImagesMLa=findViewById(R.id.listViewImagesMLa);
        scrollViewMLa=findViewById(R.id.scrollViewMLa);

        getSupportActionBar().setTitle("MLA profile");

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");

        mlaName=getIntent().getExtras().getString("mlaName");
        mlaConstituency=getIntent().getExtras().getString("mlaConstituency");
        state=getIntent().getExtras().getString("state");
        district=getIntent().getExtras().getString("district");

        txtmlaConstituency.setText(mlaConstituency);
        mlanametxt.setText(mlaName);

        scrollViewMLa.setNestedScrollingEnabled(false);
        scrollViewMLa.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mlaRatingSeekbar.correctOffsetWhenContainerOnScrolling();
            }
        });


        gettingMLAImage();
        gettingMlaTagedImages();
        //checking user is following or not
        Query postQuery =  FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("following")){
                    mlaFollowButton.setText("following");
                    mlaFollowButton.setTextColor(Color.parseColor("#000000"));
                }else {
                    mlaFollowButton.setText("follow");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addValueEventListener(valueEventListener);

        //getting overall rating and votes
        final Query postQuery1 =  FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child("Constituancy").child(mlaConstituency);
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                  String rating=dataSnapshot.child("rating").getValue().toString();
                  String Votes=dataSnapshot.child("votes").getValue().toString();
                    mlaProfile_rating.setRating(Integer.parseInt(rating)*5/100);
                  overallRatingMla.setText(rating+"%");
                  overallVotesMla.setText("Total Votes: "+Votes);
                }else {
                  Log.e("overall rating","data not exists");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery1.addValueEventListener(valueEventListener2);

        //implementing follow functionality for mla
        mlaFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlaFollowButton.getText().equals("follow")){
                    following();
                }else
                {
                    Toast.makeText(getApplicationContext(),"you are already following "+mlaName,Toast.LENGTH_SHORT).show();
                }

            }
        });

        //getting num of followers
        Query numofFol =  FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers");
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String numOfFollowers= String.valueOf(dataSnapshot.getChildrenCount());
                    // Toast.makeText(getApplicationContext(),numOfFollowers+" are following "+tag,Toast.LENGTH_SHORT).show();
                    followersForMlaCount.setText(numOfFollowers);
                }else {
                    Toast.makeText(getApplicationContext()," no followers for "+mlaName,Toast.LENGTH_SHORT).show();
                    followersForMlaCount.setText("0");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        numofFol.addValueEventListener(valueEventListener1);

        //implementing voting functionality
        mlaRatingSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tagRating=String.valueOf(progress);
                mlaRatingPercentage.setText("Rating Percentage "+tagRating+"%");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                Toast.makeText(getApplicationContext(),"stop touch ",Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(Profile_mla_Activity.this);
                builder.setCancelable(false);
                builder.setTitle("Your Rating is "+tagRating+"%")
                        .setMessage("Are you sure you want to submit this rating")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //posting Individual mla rating into fire base
                                postingMlaRating();
                                //Posting overall for mla


                                builder.setCancelable(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                builder.setCancelable(true);
                            }
                        })
                        .show();
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });


        //getting images from fire base
        mla_gridViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewImagesMLa.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_gray_24dp));
                mla_gridViewImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_primary_24dp));
                adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
                // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
                profile_mla_gridImages_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                profile_mla_gridImages_rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                profile_mla_gridImages_rv.setNestedScrollingEnabled(false);
            }
        });
        listViewImagesMLa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewImagesMLa.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
                mla_gridViewImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_gray_24dp));

                imagesInListviewAdapter=new ProfileImagesInListviewAdapter(getApplicationContext(),images,postedOn,lat,lon,tagId,user,desc,mlaName,mlaConstituency,keys);
                profile_mla_gridImages_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                profile_mla_gridImages_rv.setAdapter(imagesInListviewAdapter);
                imagesInListviewAdapter.notifyDataSetChanged();
                profile_mla_gridImages_rv.setNestedScrollingEnabled(false);
            }
        });


    }

    private void gettingMlaTagedImages()
    {
        Query taggedImages =  FirebaseDatabase.getInstance().getReference().child(NamesC.INDIA)
                .child(state).child(district).child("constituancy").child(mlaConstituency).child("PostID");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   // images.add(dataSnapshot.getKey());
                    for (DataSnapshot innersnap:dataSnapshot.getChildren()){

                        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.POSTS).child(innersnap.getKey());
                        ValueEventListener valueEventListener1=new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){

                                            keys.add(dataSnapshot.getKey());
                                            images.add(dataSnapshot.child("imageUrl").getValue().toString());
                                            postedOn.add(dataSnapshot.child("postedOn").getValue().toString());
                                            lat.add(dataSnapshot.child("latitude").getValue().toString());
                                            lon.add(dataSnapshot.child("longitude").getValue().toString());
                                            tagId.add(dataSnapshot.child("tagId").getValue().toString());
                                            desc.add(dataSnapshot.child("description").getValue().toString());
                                            user.add(dataSnapshot.child("user").getValue().toString());

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        query.addValueEventListener(valueEventListener1);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"  Images not found ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        taggedImages.addValueEventListener(valueEventListener);
    }

    private void following() {
        FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .child("following")
                .setValue("1")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mlaFollowButton.setTextColor(Color.parseColor("#000000"));
                            Toast.makeText(getApplicationContext(),"following",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private  void gettingMLAImage(){
        //getting num of followers
        Query numofFol =  FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName);
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String mlaUrl= dataSnapshot.child("image_url").getValue().toString();
                    String mlaParty= dataSnapshot.child("party").getValue().toString();
                    mlaPartyName.setText(mlaParty);
                     Toast.makeText(getApplicationContext(),mlaUrl,Toast.LENGTH_SHORT).show();
                    Glide.with(getApplicationContext())
                            .load(mlaUrl)
                            .error(R.drawable.ic_account_circle_black)
                            // read original from cache (if present) otherwise download it and decode it
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(mlaProfileImage);

                }else {
                    Toast.makeText(getApplicationContext(),mlaName+"  mla Image not found ",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        numofFol.addValueEventListener(valueEventListener1);


    }

    private void postingMlaRating() {
        String saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName)
                .child("Votes")
                .child(saveCurrentDate)
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .setValue(tagRating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Thank you for your rating", Toast.LENGTH_SHORT).show();
                    postingOverallRating();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error was occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postingOverallRating(){
        Query postQuery=FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName)
                .child("Votes");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    dataSnapshot.getChildrenCount();
                    int total = 0;
                    ArrayList<String> keysLists = new ArrayList<>();
                    for (DataSnapshot keys:dataSnapshot.getChildren()){
                        Log.e("keys",keys.getKey());
                        for (DataSnapshot innerChildren: keys.getChildren()){
                            Log.e("keys",innerChildren.getValue().toString());
                            keysLists.add(innerChildren.getValue().toString());
                            total = total+Integer.parseInt(innerChildren.getValue().toString());
                            Log.e("total", String.valueOf(total));
                        }
                    }
                    float average= (float) (total/keysLists.size());
                    Log.e("average", String.valueOf(average));
                    //posting average to respective tag
                    tagAverage(average,keysLists.size());
                }else {
                    Toast.makeText(getApplicationContext(),"Data not exists ",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        postQuery.addValueEventListener(valueEventListener);
    }

    private void tagAverage(float average, int size){
        Map<String, Object> updates = new HashMap<String,Object>();
        updates.put("rating",average);
        updates.put("votes", size);
        FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child("Constituancy").child(mlaConstituency)
                .updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Updated ", Toast.LENGTH_LONG).show();
            }
        });
//                .child("rating")
//                .setValue(average).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(getApplicationContext(),"rating updated",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(),"Some error was occurred while updating rating",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        FirebaseDatabase.getInstance().getReference().child("States")
//                .child(state).child("MLA").child("district").child(district).child("Constituancy").child(mlaConstituency)
//                .child("votes")
//                .setValue(size).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(getApplicationContext(),"votes updated",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(),"Some error was occurred while updating votes",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

}
