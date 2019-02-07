package shamgar.org.peoplesfeedback.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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
import java.io.File;
import java.io.IOException;
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
    static final int REQUEST_TAKE_PHOTO = 1;

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

    private Spinner spinnerState,spinnerDistrict,spinnerConstituency;
    private ArrayAdapter stateAdapter,districtAdapter,constituencyAdapter;
    private ArrayList<String> state=new ArrayList<>();
    private ArrayList<String> districts=new ArrayList<>();
    private ArrayList<String> constituencies=new ArrayList<>();
    private String currentState,currentDistrict,currentConstituency;

    private Button dialogConfirm;
    private String mCurrentPhotoPath;


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
                    checkOwnConstituancyOrNot(latitude,logntude);

                }else {
                    Toast.makeText(this, "Location ON Issue",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void checkOwnConstituancyOrNot(final double latitude, final double logntude) {
        FirebaseDatabase.getInstance().getReference().child(INDIA)
                .child(sharedPreference.readState())
                .child(sharedPreference.readDistrict())
                .child(CONSTITUANCY)
                .child(sharedPreference.readConstituancy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double lat = (double) dataSnapshot.child("latitude").getValue();
                        double lan = (double) dataSnapshot.child("longitude").getValue();

                        Location startPoint=new Location("locationA");
                        startPoint.setLatitude(lat);
                        startPoint.setLongitude(lan);

                        Location endPoint=new Location("locationA");
                        endPoint.setLatitude(latitude);
                        endPoint.setLongitude(logntude);

                        double dist=startPoint.distanceTo(endPoint);
                        int distance = (int)(dist/1000);

                        if(distance<=33){
                            AlertDialog.Builder builder=new AlertDialog.Builder(CameraActivity.this);
                            builder.setTitle("You are out of registered constituency, please select below");
                            View mView=getLayoutInflater().inflate(R.layout.custom_dialog,null);

                            spinnerState=mView.findViewById(R.id.spinnerGetStates);
                            spinnerDistrict=mView.findViewById(R.id.spinnerGetDistricts);
                            spinnerConstituency=mView.findViewById(R.id.spinnerGetConstituencies);

                            dialogConfirm=mView.findViewById(R.id.dialogBtnConfirm);
                            dialogConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (currentConstituency != "Select constituency" && currentDistrict != "Select district" && currentState!="Select state"){
                                        Toast.makeText(getApplicationContext(),"post into desired constituency",Toast.LENGTH_LONG).show();
                                        pushImageToFirebase(1);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"please select fields",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                            getStates();
                            builder.setView(mView);
                            AlertDialog alertDialog=builder.create();
                            alertDialog.show();

                            //open dailogbox
                            // select constituancy
                            Log.e("distance greater then 5",""+distance);
                        }else {
                            pushImageToFirebase(0);
                            Log.e("distance less then 5",""+distance);
                        }

                        Log.e("laf",""+lat);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void pushImageToFirebase(final int i) {

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
                getUrlForDownload(i);
            }
        });
    }

    private void getUrlForDownload(final int i) {

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
                Posts posts = null;
                if(i==0){
                    posts = new Posts(
                            sharedPreference.readPhoneNo(),
                            String.valueOf(latitude),
                            String.valueOf(logntude),
                            address.getCity()+", "+address.getDistrict()+", "+address.getFulladdress()+", "+address.getKnownName()+", "+address.getState(),
                            uri.toString(),
                            des,
                            des,
                            timeStamp,
                            tag,
                            mAuth.getCurrentUser().getUid(),
                            sharedPreference.readState(),
                            sharedPreference.readDistrict(),
                            sharedPreference.readConstituancy());
                }
                if(i == 1){
                    posts = new Posts(
                            sharedPreference.readPhoneNo(),
                            String.valueOf(latitude),
                            String.valueOf(logntude),
                            address.getCity()+", "+address.getDistrict()+", "+address.getFulladdress()+", "+address.getKnownName()+", "+address.getState(),
                            uri.toString(),
                            des,
                            des,
                            timeStamp,
                            tag,
                            mAuth.getCurrentUser().getUid(),
                            currentState,
                            currentDistrict,
                            currentConstituency);
                }


                postIntoFirebase(posts,i);

                key = randomString(10);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void postIntoFirebase(Posts posts, final int i) {
        final String postKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CameraActivity.this,"Success Post",Toast.LENGTH_SHORT).show();

                    postIntoUserAccount(postKey);
                    postIntoConstituancyAndTaggedArea(postKey,i);
                }
            }
        });

    }

    private void postIntoConstituancyAndTaggedArea(final String postKey, final int i)
    {
        Date now = new Date();
        HashMap<String,String> messageinfo=new HashMap<>();
        messageinfo.put("id",postKey);
        messageinfo.put("date",now.toString());

        if (i==0) {
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
                    Toast.makeText(CameraActivity.this, "posted in state con", Toast.LENGTH_SHORT).show();
                    postInTagArea(postKey,i);
                }
            });
        }
        else if (i==1){
            FirebaseDatabase.getInstance().getReference().child(INDIA)
                    .child(currentState)
                    .child(currentDistrict)
                    .child(CONSTITUANCY)
                    .child(currentConstituency)
                    .child("PostID")
                    .child(postKey)
                    .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(CameraActivity.this, "posted in state con", Toast.LENGTH_SHORT).show();
                    postInTagArea(postKey, i);
                }
            });
            }



    }

    private void postInTagArea(String postKey, int i) {
        if (i==0){
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
        else if (i==1){
            FirebaseDatabase.getInstance().getReference().child(INDIA)
                    .child(currentState)
                    .child(currentDistrict)
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "shamgar.org.peoplesfeedback.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE );
                Log.e("uri",photoURI.toString());

            }
        }
    }
    private Location getcurrentLocation()
    {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(CameraActivity.this, "not Per", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);

            return null;
        }

        @SuppressLint("MissingPermission")
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
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
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 1: {

                    if (resultCode == RESULT_OK ) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(this.getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            cameraImage.setImageBitmap(bitmap);
                        }
                    }
                    break;
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("camera",image.toString());
        return image;
    }

    public void chechedTag(View view) {
        switch (view.getId()) {
            case R.id.gvmc:
                chkTraffic.setChecked(false);
                chkPolice.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag="Municipality";
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
                this.tag="Collector";

        }
    }

    public String randomString(int length){
        StringBuffer randStr = new StringBuffer(length);
        SecureRandom secureRandom = new SecureRandom();
        for( int i = 0; i < length; i++ )
            randStr.append( CHAR_LIST.charAt( secureRandom.nextInt(CHAR_LIST.length()) ) );
        return randStr.toString();
    }

    private void getStates(){
        Query query= FirebaseDatabase.getInstance().getReference().child("States");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    state.clear();
                    state.add("Select state");
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        state.add(states.getKey());
                    }
                    stateAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,state);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerState.setAdapter(stateAdapter);

                    spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            currentState=parent.getSelectedItem().toString();
                            getDistricts(currentState);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void getDistricts(final String currentState) {
        if (currentState.equalsIgnoreCase("Select state") || currentState.equalsIgnoreCase("All")){
            districts.clear();
            constituencies.clear();
        }
        Query query= FirebaseDatabase.getInstance().getReference().child("States")
                .child(currentState).child("MLA").child("district");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    districts.clear();
                    districts.add("Select district");
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        districts.add(states.getKey());
                    }

                    districtAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,districts);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);

                    spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            currentDistrict=parent.getSelectedItem().toString();
                            getConstituency(currentState,currentDistrict);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void getConstituency(String state, String currentDistrict) {
        if (currentDistrict.equalsIgnoreCase("Select district") || currentDistrict.equalsIgnoreCase("All") ){
            constituencies.clear();
        }
        Query query= FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(currentDistrict).child("Constituancy");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    constituencies.clear();
                    constituencies.add("Select constituency");
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        constituencies.add(states.getKey());
                    }
                    constituencyAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,constituencies);
                    constituencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerConstituency.setAdapter(constituencyAdapter);

                    spinnerConstituency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            currentConstituency=adapterView.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }



}
//firebase key
//tag to all option
//
