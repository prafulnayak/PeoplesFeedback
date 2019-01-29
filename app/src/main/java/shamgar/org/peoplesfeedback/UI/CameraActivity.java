package shamgar.org.peoplesfeedback.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import shamgar.org.peoplesfeedback.Model.Posts;
import shamgar.org.peoplesfeedback.Model.UserAddress;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;
import shamgar.org.peoplesfeedback.ConstantName.*;

import static shamgar.org.peoplesfeedback.ConstantName.NamesC.CONSTITUANCY;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.INDIA;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.PEOPLE;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener   {

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    private ImageView cameraImage;
    private EditText imageDesc;
    private Spinner editMLatag;
    // private TextView imageLoc;
    private Button submit;

    private FloatingActionButton camera;

    private String tag = "";

    private CheckBox chkGvmc, chkTraffic, chkPolice, chkOthers,chklocality;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private SharedPreferenceConfig sharedPreference;
    private String key;

    private static final String CHAR_LIST =
            "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude,logntude;

    private String  mlaID,MLAname,constituency;
    private UserAddress address;
    ArrayList<String> mlalist=new ArrayList<String>();

    private String imageId;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mlalist.add("select constituency");
        mAuth=FirebaseAuth.getInstance();


        sharedPreference = new SharedPreferenceConfig(this);
        cameraImage = findViewById(R.id.camera_imageView);
        imageDesc = findViewById(R.id.desc);
        editMLatag = findViewById(R.id.editMLatag);
        submit = findViewById(R.id.submit);
        camera = findViewById(R.id.camera);

        chkGvmc = findViewById(R.id.gvmc);
        chkTraffic = findViewById(R.id.trafficPolice);
        chkPolice = findViewById(R.id.Police);
        chkOthers = findViewById(R.id.others);
        chklocality = findViewById(R.id.locality);


        editMLatag.setVisibility(View.GONE);
        address=new UserAddress();

        submit.setOnClickListener(this);
        camera.setOnClickListener(this);

        dispatchTakePictureIntent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

              case R.id.camera:
                  dispatchTakePictureIntent();
                  break;

            case R.id.submit:
                //check weather image is blank or not
                //check Edit text is blank or not
                Location location = getcurrentLocation();
                if(location!=null){
                    latitude = location.getLatitude();
                    logntude = location.getLongitude();
                    pushImageToFirebase();
                }else {
                    Toast.makeText(this, "Location ON Issue",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void pushImageToFirebase() {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        imageId = FirebaseDatabase.getInstance().getReference().push().getKey();
        imageId = "images/"+imageId+".jpg";
        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child(imageId);

        // Get the data from an ImageView as bytes
        cameraImage.setDrawingCacheEnabled(true);
        cameraImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) cameraImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CameraActivity.this, "success image posted", Toast.LENGTH_SHORT).show();
//                Log.e("gggg", "sssssssd");

                //get the url for the image
                getUrlForDownload();
            }
        });
    }

    private void getUrlForDownload() {

        final Posts posts = new Posts();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://peoplesfeedback-124ba.appspot.com/");
        final StorageReference pathReference = storageRef.child(imageId);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

               // Toast.makeText(CameraActivity.this, "" + pathReference.getDownloadUrl(), Toast.LENGTH_SHORT).show();
                Log.e("url", "" + uri.toString());

                String des = imageDesc.getText().toString();
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(Calendar.getInstance().getTime());
                Posts posts = new Posts(
                        sharedPreference.readPhoneNo(),
                        String.valueOf(latitude),
                        String.valueOf(logntude),
                        address.getCity()+", "+address.getDistrict()+", "+address.getFulladdress()+", "+address.getKnownName()+", "+address.getState(),
                        uri.toString(),
                        des,
                        des,
                        timeStamp,
                        tag,mAuth.getCurrentUser().getUid());

                postIntoFirebase(posts);

                key = randomString(10);


//                DatabaseReference mlaReference = FirebaseDatabase.getInstance().getReference("states/" + sharedPreference.readState() + "/" + sharedPreference.readDistrict() + "/con/" + sharedPreference.readConstituancy());
////                mlaReference.child("MLAID").child("id").setValue(mlaID).addOnCompleteListener(new OnCompleteListener<Void>()
////                {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task)
////                    {
////                        if (task.isSuccessful()) {
//////                         Toast.makeText(CameraActivity.this, "mla id added: ", Toast.LENGTH_LONG).show();
////
////
////                        } else
////                            Toast.makeText(CameraActivity.this, "mla id added failed: ", Toast.LENGTH_LONG).show();
////
////                    }
////                });



//                DatabaseReference TagReference = FirebaseDatabase.getInstance().getReference("states/" + sharedPreference.readState() + "/" + sharedPreference.readDistrict());
//                TagReference.child(tag).child(key).setValue("postid").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task)
//                    {
//                        if (task.isSuccessful()) {
////                           Toast.makeText(CameraActivity.this, "Tag success: ", Toast.LENGTH_LONG).show();
//
//
//                        } else
//                            Toast.makeText(CameraActivity.this, "Tag Fail: ", Toast.LENGTH_LONG).show();
//                    }
//                });





//                DatabaseReference PostReference = FirebaseDatabase.getInstance().getReference("states/" + sharedPreference.readState() + "/" + sharedPreference.readDistrict() + "/con/" + sharedPreference.readConstituancy());
//                PostReference.child("PostID").child(key).setValue("postid");
//
//
//                PostReference.addListenerForSingleValueEvent(new ValueEventListener()
//                {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Posts").child("Z6zi5Dl0hj");
////                        Map<String, Object> updates = new HashMap<String,Object>();
////                        updates.put("description", "hello");
////                        updates.put("mla", "hello");
////                        updates.put("tag", "hello");
////                        updates.put("url", "hello");
////                        ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
////                            @Override
////                            public void onSuccess(Void aVoid) {
////
////                            }
////                        });
//
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
////                        databaseReference.child("Posts").child(key).setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
////
////
////                            @Override
////                            public void onComplete(@NonNull Task<Void> task) {
////
////
////                                if (task.isSuccessful())
////                                {
//////                                  Toast.makeText(CameraActivity.this, "post success: ", Toast.LENGTH_LONG).show();
////                                    finish();
////
////                                } else
////                                {
////                                     Toast.makeText(CameraActivity.this, "post Fail: ", Toast.LENGTH_LONG).show();
////                                }
////
////                            }
////                        });
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void postIntoFirebase(Posts posts) {
        final String postKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CameraActivity.this,"Success Post",Toast.LENGTH_SHORT).show();

                    postIntoUserAccount(postKey);
                    postIntoConstituancyAndTaggedArea(postKey);
                }
            }
        });

    }

    private void postIntoConstituancyAndTaggedArea(final String postKey)
    {
        Date now = new Date();
        HashMap<String,String> messageinfo=new HashMap<>();
        messageinfo.put("id",postKey);
        messageinfo.put("date",now.toString());

        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .child("PostID")
                .child(postKey)
                .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CameraActivity.this,"posted in state con",Toast.LENGTH_SHORT).show();
                postInTagArea(postKey);
            }
        });
    }

    private void postInTagArea(String postKey) {
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(tag)
                .child("postID")
                .child(postKey)
                .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CameraActivity.this,"posted in tag area",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void postIntoUserAccount(String postKey) {
        FirebaseDatabase.getInstance().getReference().child("people")
                .child(sharedPreference.readPhoneNo().substring(3))
                .child(NamesC.POSTEDPOST)
                .child(postKey)
                .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(CameraActivity.this,"success on people post",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void get_listof_constituency_mla() {
        Query query=FirebaseDatabase.getInstance().getReference("Politicians")
                .orderByChild("district")
                .equalTo(address.getCity());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                  // Toast.makeText(getApplicationContext(),"res"+dataSnapshot,Toast.LENGTH_LONG).show();
                     for(DataSnapshot mlaname : dataSnapshot.getChildren())
                    {
                        mlalist.add(mlaname.child("constituancy").getValue().toString());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,mlalist);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    editMLatag.setAdapter(adapter);
                    editMLatag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
                        {
                            TextView mlaname = (TextView)adapterView.getSelectedView();
                            constituency = mlaname.getText().toString();
                            Query query2=FirebaseDatabase.getInstance().getReference("Politicians")
                                    .orderByChild("constituancy")
                                    .equalTo(constituency);
                            query2.addValueEventListener(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    for(DataSnapshot mlaid : dataSnapshot.getChildren())
                                    {
                                        mlaID=mlaid.child("id").getValue().toString();
                                        MLAname=mlaid.child("name").getValue().toString();
                                        //Toast.makeText(getApplicationContext(), "mla name"+dataSnapshot, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {

                        }
                    });

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"no results",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }
    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private Location getcurrentLocation()
    {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(CameraActivity.this, "not Per", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return null;
        }

        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        return location;
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location)
//            {
//
//                 latitude=location.getLatitude();
//                 logntude=location.getLongitude();
//
//                Toast.makeText(getApplicationContext(),"location   ",Toast.LENGTH_LONG).show();
//
//                Geocoder geocoder;
//                List<Address> addresses = null;
//                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//
//                try {
//                    addresses = geocoder.getFromLocation(latitude, logntude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//               address.setFulladdress(addresses.get(0).getAddressLine(0));
//               address.setCity(addresses.get(0).getLocality());
//               address.setState(addresses.get(0).getAdminArea());
//               address.setCountry(addresses.get(0).getCountryName());
//               address.setPostalCode(addresses.get(0).getPostalCode());
//               address.setKnownName(addresses.get(0).getFeatureName());
//               address.setDistrict(addresses.get(0).getSubAdminArea());
//
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider)
//            {
//                Intent settings=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(settings);
//            }
//        };
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
//                },10);
//
//
//
//                return;
//            }
//            else
//            {
//                configureButton();
//            }
//        }
    }

    @SuppressLint("MissingPermission")
    private void configureButton()
    {
        locationManager.requestLocationUpdates("gps", 5000, 1000, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
       switch (requestCode)
       {
           case 10:
            {
               if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED);
               configureButton();
               return;
           }
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cameraImage.setImageBitmap(imageBitmap);
        }
    }

    public void chechedTag(View view) {
        switch (view.getId()) {
            case R.id.gvmc:
                chkTraffic.setChecked(false);
                chkPolice.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag="GVMC";
                break;

            case R.id.trafficPolice:

                chkGvmc.setChecked(false);
                chkPolice.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag="Traffic";
                break;

            case R.id.Police:

                chkGvmc.setChecked(false);
                chkTraffic.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag="Police";
                break;

            case R.id.others:

                chkGvmc.setChecked(false);
                chkPolice.setChecked(false);
                chkTraffic.setChecked(false);
                chklocality.setChecked(false);

                this.tag="Others";
                break;

            case R.id.locality:

                chkGvmc.setChecked(false);
                chkPolice.setChecked(false);
                chkTraffic.setChecked(false);
                chkOthers.setChecked(false);

                this.tag="Locality";
        }
    }

    public String randomString(int length){
        StringBuffer randStr = new StringBuffer(length);
        SecureRandom secureRandom = new SecureRandom();
        for( int i = 0; i < length; i++ )
            randStr.append( CHAR_LIST.charAt( secureRandom.nextInt(CHAR_LIST.length()) ) );
        return randStr.toString();
    }




}
//firebase key
//tag to all option
//
