package shamgar.org.peoplesfeedback.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import shamgar.org.peoplesfeedback.Model.Contacts;
import shamgar.org.peoplesfeedback.Model.People;
import shamgar.org.peoplesfeedback.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment  {
    protected boolean isVisible;
    private RecyclerView notificationRecyclerView;
    private DatabaseReference requestRef,userRef,contanctsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationRecyclerView=view.findViewById(R.id.Notification_Recyclerview);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getPhoneNumber();
        requestRef= FirebaseDatabase.getInstance().getReference().child("chat request");
        userRef= FirebaseDatabase.getInstance().getReference().child("people");
        contanctsRef= FirebaseDatabase.getInstance().getReference().child("contacts");
      //  Toast.makeText(getActivity(),"Notifications",Toast.LENGTH_LONG).show();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(requestRef.child(currentUserId),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,NotificationsViewHolder> adapter= new FirebaseRecyclerAdapter<Contacts, NotificationsViewHolder>(options) {
            @NonNull
            @Override
            public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_display_layout,parent,false);
                NotificationsViewHolder holder=new NotificationsViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final NotificationsViewHolder holder, int position, @NonNull Contacts model) {
                final String list_userId=getRef(position).getKey();
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String trpe=dataSnapshot.getValue().toString();

                            if (trpe.equals("received"))
                            {
                                userRef.orderByChild("phoneno").equalTo(list_userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String email=null;
                                        String constituency=null;
                                        for (DataSnapshot innershnap:dataSnapshot.getChildren()) {
                                            // people=innner.getValue(People.class);
                                            email = innershnap.child("email").getValue(String.class);
                                            constituency = innershnap.child("constituancy").getValue(String.class);
                                        }
                                        holder.userName.setText("Email:"+email+"\nConstituency:"+constituency);
                                        holder.userStatus.setText("Wants to connects with you");
                                        Log.i("notification",dataSnapshot.toString());

                                        final String finalEmail = email;
                                        holder.Accept.setText("Respond");
                                        holder.Accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[]=new CharSequence[]{
                                                                "Accept",
                                                                "Cancel"
                                                        };
                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle(finalEmail +" Chat Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i)
                                                    {
                                                        if (i==0)
                                                        {
                                                            contanctsRef.child(list_userId).child(currentUserId).child("contacts")
                                                                    .setValue("saved")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                contanctsRef.child(currentUserId).child(list_userId).child("contacts")
                                                                                        .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        requestRef.child(currentUserId).child(list_userId)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            requestRef.child(list_userId).child(currentUserId)
                                                                                                                    .removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                Toast.makeText(getContext(),"new Contact Saved",Toast.LENGTH_LONG).show();


                                                                                                                            }

                                                                                                                        }
                                                                                                                    });
                                                                                                        }

                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });

                                                                            }

                                                                        }
                                                                    });

                                                        }
                                                        if (i==1)
                                                        {
                                                            requestRef.child(currentUserId).child(list_userId)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                requestRef.child(list_userId).child(currentUserId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext()," Contact deleted",Toast.LENGTH_LONG).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }

                                                                        }
                                                                    });
                                                        }

                                                    }

                                                });
                                                builder.create();
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if (trpe.equals("sent"))
                            {
                                holder.Accept.setText("Request sent");
                                userRef.orderByChild("phoneno").equalTo(list_userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String email=null;
                                        String constituency=null;

                                        for (DataSnapshot innershnap:dataSnapshot.getChildren()) {
                                              // people=innner.getValue(People.class);
                                            email = innershnap.child("email").getValue(String.class);
                                            constituency = innershnap.child("constituancy").getValue(String.class);
                                        }
                                        holder.userStatus.setText("Constituency: "+constituency);
                                        holder.userName.setText("you have sent a request to "+email);
                                        holder.Accept.setBackgroundColor(Color.RED);
                                        holder.Accept.setText("Cancel Request");
                                        holder.Accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Cancel Chat Request"
                                                        };
                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle("Already sent request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i)
                                                    {
                                                        if (i==0) {
                                                            requestRef.child(currentUserId).child(list_userId)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                requestRef.child(list_userId).child(currentUserId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext()," Canceled request",Toast.LENGTH_LONG).show();
                                                                                                }

                                                                                            }
                                                                                        });
                                                                            }

                                                                        }
                                                                    });
                                                        }

                                                    }

                                                });

                                                builder.create();
                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

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
        notificationRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        Button Accept;
        public NotificationsViewHolder(View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_notification_email);
            userStatus=itemView.findViewById(R.id.user_notification_constituency);
            Accept=itemView.findViewById(R.id.request_Accept_Button);
        }
    }
}
