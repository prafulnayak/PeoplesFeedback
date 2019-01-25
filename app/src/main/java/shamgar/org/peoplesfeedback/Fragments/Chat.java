package shamgar.org.peoplesfeedback.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import shamgar.org.peoplesfeedback.Model.Contacts;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.ChatActivity;
import shamgar.org.peoplesfeedback.UI.ContactsActivity;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {
    private RecyclerView chatList;
    private DatabaseReference contactsRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private SharedPreferenceConfig sharedPreferences;


    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        mAuth= FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getPhoneNumber();
        sharedPreferences = new SharedPreferenceConfig(getActivity());
        contactsRef= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserId);
        userRef= FirebaseDatabase.getInstance().getReference().child("people");

        chatList=view.findViewById(R.id.chatFragmentRecyclerView);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts,ChatViewHolder>(options) {

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_layout,parent,false);
                        ChatViewHolder viewHolder=new ChatViewHolder(view);
                        return viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {
                        final  String userIds=getRef(position).getKey();
                        Log.e("ids",userIds);
                        final String[] Image = {"default image"};
                        userRef.orderByChild("phoneno").equalTo(userIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String email=null;
                                    String phoneno=null;
                                   
                                    for (DataSnapshot innersnap:dataSnapshot.getChildren()) {
                                        if (dataSnapshot.hasChild("image")) {
                                            Image[0] =innersnap.child("image").getValue().toString();
                                            Picasso.get().load(Image[0]).placeholder(R.drawable.profile).into(holder.profileImage);
                                        }

                                        //have to change email to username
                                        email = innersnap.child("email").getValue(String.class);
                                        phoneno=innersnap.child("phoneno").getValue(String.class);
                                        holder.userName.setText(email);
                                        Log.e("email",email);

                                        if (innersnap.child("userState").hasChild("state")){
                                                String state=innersnap.child("userState").child("state").getValue().toString();
                                                String time=innersnap.child("userState").child("date").getValue().toString();
                                                String date=innersnap.child("userState").child("time").getValue().toString();
                                                if (state.equals("online")) {
                                                    holder.userStatus.setText("online");
                                                }
                                                else if (state.equals("offline")) {
                                                    holder.userStatus.setText("Last Seen: " + date + " " + time);
                                                }

                                        }
                                        else {
                                            holder.userStatus.setText("offline");
                                        }


                                        final String finalEmail = email;
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent chatActivity=new Intent(getContext(), ChatActivity.class);
                                                chatActivity.putExtra("visit_user_id",userIds);
                                                chatActivity.putExtra("visit_email_id", finalEmail);
                                                chatActivity.putExtra("visit_image", Image[0]);
                                                startActivity(chatActivity);
                                            }
                                        });

                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;

        public ChatViewHolder(View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }
}
