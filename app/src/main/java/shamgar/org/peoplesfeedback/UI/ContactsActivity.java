package shamgar.org.peoplesfeedback.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Fragments.Chat;
import shamgar.org.peoplesfeedback.Model.Contacts;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView myContactRecyclerView;
    private DatabaseReference contactsRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private SharedPreferenceConfig sharedPreferenceConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        myContactRecyclerView=findViewById(R.id.contactsRecyclerview);
        myContactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Contacts");
        sharedPreferenceConfig=new SharedPreferenceConfig(this);

        mAuth= FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getPhoneNumber();
        contactsRef= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserId);
        userRef= FirebaseDatabase.getInstance().getReference().child("people");


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull Contacts model)
                    {
                        String userIds=getRef(position).getKey();
                        userRef.orderByChild("phoneno").equalTo(userIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists()){
                                    String email=null;
                                    String phoneno=null;
                                    String Image=null;
                                    for (DataSnapshot innersnap:dataSnapshot.getChildren()) {
                                        Image =innersnap.child("desc").getValue(String.class);
                                        Glide.with(getApplicationContext())
                                                .load(Image)
                                                .error(R.drawable.ic_account_circle_black)
                                                // read original from cache (if present) otherwise download it and decode it
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(holder.profileImage);
                                        email = innersnap.child("name").getValue(String.class);
                                        phoneno=innersnap.child("phoneno").getValue(String.class);
                                        holder.userName.setText(email);
                                        holder.userStatus.setText(phoneno);
                                        if (innersnap.child("userState").hasChild("state")){
                                                String state=innersnap.child("userState").child("state").getValue().toString();
                                                String time=innersnap.child("userState").child("date").getValue().toString();
                                                String date=innersnap.child("userState").child("time").getValue().toString();
                                                if (state.equals("online"))
                                                    holder.user_online.setVisibility(View.VISIBLE);
                                                else if (state.equals("offline"))
                                                    holder.user_online.setVisibility(View.INVISIBLE);
                                        }
                                        else{
                                               holder.user_online.setVisibility(View.INVISIBLE);
                                            }


                                    }
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_layout,parent,false);
                        ContactsViewHolder viewHolder=new ContactsViewHolder(view);
                        return viewHolder;
                    }
                };
        myContactRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;
        ImageView user_online;

        public ContactsViewHolder(View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            user_online=itemView.findViewById(R.id.user_online);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//         super.onOptionsItemSelected(item);
//         if (item.getItemId()==android.R.id.home)
//         {
//             Intent HomeScreenActivity=new Intent(ContactsActivity.this,HomeScreenActivity.class);
//             startActivity(HomeScreenActivity);
//             finish();
//         }
//
//         return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent HomeScreenActivity=new Intent(ContactsActivity.this,HomeScreenActivity.class);
//        startActivity(HomeScreenActivity);
//        finish();
//    }
}
