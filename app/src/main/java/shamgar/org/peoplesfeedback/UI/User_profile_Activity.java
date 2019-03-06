package shamgar.org.peoplesfeedback.UI;

import android.content.Intent;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class User_profile_Activity extends AppCompatActivity {
    private CircleImageView profile_userImage;
    private TextView current_user_profile_name,current_user_profile_location,current_user_profile_num_of_photos,current_user_profile_num_of_followers,
            current_user_profile_num_of_following;
    private ImageButton user_grid_image,listViewImagesUser;

    private RecyclerView profile_tag_gridImages_rv;
    private Tag_Profile_Images_Adapter adapter;

    private SharedPreferenceConfig config;
    private FirebaseAuth mAuth;

    private String UID,mobile;
    private Button current_user_profile_follow_button;
    private ImageView user_profile_chat;
    private TextView user_profile_chat_heading;

    private String name,number,image;

    private ArrayList<String> images;
    private ArrayList<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_);

        getSupportActionBar().setTitle("Profile");

        images=new ArrayList<>();
        keys=new ArrayList<>();

        mAuth=FirebaseAuth.getInstance();
        UID=mAuth.getCurrentUser().getPhoneNumber();
        mobile=getIntent().getExtras().get("mobile").toString();
       // Log.e("UID",UID);

        checkingUserProfile(UID);


        config=new SharedPreferenceConfig(this);
        profile_userImage=findViewById(R.id.profile_userImage);
        current_user_profile_name=findViewById(R.id.current_user_profile_name);
        current_user_profile_location=findViewById(R.id.current_user_profile_location);
        current_user_profile_num_of_photos=findViewById(R.id.current_user_profile_num_of_photos);
        profile_tag_gridImages_rv=findViewById(R.id.profile_tag_gridImages_rv);
        current_user_profile_follow_button=findViewById(R.id.current_user_profile_follow_button);
        current_user_profile_num_of_followers=findViewById(R.id.current_user_profile_num_of_followers);
        current_user_profile_num_of_following=findViewById(R.id.current_user_profile_num_of_following);
        user_profile_chat=findViewById(R.id.user_profile_chat);
        user_profile_chat_heading=findViewById(R.id.user_profile_chat_heading);
//        user_grid_image=findViewById(R.id.user_grid_image);
//        listViewImagesUser=findViewById(R.id.listViewImagesUser);



        gettingNUmOfPhotosUpLoaded();

        current_user_profile_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(current_user_profile_follow_button.getText().equals("follow")){
                    followTheUser();
                }
                else {
                    unFollowTheUser();
                }
            }
        });

        user_profile_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkingContactIsSavedOrNot();
            }
        });



        //click listener for images


        //getting images from fire base
//        user_grid_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listViewImagesUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_gray_24dp));
//                user_grid_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_primary_24dp));
//                adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
//                // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
//                profile_tag_gridImages_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
//                profile_tag_gridImages_rv.setAdapter(adapter);
//                profile_tag_gridImages_rv.setNestedScrollingEnabled(false);
//            }
//        });
//        listViewImagesUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listViewImagesUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
//                user_grid_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_gray_24dp));
//            }
//        });
    }

    private void checkingContactIsSavedOrNot() {
        Query query=FirebaseDatabase.getInstance().getReference().child("contacts").child(config.readPhoneNo());
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(mobile)){
                        Intent profile=new Intent(User_profile_Activity.this,ChatActivity.class);
                        profile.putExtra("visit_user_id",number);
                        profile.putExtra("visit_email_id", name);
                        profile.putExtra("visit_image", image);
                        startActivity(profile);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"You have to send chat request "+name,Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void unFollowTheUser() {
        FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(UID.substring(3)).child("following")
                .child(mobile.substring(3)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Log.e("task","following the user");
                    FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(mobile.substring(3)).child("followers")
                            .child(UID.substring(3)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                current_user_profile_follow_button.setText("follow");
                                // Log.e("task","followers Updated");
                            }
                        }
                    });
                }
            }
        });
    }

    private void followTheUser() {

        FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(UID.substring(3)).child("following")
                .child(mobile.substring(3)).setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   // Log.e("task","following the user");
                    FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(mobile.substring(3)).child("followers")
                            .child(UID.substring(3)).setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                current_user_profile_follow_button.setText("unFollow");
                               // Log.e("task","followers Updated");
                            }
                        }
                    });
                }
            }
        });


    }

    private void checkingUserProfile(final String uid) {

        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE).child(mobile.substring(3));
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(TextUtils.equals(uid,dataSnapshot.child("phoneno").getValue().toString())){
                       // Log.e("note","Hide something");
                        current_user_profile_follow_button.setVisibility(View.GONE);
                        user_profile_chat_heading.setVisibility(View.GONE);
                        user_profile_chat.setVisibility(View.GONE);
                        current_user_profile_name.setText(config.readName());
                        current_user_profile_location.setText(config.readConstituancy());

                        Glide.with(getApplicationContext())
                                .load(config.readPhotoUrl())
                                .error(R.drawable.ic_account_circle_black)
                                // read original from cache (if present) otherwise download it and decode it
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(profile_userImage);

                        if(dataSnapshot.hasChild("followers")){
                            String followers= String.valueOf(dataSnapshot.child("followers").getChildrenCount());
                            current_user_profile_num_of_followers.setText(followers);
                        }
                        else {
                            current_user_profile_num_of_followers.setText("0");
                        }
                        if (dataSnapshot.hasChild("following")){
                            String following= String.valueOf(dataSnapshot.child("following").getChildrenCount());
                            current_user_profile_num_of_following.setText(following);
                        }
                        else {
                            current_user_profile_num_of_following.setText("0");
                        }


                    }
                    else{

                        name=dataSnapshot.child("name").getValue().toString();
                        number=dataSnapshot.child("phoneno").getValue().toString();
                        image=dataSnapshot.child("desc").getValue().toString();

                        current_user_profile_name.setText(dataSnapshot.child("name").getValue().toString());
                        current_user_profile_location.setText(dataSnapshot.child("constituancy").getValue().toString());

                        Glide.with(getApplicationContext())
                                .load(dataSnapshot.child("desc").getValue().toString())
                                .error(R.drawable.ic_account_circle_black)
                                // read original from cache (if present) otherwise download it and decode it
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(profile_userImage);
                        if (dataSnapshot.hasChild("followers")) {
                            if (dataSnapshot.child("followers").hasChild(UID.substring(3))){
                                current_user_profile_follow_button.setText("unFollow");
                            }
                        }
                        if(dataSnapshot.hasChild("followers")){
                            String followers= String.valueOf(dataSnapshot.child("followers").getChildrenCount());
                            current_user_profile_num_of_followers.setText(followers);
                        }
                        else {
                            current_user_profile_num_of_followers.setText("0");
                        }
                        if (dataSnapshot.hasChild("following")){
                            String following= String.valueOf(dataSnapshot.child("following").getChildrenCount());
                            current_user_profile_num_of_following.setText(following);
                        }
                        else {
                            current_user_profile_num_of_following.setText("0");
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void gettingNUmOfPhotosUpLoaded() {

        Query taggedImages =  FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE)
                .child(mobile.substring(3)).child("postedPost");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    // images.add(dataSnapshot.getKey());
                    String photos= String.valueOf(dataSnapshot.getChildrenCount());
                    current_user_profile_num_of_photos.setText( photos);
                    keys.clear();
                    for (DataSnapshot innersnap:dataSnapshot.getChildren()){
                        keys.add(innersnap.getKey());
                       // Log.e("keys",keys.toString());
                    }
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
                    profile_tag_gridImages_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                    profile_tag_gridImages_rv.setAdapter(adapter);
                    profile_tag_gridImages_rv.setNestedScrollingEnabled(false);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }
}
