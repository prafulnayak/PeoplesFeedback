package shamgar.org.peoplesfeedback.UI;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
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
import java.util.List;

import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.Adapters.Politicians.ViewAllPoliticiansAdapter;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.widget.LinearLayout.VERTICAL;

public class Profile_TagActivity extends AppCompatActivity {

    private ImageButton tag_gridViewImages;
    private TextView tagName,tagDistrictName,followersForTags,followersForTagsCount,tagRatingPercentage;
    private Button tagFollowButton;
    private SeekBar profileTagRating;

    private Tag_Profile_Images_Adapter adapter;
    private RecyclerView recyclerView;
    private String district,tag,tagRating,state;
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_profile_updated);

        sharedPreferenceConfig=new SharedPreferenceConfig(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        district=getIntent().getExtras().getString("district");
        tag=getIntent().getExtras().getString("tag");
        state=getIntent().getExtras().getString("state");

        tag_gridViewImages=(ImageButton)findViewById(R.id.tag_gridViewImages);
        recyclerView=(RecyclerView)findViewById(R.id.profile_tag_gridImages_rv);
        tagDistrictName=(TextView)findViewById(R.id.tagDistrictName);
        tagName=(TextView)findViewById(R.id.tagName);
        tagFollowButton=(Button)findViewById(R.id.tagFollowButton);
        followersForTags=(TextView)findViewById(R.id.followersForTags);
        followersForTagsCount=(TextView)findViewById(R.id.followersForTagsCount);
        profileTagRating=(SeekBar) findViewById(R.id.profileTagRating);
        tagRatingPercentage=(TextView) findViewById(R.id.tagRatingPercentage);

        //checking user is following or not
        Query postQuery =  FirebaseDatabase.getInstance().getReference().child("District")
                .child(district).child(tag).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.hasChild("following")){
                       tagFollowButton.setText("following");
                   }else {
                       tagFollowButton.setText("follow");
                   }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addValueEventListener(valueEventListener);

        //getting district name and tag from previous intent
        tagDistrictName.setText(district);
        tagName.setText(tag);

        //listener for user is following or not
        tagFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagFollowButton.getText().equals("follow")){
                    following();
                }else
                {
                    Toast.makeText(getApplicationContext(),"you are already following "+tag,Toast.LENGTH_SHORT).show();
                }

            }
        });

         //getting num of followers
         Query numofFol =  FirebaseDatabase.getInstance().getReference().child("District")
                        .child(district).child(tag).child("Followers");
                ValueEventListener valueEventListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String numOfFollowers= String.valueOf(dataSnapshot.getChildrenCount());
                           // Toast.makeText(getApplicationContext(),numOfFollowers+" are following "+tag,Toast.LENGTH_SHORT).show();
                            followersForTagsCount.setText(numOfFollowers);
                        }else {
                            Toast.makeText(getApplicationContext()," no followers for "+tag,Toast.LENGTH_SHORT).show();
                            followersForTagsCount.setText("0");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
        numofFol.addValueEventListener(valueEventListener1);


        tag_gridViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int mod = position % 4;

                        if(position == 0 || position == 1)
                            return 2;
                        else if(position < 4)
                            return 2;
                        else if(mod == 0 || mod == 1)
                            return 1;
                        else
                            return 2;
                    }
                });
                adapter=new Tag_Profile_Images_Adapter(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setNestedScrollingEnabled(false);
            }
        });


        //implementing rating functionality
        profileTagRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tagRating=String.valueOf(progress);
                tagRatingPercentage.setText("Rating Percentage "+tagRating+"%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(),"stop touch ",Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(Profile_TagActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Your Rating is "+tagRating+"%")
                        .setMessage("Are you sure you want to submit this rating")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //posting Individual tag rating into fire base
                                postingTagRating();
                                //Posting overall for tag
                                postingOverallRating();

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
      FirebaseDatabase.getInstance().getReference().child("District")
              .child(district).child(tag).child("Followers")
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

    private void postingTagRating(){
        String saveCurrentDate;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("District")
                .child(district)
                .child(tag)
                .child("Votes")
                .child(saveCurrentDate)
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .setValue(tagRating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Thank you for your rating",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Some error was occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postingOverallRating(){
        Query postQuery=FirebaseDatabase.getInstance().getReference().child("District")
                .child(district)
                .child(tag)
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
        FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child(tag)
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
                .child(state).child("MLA").child("district").child(district).child(tag)
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
