package shamgar.org.peoplesfeedback.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private String mlaName,mlaConstituency,tagRating,state,district,status,mla_image;

    private TextView mlanametxt,txtmlaConstituency,followersForMlaCount,mlaRatingPercentage,overallVotesMla,overallRatingMla,mlaPartyName;
    private Button mlaFollowButton;
    private ImageView mlaimageButton;
    private BubbleSeekBar mlaRatingSeekbar;
    private ImageView mla_gridViewImage;
    private RecyclerView profile_mla_gridImages_rv;
    private RatingBar mlaProfile_rating;
    private CircleImageView mlaProfileImage;
    private ImageView listViewImagesMLa;
    private NestedScrollView scrollViewMLa;

    private SharedPreferenceConfig sharedPreferenceConfig;
    private ArrayList<String> images;
//    private ArrayList<String> postedOn;
//    private ArrayList<String> lat;
//    private ArrayList<String> lon;
//    private ArrayList<String> tagId;
//    private ArrayList<String> desc;
//    private ArrayList<String> user;
      private ArrayList<String> keys;
    private Tag_Profile_Images_Adapter adapter;
    private ProfileImagesInListviewAdapter imagesInListviewAdapter;
    private TextView followersForMla;

    private View convertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_mla_);

        sharedPreferenceConfig=new SharedPreferenceConfig(this);

        images=new ArrayList<>();
        keys=new ArrayList<>();

        mlanametxt=findViewById(R.id.mlaDistrictName);
        txtmlaConstituency=findViewById(R.id.mlaName);
        mlaFollowButton=findViewById(R.id.mlaFollowButton);
        followersForMlaCount=findViewById(R.id.followersForMlaCount);
        mlaRatingSeekbar=findViewById(R.id.mlaRatingSeekbar);
        mlaRatingPercentage=findViewById(R.id.mlaRatingPercentage);
        overallRatingMla=findViewById(R.id.overallRatingMla);
        overallVotesMla=findViewById(R.id.overallVotesMla);
        //mla_gridViewImage = findViewById(R.id.mla_grid_image);
        profile_mla_gridImages_rv=findViewById(R.id.profile_mla_gridImages_rv);
        mlaProfile_rating=findViewById(R.id.mlaProfile_rating);
        mlaProfileImage=findViewById(R.id.mlaProfileImage);
        mlaPartyName=findViewById(R.id.mlaPartyName);
      //  listViewImagesMLa=findViewById(R.id.listViewImagesMLa);
        scrollViewMLa=findViewById(R.id.scrollViewMLa);
        convertView=findViewById(R.id.mlaFramelayout);
        mlaimageButton=findViewById(R.id.mlaimageButton);
        followersForMla=findViewById(R.id.followersForMla);

        //converting view into image
        convertView.setDrawingCacheEnabled(true);
        convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        convertView.layout(0,0,convertView.getWidth(),convertView.getHeight());
        convertView.buildDrawingCache(true);

        mlaimageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap=Bitmap.createBitmap(convertView.getDrawingCache());
                convertView.setDrawingCacheEnabled(false);
//                ByteArrayOutputStream bytes=new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);

                if(bitmap!=null)
                {
                    Log.e("img","converted image");
                    try {
                        File file = new File(getApplicationContext().getExternalCacheDir(),"logicchip.png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();

                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        Uri apkURI = FileProvider.getUriForFile(
                                getApplicationContext(), getApplicationContext()
                                        .getPackageName() + ".provider", file);
                        intent.setDataAndType(apkURI, Intent.normalizeMimeType("image/png"));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_TEXT, "MLA name : "+mlaName+"\nConstituency : "+mlaConstituency+
                                "\nPlayStore Link : https://play.google.com/store/apps/details?id=shamgar.org.peoplesfeedback");
                        intent.putExtra(Intent.EXTRA_STREAM, apkURI);
//                            intent.setType("image/png");
                        //  ctx.startActivity(Intent.createChooser(intent, "Share image via"));
                        startActivity(intent);


                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(),"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Log.e("img","failed");

                }

            }
        });

        getSupportActionBar().setTitle("MLA profile");
//         final int sdk= Build.VERSION.SDK_INT;
//         if (sdk< Build.VERSION_CODES.JELLY_BEAN){
//             getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
//         }

        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
        getSupportActionBar().setElevation(0);

        followersForMla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mlaFollowers=new Intent(Profile_mla_Activity.this,MLAFollowers.class);
                mlaFollowers.putExtra("follower",mlaName);
                startActivity(mlaFollowers);
            }
        });



        mlaName=getIntent().getExtras().getString("mlaName");
        mlaConstituency=getIntent().getExtras().getString("mlaConstituency");
        state=getIntent().getExtras().getString("state");
        district=getIntent().getExtras().getString("district");
        status=getIntent().getExtras().getString("status");
        mla_image=getIntent().getExtras().getString("mla_image");

        Glide.with(getApplicationContext())
                .load(mla_image)
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mlaProfileImage);


        if (status.contains("1")){
            txtmlaConstituency.setText(mlaConstituency);
            mlanametxt.setText(mlaName);

            gettingMLAImage();
            gettingMlaTagedImagesKeys();
            //checking user is following or not
            Query postQuery =  FirebaseDatabase.getInstance().getReference().child("Politicians")
                    .child(mlaName).child("Followers")
                    .child(sharedPreferenceConfig.readPhoneNo().substring(3));
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("following")){
                        mlaFollowButton.setText("unFollow");
                        mlaFollowButton.setTextColor(Color.parseColor("#000000"));
                    }else {
                        mlaFollowButton.setText("follow");
                        mlaFollowButton.setTextColor(Color.parseColor("#c2185b"));

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
                        // Toast.makeText(getApplicationContext()," no followers for "+mlaName,Toast.LENGTH_SHORT).show();
                        followersForMlaCount.setText("0");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            numofFol.addValueEventListener(valueEventListener1);
        }


        scrollViewMLa.setNestedScrollingEnabled(false);
        scrollViewMLa.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mlaRatingSeekbar.correctOffsetWhenContainerOnScrolling();
            }
        });




        //implementing follow functionality for mla
        mlaFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlaFollowButton.getText().equals("follow")){
                    following();
                }else
                {
                    unfollow();
                }

            }
        });



        //implementing voting functionality
        mlaRatingSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tagRating=String.valueOf(progress);
                mlaRatingPercentage.setText("Rating Percentage "+tagRating+"%");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                // Toast.makeText(getApplicationContext(),"stop touch ",Toast.LENGTH_SHORT).show();
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





    }

    private void unfollow() {
        FirebaseDatabase.getInstance().getReference().child("Politicians")
                .child(mlaName).child("Followers")
                .child(sharedPreferenceConfig.readPhoneNo().substring(3))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mlaFollowButton.setTextColor(Color.parseColor("#c2185b"));
                            mlaFollowButton.setText("follow");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE)
                .child(sharedPreferenceConfig.readPhoneNo().substring(3)).child("pFollow")
                .child(mlaName).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("pFollow","unFollow success");
                        }
                    }
                });
    }

    private void gettingMlaTagedImagesKeys()
    {
        Query taggedImages =  FirebaseDatabase.getInstance().getReference().child(NamesC.INDIA)
                .child(state).child(district).child("constituancy").child(mlaConstituency).child("PostID");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   // images.add(dataSnapshot.getKey());
                    keys.clear();
                    for (DataSnapshot innersnap:dataSnapshot.getChildren()){
                        keys.add(innersnap.getKey());
                    }
                  //  Log.e("keys",keys.toString());
                    gettingImageUrls(keys);
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
                // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
                profile_mla_gridImages_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                profile_mla_gridImages_rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                profile_mla_gridImages_rv.setNestedScrollingEnabled(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
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
                            mlaFollowButton.setText("unFollow");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE)
                .child(sharedPreferenceConfig.readPhoneNo().substring(3)).child("pFollow")
                .child(mlaName).setValue("1")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("pFollow","success");
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
                    // Toast.makeText(getApplicationContext(),mlaUrl,Toast.LENGTH_SHORT).show();


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
                       // Log.e("keys",keys.getKey());
                        for (DataSnapshot innerChildren: keys.getChildren()){
                          //  Log.e("keys",innerChildren.getValue().toString());
                            keysLists.add(innerChildren.getValue().toString());
                            total = total+Integer.parseInt(innerChildren.getValue().toString());
                          //  Log.e("total", String.valueOf(total));
                        }
                    }
                    float average= (float) (total/keysLists.size());
                   // Log.e("average", String.valueOf(average));
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
              //  Toast.makeText(getApplicationContext(), "Updated ", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
