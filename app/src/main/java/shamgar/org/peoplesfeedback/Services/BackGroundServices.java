package shamgar.org.peoplesfeedback.Services;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import shamgar.org.peoplesfeedback.ConstantName.NamesC;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.MainActivity;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BackGroundServices extends JobService implements NamesC {


    private DatabaseReference dbRefLike;

    NotificationManager mNotifyManager;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";


    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        dbRefLike= FirebaseDatabase.getInstance().getReference().child("Posts");

        String postId = jobParameters.getExtras().getString("postId");
        String phoneNo = jobParameters.getExtras().getString("phoneNo");
        final String typePost = jobParameters.getExtras().getString("typePost");

//        createNotificationChannel();
//
//        //Set up the notification content intent to launch the app when clicked
//        PendingIntent contentPendingIntent = PendingIntent.getActivity
//                (this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder
//                (this, PRIMARY_CHANNEL_ID)
//                .setContentTitle("Job Service")
//                .setContentText("Your Job ran to completion!"+postId+phoneNo)
//                .setContentIntent(contentPendingIntent)
//                .setSmallIcon(R.drawable.ic_eye)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setAutoCancel(true);
//
////        mNotifyManager.notify(0, builder.build());
//        Toast.makeText(this, "onStart() is called", Toast.LENGTH_SHORT).show();
        if(postId != null && phoneNo != null && typePost != null){
            if(typePost.equals(Share) || typePost.equals(viewC)){
                dbRefLike.child(postId).child(typePost).push().setValue(phoneNo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Log.e("type post",""+typePost);
                    }
                });
            }else {
                dbRefLike.child(postId).child(typePost).child(phoneNo).setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        mNotifyManager.notify(0, builder.build());
//                        Toast.makeText(BackGroundServices.this, "liked Success", Toast.LENGTH_SHORT).show();
                        jobFinished(jobParameters,false);
                       // Log.e("type post",""+typePost);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        jobFinished(jobParameters,true);

                    }
                });
            }

        }else {
            jobFinished(jobParameters,false);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
//        Toast.makeText(this, "onStopJob() is called", Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Define notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Job Service notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifications from Job Service");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
