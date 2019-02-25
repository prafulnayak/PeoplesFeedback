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
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.Adapters.Politicians.ViewAllPoliticiansAdapter;
import shamgar.org.peoplesfeedback.Adapters.ProfileImagesInListviewAdapter;
import shamgar.org.peoplesfeedback.Adapters.TagListViewImagesAdapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.widget.LinearLayout.VERTICAL;

public class Profile_TagActivity extends AppCompatActivity {

    private ImageButton tag_grid_image,listViewImagesTag;
    private TextView tagName,tagDistrictName,followersForTags,
            followersForTagsCount,tagRatingPercentage
            ,overallVotesTag,overallRatingTag;
    private Button tagFollowButton;
    private BubbleSeekBar profileTagRating;

    private Tag_Profile_Images_Adapter adapter;
    private RecyclerView recyclerView;
    private String district,tag,tagRating,state;
    private SharedPreferenceConfig sharedPreferenceConfig;


    private TagListViewImagesAdapter tag_profile_images_adapter;
    private ArrayList<String> images;
//    private ArrayList<String> postedOn;
//    private ArrayList<String> lat;
//    private ArrayList<String> lon;
//    private ArrayList<String> tagId;
//    private ArrayList<String> desc;
//    private ArrayList<String> user;
    private ArrayList<String> keys;
//    private ArrayList<String> districts;
//    private ArrayList<String> constituency;
//    private ArrayList<String> states;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_profile_updated);

        sharedPreferenceConfig=new SharedPreferenceConfig(this);
        images=new ArrayList<>();
//        postedOn=new ArrayList<>();
//        lat=new ArrayList<>();
//        lon=new ArrayList<>();
//        tagId=new ArrayList<>();
//        desc=new ArrayList<>();
//        user=new ArrayList<>();
        keys=new ArrayList<>();
//        districts=new ArrayList<>();
//        constituency=new ArrayList<>();
//        states=new ArrayList<>();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        district=getIntent().getExtras().getString("district");
        tag=getIntent().getExtras().getString("tag");
        state=getIntent().getExtras().getString("state");
        getSupportActionBar().setTitle(tag);

//        tag_grid_image=(ImageButton)findViewById(R.id.tag_grid_image);
//        listViewImagesTag=(ImageButton)findViewById(R.id.listViewImagesTag);
        recyclerView=(RecyclerView)findViewById(R.id.profile_tag_gridImages_rv);
        tagDistrictName=(TextView)findViewById(R.id.tagDistrictName);
        tagName=(TextView)findViewById(R.id.tagName);
        tagFollowButton=(Button)findViewById(R.id.tagFollowButton);
        followersForTags=(TextView)findViewById(R.id.followersForTags);
        followersForTagsCount=(TextView)findViewById(R.id.followersForTagsCount);
        profileTagRating= findViewById(R.id.profileTagRating);
        tagRatingPercentage=(TextView) findViewById(R.id.tagRatingPercentage);
        overallRatingTag=(TextView) findViewById(R.id.overallRatingTag);
        overallVotesTag=(TextView) findViewById(R.id.overallVotesTag);

        //checking user is following or not
        Query postQuery =  FirebaseDatabase.getInstance().getReference().child("District")
                .child(district).child(tag).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.hasChild("following")){
                       tagFollowButton.setText("unFollow");
                   }else {
                       tagFollowButton.setText("follow");
                   }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addValueEventListener(valueEventListener);

        //getting overall rating and votes
        Query postQuery1 =  FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(district).child(tag);
        ValueEventListener valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String rating=dataSnapshot.child("rating").getValue().toString();
                    String Votes=dataSnapshot.child("votes").getValue().toString();
                    overallRatingTag.setText(rating+"%");
                    overallVotesTag.setText("Total votes: "+Votes);
                }else {
                    Log.e("overall rating","data not exists");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        postQuery1.addValueEventListener(valueEventListener2);

        //getting district name and tag from previous intent
        tagDistrictName.setText(district);
        tagName.setText("@"+tag);

        //listener for user is following or not
        tagFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagFollowButton.getText().equals("follow")){
                    following();
                }else
                {
                    unFollow();
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
                            //Toast.makeText(getApplicationContext()," no followers for "+tag,Toast.LENGTH_SHORT).show();
                            followersForTagsCount.setText("0");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
        numofFol.addValueEventListener(valueEventListener1);

        //implementing rating functionality
        profileTagRating.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tagRating=String.valueOf(progress);
                tagRatingPercentage.setText("Rating Percentage "+tagRating+"%");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
               // Toast.makeText(getApplicationContext(),"stop touch ",Toast.LENGTH_SHORT).show();
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

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        gettingMlaTagedImages();

        //getting images from fire base
//        tag_grid_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listViewImagesTag.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_gray_24dp));
//                tag_grid_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_primary_24dp));
//                adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
//                // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
//                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
//                recyclerView.setAdapter(adapter);
//                recyclerView.setNestedScrollingEnabled(false);
//            }
//        });
//        listViewImagesTag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listViewImagesTag.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
//                tag_grid_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_gray_24dp));
//
//                tag_profile_images_adapter=new TagListViewImagesAdapter(getApplicationContext(),images,postedOn,lat,lon,tagId,desc,user,keys,districts,constituency,states);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                recyclerView.setAdapter(tag_profile_images_adapter);
//                tag_profile_images_adapter.notifyDataSetChanged();
//                recyclerView.setNestedScrollingEnabled(false);
//            }
//        });

    }

    private void unFollow() {
        FirebaseDatabase.getInstance().getReference().child("District")
                .child(district).child(tag).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            tagFollowButton.setText("follow");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void gettingMlaTagedImages()
    {

        Query taggedImages =  FirebaseDatabase.getInstance().getReference().child(NamesC.INDIA)
                .child(state).child(district).child(tag).child("postID");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // images.add(dataSnapshot.getKey());
                    keys.clear();
                    for (DataSnapshot innersnap:dataSnapshot.getChildren()){
                        keys.add(innersnap.getKey());
                    }
                    gettingImageUrls(keys);

                }else {
                   // Toast.makeText(getApplicationContext(),"  Images not found ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        taggedImages.addValueEventListener(valueEventListener);
    }

    private void gettingImageUrls(final ArrayList<String> keys) {


        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.POSTS);
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    images.clear();
                    for (int i=0;i<keys.size();i++){
                        // Log.e("urls",dataSnapshot.child(keys.get(i)).child("imageUrl").getValue().toString());
                        images.add(dataSnapshot.child(keys.get(i)).child("imageUrl").getValue().toString());
                    }
                    adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
//                // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                recyclerView.setAdapter(adapter);
                recyclerView.setNestedScrollingEnabled(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
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
                    tagFollowButton.setText("unFollow");
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
                       // Log.e("keys",keys.getKey());
                        for (DataSnapshot innerChildren: keys.getChildren()){
                          //  Log.e("keys",innerChildren.getValue().toString());
                            keysLists.add(innerChildren.getValue().toString());
                            total = total+Integer.parseInt(innerChildren.getValue().toString());
                           // Log.e("total", String.valueOf(total));
                        }
                    }
                    float average= (float) (total/keysLists.size());
                  //  Log.e("average", String.valueOf(average));
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
                    //Toast.makeText(getApplicationContext(),"rating updated",Toast.LENGTH_SHORT).show();
                }
                else {
                   // Toast.makeText(getApplicationContext(),"Some error was occurred while updating rating",Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(getApplicationContext(),"votes updated",Toast.LENGTH_SHORT).show();
                }
                else {
                   // Toast.makeText(getApplicationContext(),"Some error was occurred while updating votes",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
