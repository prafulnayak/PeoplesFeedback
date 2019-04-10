package shamgar.org.peoplesfeedback.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class PartyProfile extends AppCompatActivity {

    private String party_name,established_on,established_by,party_image,status,tagRating;
    private CircleImageView partyProfileImage;
    private TextView partyName,partyEstablishedBY,PartyEstablishedOn,followersForPartyCount,followersForParty,overallVotesParty,overallRatingParty,
            partyRatingPercentage;
    private Button PartyFollowButton;
    private ImageView partyimageButton;
    private RatingBar partyProfile_rating;
    private SharedPreferenceConfig config;
    private BubbleSeekBar partyRatingSeekbar;
    private NestedScrollView scrollViewParty;

    private View convertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_profile);

        config=new SharedPreferenceConfig(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Party profile");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
        getSupportActionBar().setElevation(0);

        party_name=getIntent().getExtras().getString("partyName");
        established_by=getIntent().getExtras().getString("established_by");
        established_on=getIntent().getExtras().getString("established_on");
        status=getIntent().getExtras().getString("status");

        partyName=findViewById(R.id.partyName);
        partyEstablishedBY=findViewById(R.id.partyEstablishedBY);
        PartyEstablishedOn=findViewById(R.id.PartyEstablishedOn);
        partyProfileImage=findViewById(R.id.partyProfileImage);
        PartyFollowButton=findViewById(R.id.PartyFollowButton);
        followersForPartyCount=findViewById(R.id.followersForPartyCount);
        partyimageButton=findViewById(R.id.partyimageButton);
        convertView=findViewById(R.id.partyFramelayout);
        followersForParty=findViewById(R.id.followersForParty);
        partyProfile_rating=findViewById(R.id.partyProfile_rating);
        overallRatingParty=findViewById(R.id.overallRatingParty);
        overallVotesParty=findViewById(R.id.overallVotesParty);
        partyRatingSeekbar=findViewById(R.id.partyRatingSeekbar);
        partyRatingPercentage=findViewById(R.id.partyRatingPercentage);
        scrollViewParty=findViewById(R.id.scrollViewParty);

        //converting view into image
        convertView.setDrawingCacheEnabled(true);
        convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        convertView.layout(0,0,convertView.getWidth(),convertView.getHeight());
        convertView.buildDrawingCache(true);

        partyimageButton.setOnClickListener(new View.OnClickListener() {
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
                        intent.putExtra(Intent.EXTRA_TEXT, "MLA name : "+party_name+
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

        followersForParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mlaFollowers=new Intent(PartyProfile.this,MLAFollowers.class);
                mlaFollowers.putExtra("follower",party_name);
                mlaFollowers.putExtra("status","2");
                startActivity(mlaFollowers);
            }
        });


        if (status.contains("1")){
            partyName.setText(party_name);
            partyEstablishedBY.setText(established_by);
            PartyEstablishedOn.setText(established_on);

            Glide.with(getApplicationContext())
                    .load("http://sairaa.org/peopleFeedback/images/"+party_name+".jpg")
                    .error(R.drawable.ic_account_circle_black)
                    // read original from cache (if present) otherwise download it and decode it
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(partyProfileImage);

            //checking user is following or not
            Query postQuery =  FirebaseDatabase.getInstance().getReference().child("Party")
                    .child(party_name).child("Followers")
                    .child(config.readPhoneNo().substring(3));
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("following")){
                        PartyFollowButton.setText("unFollow");
                        PartyFollowButton.setTextColor(Color.parseColor("#000000"));
                    }else {
                        PartyFollowButton.setText("follow");
                        PartyFollowButton.setTextColor(Color.parseColor("#c2185b"));

                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            postQuery.addValueEventListener(valueEventListener);

            //getting overall rating and votes
            final Query postQuery1 =  FirebaseDatabase.getInstance().getReference().child("Party")
                    .child(party_name);
            ValueEventListener valueEventListener2 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        if (dataSnapshot.hasChild("rating")){
                            String rating=dataSnapshot.child("rating").getValue().toString();
                            String Votes=dataSnapshot.child("votes").getValue().toString();
                            partyProfile_rating.setRating(Integer.parseInt(rating)*5/100);
                            overallRatingParty.setText(rating+"%");
                            overallVotesParty.setText("Total Votes: "+Votes);
                        }else {
                            partyProfile_rating.setRating(0);
                            overallRatingParty.setText("0%");
                            overallVotesParty.setText("Total Votes: 0");
                        }
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
            Query numofFol =  FirebaseDatabase.getInstance().getReference().child("Party")
                    .child(party_name).child("Followers");
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String numOfFollowers= String.valueOf(dataSnapshot.getChildrenCount());
                        // Toast.makeText(getApplicationContext(),numOfFollowers+" are following "+tag,Toast.LENGTH_SHORT).show();
                        followersForPartyCount.setText(numOfFollowers);
                    }else {
                        // Toast.makeText(getApplicationContext()," no followers for "+mlaName,Toast.LENGTH_SHORT).show();
                        followersForPartyCount.setText("0");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            numofFol.addValueEventListener(valueEventListener1);



            //implementing follow functionality for party
            PartyFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PartyFollowButton.getText().equals("follow")){
                       follow();
                    }else {
                        unfollow();
                    }
                }
            });


            scrollViewParty.setNestedScrollingEnabled(false);
            scrollViewParty.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    partyRatingSeekbar.correctOffsetWhenContainerOnScrolling();
                }
            });

            //implementing voting functionality
            partyRatingSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(int progress, float progressFloat) {
                    tagRating=String.valueOf(progress);
                    partyRatingPercentage.setText("Rating Percentage "+tagRating+"%");
                }

                @Override
                public void getProgressOnActionUp(int progress, float progressFloat) {
                    // Toast.makeText(getApplicationContext(),"stop touch ",Toast.LENGTH_SHORT).show();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PartyProfile.this);
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
    }

    private void unfollow() {
        FirebaseDatabase.getInstance().getReference().child("Party")
                .child(party_name).child("Followers")
                .child(config.readPhoneNo().substring(3))
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            PartyFollowButton.setTextColor(Color.parseColor("#c2185b"));
                            PartyFollowButton.setText("follow");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE)
                .child(config.readPhoneNo().substring(3)).child("partyFollow")
                .child(party_name).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("partyFollow","unFollow success");
                        }
                    }
                });
    }

    private void follow() {
        FirebaseDatabase.getInstance().getReference().child("Party")
                .child(party_name).child("Followers")
                .child(config.readPhoneNo().substring(3))
                .child("following")
                .setValue("1")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            PartyFollowButton.setTextColor(Color.parseColor("#000000"));
                            PartyFollowButton.setText("unFollow");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE)
                .child(config.readPhoneNo().substring(3)).child("partyFollow")
                .child(party_name).setValue("1")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("partyFollow","success");
                        }
                    }
                });
    }

    private void postingMlaRating() {
        String saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("Party")
                .child(party_name)
                .child("Votes")
                .child(saveCurrentDate)
                .child(config.readPhoneNo().substring(3))
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
        Query postQuery=FirebaseDatabase.getInstance().getReference().child("Party")
                .child(party_name)
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

        FirebaseDatabase.getInstance().getReference().child("Party")
                .child(party_name)
                .updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  Toast.makeText(getApplicationContext(), "Updated ", Toast.LENGTH_LONG).show();
            }
        });
    }
}
