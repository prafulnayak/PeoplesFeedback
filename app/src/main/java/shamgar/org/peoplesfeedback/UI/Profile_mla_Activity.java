package shamgar.org.peoplesfeedback.UI;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class Profile_mla_Activity extends AppCompatActivity {
    private String mlaName,mlaConstituency,tagRating,state,district;

    private TextView mlanametxt,txtmlaConstituency,followersForMlaCount,mlaRatingPercentage,overallVotesMla,overallRatingMla;
    private Button mlaFollowButton;
    private SeekBar mlaRatingSeekbar;

    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_mla_);

        sharedPreferenceConfig=new SharedPreferenceConfig(this);

        mlanametxt=findViewById(R.id.mlaDistrictName);
        txtmlaConstituency=findViewById(R.id.mlaName);
        mlaFollowButton=findViewById(R.id.mlaFollowButton);
        followersForMlaCount=findViewById(R.id.followersForMlaCount);
        mlaRatingSeekbar=findViewById(R.id.mlaRatingSeekbar);
        mlaRatingPercentage=findViewById(R.id.mlaRatingPercentage);
        overallRatingMla=findViewById(R.id.overallRatingMla);
        overallVotesMla=findViewById(R.id.overallVotesMla);

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


    }

    private void following() {
        FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .child("following").setValue("1")
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
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName)
                .child("votes")
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
                .child("votes");
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
        FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child("Constituancy").child(mlaConstituency)
                .child("rating")
                .setValue(average).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"rating updated",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Some error was occurred while updating rating",Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child("Constituancy").child(mlaConstituency)
                .child("votes")
                .setValue(size).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"votes updated",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Some error was occurred while updating votes",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
