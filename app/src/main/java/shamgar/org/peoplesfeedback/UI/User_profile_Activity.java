package shamgar.org.peoplesfeedback.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    private TextView current_user_profile_name,current_user_profile_location,current_user_profile_num_of_photos;
    private ImageButton user_grid_image,listViewImagesUser;

    private RecyclerView profile_tag_gridImages_rv;
    private Tag_Profile_Images_Adapter adapter;

    private SharedPreferenceConfig config;

    private ArrayList<String> images=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_);

        getSupportActionBar().setTitle("My Profile");

        config=new SharedPreferenceConfig(this);
        profile_userImage=findViewById(R.id.profile_userImage);
        current_user_profile_name=findViewById(R.id.current_user_profile_name);
        current_user_profile_location=findViewById(R.id.current_user_profile_location);
        current_user_profile_num_of_photos=findViewById(R.id.current_user_profile_num_of_photos);
        profile_tag_gridImages_rv=findViewById(R.id.profile_tag_gridImages_rv);
        user_grid_image=findViewById(R.id.user_grid_image);
        listViewImagesUser=findViewById(R.id.listViewImagesUser);

        current_user_profile_name.setText(config.readName());
        current_user_profile_location.setText(config.readConstituancy());

        Glide.with(this)
                .load(config.readPhotoUrl())
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(profile_userImage);

        gettingNUmOfPhotosUpLoaded();

        //click listener for images


        //getting images from fire base
        user_grid_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewImagesUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_gray_24dp));
                user_grid_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_primary_24dp));
                adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
                // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
                profile_tag_gridImages_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                profile_tag_gridImages_rv.setAdapter(adapter);
                profile_tag_gridImages_rv.setNestedScrollingEnabled(false);
            }
        });
        listViewImagesUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewImagesUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
                user_grid_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_view_quilt_gray_24dp));
            }
        });
    }

    private void gettingNUmOfPhotosUpLoaded() {
        Query taggedImages =  FirebaseDatabase.getInstance().getReference().child(NamesC.PEOPLE)
                .child(config.readPhoneNo().substring(3)).child("postedPost");

        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // images.add(dataSnapshot.getKey());
                    String photos= String.valueOf(dataSnapshot.getChildrenCount());
                    current_user_profile_num_of_photos.setText( photos);
                    for (DataSnapshot innersnap:dataSnapshot.getChildren()){

                        Query query=FirebaseDatabase.getInstance().getReference().child(NamesC.POSTS).child(innersnap.getKey());
                        ValueEventListener valueEventListener1=new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Log.e("image urls",dataSnapshot.child("imageUrl").getValue().toString());
                                    images.add(dataSnapshot.child("imageUrl").getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        query.addValueEventListener(valueEventListener1);
                    }
//                    adapter=new Tag_Profile_Images_Adapter(getApplicationContext(),images);
//                    // staggeredGridLayoutManager=new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
//                    profile_tag_gridImages_rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
//                    profile_tag_gridImages_rv.setAdapter(adapter);
//                    profile_tag_gridImages_rv.setNestedScrollingEnabled(false);

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
}
