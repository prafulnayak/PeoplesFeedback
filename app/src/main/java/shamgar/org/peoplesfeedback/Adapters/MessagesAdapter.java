package shamgar.org.peoplesfeedback.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    private String image;
    private Context context;

    public MessagesAdapter(Context applicationContext, List<Messages> userMessagesList, String messageImage) {
        this.userMessagesList = userMessagesList;
        this.image=messageImage;
        this.context=applicationContext;
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
        Glide.with(context)
                .load(image)
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.receiverProfileImage);
        Messages messages=userMessagesList.get(position);

        String fromUserId=messages.getFrom();
        String fromMessageType=messages.getType();
        String time=messages.getDate()+"  "+messages.getTime();

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
            holder.receiver_messages_time.setVisibility(View.INVISIBLE);
            holder.sender_messages_time.setVisibility(View.INVISIBLE);
            holder.sendMessagesText.setVisibility(View.INVISIBLE);
            if (fromUserId.equals(messageSenderId)){
                holder.sendMessagesText.setVisibility(View.VISIBLE);
                holder.sender_messages_time.setVisibility(View.VISIBLE);
                holder.sendMessagesText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.sendMessagesText.setTextColor(Color.WHITE);
                holder.sendMessagesText.setText(messages.getMessage());
                holder.sender_messages_time.setText(time);
            }
            else {


                holder.receiveMessagesText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiver_messages_time.setVisibility(View.VISIBLE);

                holder.receiveMessagesText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiveMessagesText.setTextColor(Color.BLACK);
                holder.receiveMessagesText.setText(messages.getMessage());
                holder.receiver_messages_time.setText(time);

            }
        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        public TextView sendMessagesText,receiveMessagesText,receiver_messages_time,sender_messages_time;
        public CircleImageView receiverProfileImage;

        public MessagesViewHolder(View itemView) {
            super(itemView);

            sendMessagesText=itemView.findViewById(R.id.sender_messages);
            receiveMessagesText=itemView.findViewById(R.id.receiver_messages);
            receiverProfileImage=itemView.findViewById(R.id.message_profile_image);
            receiver_messages_time=itemView.findViewById(R.id.receiver_messages_time);
            sender_messages_time=itemView.findViewById(R.id.sender_messages_time);



        }
    }
}
