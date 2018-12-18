package shamgar.org.peoplesfeedback.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Adapters.MessagesAdapter;
import shamgar.org.peoplesfeedback.Model.Messages;
import shamgar.org.peoplesfeedback.R;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverId,messageReceiverName,messageImage,messageSenderId;
    private TextView userEmail,userLastSeen;
    private CircleImageView userImage;
    private ImageButton messageSendButton;
    private EditText chatActivityMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MessagesAdapter adapter;
    private RecyclerView messagesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        messageSenderId=mAuth.getCurrentUser().getPhoneNumber();
        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_email_id").toString();
        messageImage=getIntent().getExtras().get("visit_image").toString();
        init();

        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        adapter=new MessagesAdapter(messagesList);
        layoutManager=new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(adapter);

    }



    private void init() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_chat_bar);
        userEmail=(TextView)findViewById(R.id.emailChatActivity);
        userLastSeen=(TextView)findViewById(R.id.lastSeenChatActivity);
        userImage=(CircleImageView) findViewById(R.id.chat_profile_image);
        userEmail.setText(messageReceiverName);
        Picasso.get().load(messageImage).placeholder(R.drawable.profile).into(userImage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        messageSendButton=(ImageButton)findViewById(R.id.messageSendButton);
        chatActivityMessage=(EditText)findViewById(R.id.chatActivityMessage);
        messagesRecyclerView=(RecyclerView) findViewById(R.id.chatRecyclerView);
    }
    private void sendMessage() {
        String message=chatActivityMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(),"please input Text",Toast.LENGTH_LONG).show();
        }
        else {
            String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
            String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;

            DatabaseReference userMessageKeyRef=rootRef.child("Messages")
                    .child(messageSenderId)
                    .child(messageReceiverId)
                    .push();
            String messagePushId=userMessageKeyRef.getKey();
            Map messageTextBody=new HashMap();
            messageTextBody.put("message",message);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushId,messageTextBody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagePushId,messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),"message sent successfully",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    }

                    chatActivityMessage.setText("");

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        adapter.notifyDataSetChanged();
                        messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
