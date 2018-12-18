package shamgar.org.peoplesfeedback.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Model.Messages;
import shamgar.org.peoplesfeedback.R;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public MessagesAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
       mAuth=FirebaseAuth.getInstance();



       return new MessagesViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, int position) {

        String messageSenderId=mAuth.getCurrentUser().getPhoneNumber();
        Messages messages=userMessagesList.get(position);

        String fromUserId=messages.getFrom();
        String fromMessageType=messages.getType();

        usersRef=FirebaseDatabase.getInstance().getReference().child("people");
        usersRef.orderByChild("phoneno").equalTo(fromUserId)
                 .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {
                    String receiverImage=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile).into(holder.receiverProfileImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")){
            holder.receiveMessagesText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            holder.sendMessagesText.setVisibility(View.INVISIBLE);
            if (fromUserId.equals(messageSenderId)){
                holder.sendMessagesText.setVisibility(View.VISIBLE);
                holder.sendMessagesText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.sendMessagesText.setTextColor(Color.BLACK);
                holder.sendMessagesText.setText(messages.getMessage());
            }
            else {


                holder.receiveMessagesText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                holder.receiveMessagesText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiveMessagesText.setTextColor(Color.BLACK);
                holder.receiveMessagesText.setText(messages.getMessage());

            }
        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        public TextView sendMessagesText,receiveMessagesText;
        public CircleImageView receiverProfileImage;

        public MessagesViewHolder(View itemView) {
            super(itemView);

            sendMessagesText=itemView.findViewById(R.id.sender_messages);
            receiveMessagesText=itemView.findViewById(R.id.receiver_messages);
            receiverProfileImage=itemView.findViewById(R.id.message_profile_image);



        }
    }
}
