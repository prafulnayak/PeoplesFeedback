package shamgar.org.peoplesfeedback.UI;

import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class Profile_mla_Activity extends AppCompatActivity {
    private String mlaName,mlaConstituency,tagRating,state,district;

    private TextView mlanametxt,txtmlaConstituency,followersForMlaCount,mlaRatingPercentage,overallVotesMla,overallRatingMla;
    private Button mlaFollowButton;
    private SeekBar mlaRatingSeekbar;
    private ImageView mla_gridViewImage;
    private RecyclerView profile_mla_gridImages_rv;

    private SharedPreferenceConfig sharedPreferenceConfig;
    private ArrayList<String> images;
    private Tag_Profile_Images_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_mla_);

        sharedPreferenceConfig=new SharedPreferenceConfig(this);

        images=new ArrayList<>();
        images.add("https://www.pixelstalk.net/wp-content/uploads/2016/06/HD-images-of-nature-download.jpg");
        images.add("http://2.bp.blogspot.com/-q2tFyftUy9o/UkPs5Oofa7I/AAAAAAAAAxE/dyQCGMfQ6rg/s1600/full-hd-nature-wallpapers-free-downloads-for-laptop-06.jpg");
        images.add("https://hdwallpaper20.com/wp-content/uploads/2016/11/wallpaper-of-nature-free-Download1-1.jpg");
        images.add("http://3.bp.blogspot.com/-8Ow2DuXPAQU/UnyVDO5N7eI/AAAAAAAAAGw/dEda6GiX4CE/s1600/Free+Download+Nature+Wallpapers.jpg");
        images.add("http://2.bp.blogspot.com/-q2tFyftUy9o/UkPs5Oofa7I/AAAAAAAAAxE/dyQCGMfQ6rg/s1600/full-hd-nature-wallpapers-free-downloads-for-laptop-06.jpg");
        images.add("https://hdwallpaper20.com/wp-content/uploads/2016/11/wallpaper-of-nature-free-Download1-1.jpg");
        images.add("https://www.pixelstalk.net/wp-content/uploads/2016/06/HD-images-of-nature-download.jpg");
        images.add("http://3.bp.blogspot.com/-8Ow2DuXPAQU/UnyVDO5N7eI/AAAAAAAAAGw/dEda6GiX4CE/s1600/Free+Download+Nature+Wallpapers.jpg");

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

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mlaName=getIntent().getExtras().getString("mlaName");
        mlaConstituency=getIntent().getExtras().getString("mlaConstituency");
        state=getIntent().getExtras().getString("state");
        district=getIntent().getExtras().getString("district");

        txtmlaConstituency.setText(mlaConstituency);
        mlanametxt.setText(mlaName);


        //checking user is following or not
        Query postQuery =  FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("following")){
                    mlaFollowButton.setText("following");
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
        Query postQuery1 =  FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child("Constituancy").child(mlaConstituency);
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                  String rating=dataSnapshot.child("rating").getValue().toString();
                  String Votes=dataSnapshot.child("votes").getValue().toString();

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
        mlaRatingSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tagRating=String.valueOf(progress);
                mlaRatingPercentage.setText("Rating Percentage "+tagRating+"%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
        });

        //getting images from fire base
        mla_gridViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
                StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                profile_mla_gridImages_rv.setLayoutManager(staggeredGridLayoutManager);
                profile_mla_gridImages_rv.setAdapter(adapter);
                profile_mla_gridImages_rv.setNestedScrollingEnabled(false);
            }
        });


    }

    private void following() {
        FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .setValue("1")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"following",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
