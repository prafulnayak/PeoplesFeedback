package shamgar.org.peoplesfeedback.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private RecyclerView myContactRecyclerView;
    private DatabaseReference contactsRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        Toast.makeText(getActivity(),"contacts",Toast.LENGTH_LONG).show();
        myContactRecyclerView=view.findViewById(R.id.contactsRecyclerview);
        myContactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth= FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getPhoneNumber();
        contactsRef= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentUserId);
        userRef= FirebaseDatabase.getInstance().getReference().child("people");

        return view;
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
                                String email=null;
                                String phoneno=null;
                                String Image=null;
                                for (DataSnapshot innersnap:dataSnapshot.getChildren()) {
                                    if (dataSnapshot.hasChild("image")) {
                                         Image=innersnap.child("image").getValue().toString();
                                         email = innersnap.child("email").getValue(String.class);
                                         phoneno=innersnap.child("phoneno").getValue(String.class);

                                        holder.userName.setText(email);
                                        holder.userStatus.setText(phoneno);
                                        Picasso.get().load(Image).placeholder(R.drawable.profile).into(holder.profileImage);
                                    }
                                    else {
                                        email = innersnap.child("email").getValue(String.class);
                                        phoneno=innersnap.child("phoneno").getValue(String.class);
                                        holder.userName.setText(email);
                                        holder.userStatus.setText(phoneno);
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
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_layout,parent,false);
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

        public ContactsViewHolder(View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_profile_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }

}
