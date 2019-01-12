package shamgar.org.peoplesfeedback.UI;

import android.support.annotation.NonNull;
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

import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.Adapters.Politicians.ViewAllPoliticiansAdapter;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

import static android.widget.LinearLayout.VERTICAL;

public class Profile_TagActivity extends AppCompatActivity {

    private ImageButton tag_gridViewImages;
    private TextView tagName,tagDistrictName,followersForTags;
    private Button tagFollowButton;

    private Tag_Profile_Images_Adapter adapter;
    private RecyclerView recyclerView;
    private String district,tag;
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

        tag_gridViewImages=(ImageButton)findViewById(R.id.tag_gridViewImages);
        recyclerView=(RecyclerView)findViewById(R.id.profile_tag_gridImages_rv);
        tagDistrictName=(TextView)findViewById(R.id.tagDistrictName);
        tagName=(TextView)findViewById(R.id.tagName);
        tagFollowButton=(Button)findViewById(R.id.tagFollowButton);
        followersForTags=(TextView)findViewById(R.id.followersForTags);

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

        //getting num of followers for this tag
        followersForTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking user is following or not
                Query postQuery =  FirebaseDatabase.getInstance().getReference().child("District")
                        .child(district).child(tag).child("Followers");
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            int numOfFollowers= (int) dataSnapshot.getChildrenCount();
                            Toast.makeText(getApplicationContext(),numOfFollowers+" are following "+tag,Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext()," no followers for "+tag,Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                postQuery.addValueEventListener(valueEventListener);
            }
        });


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
}
