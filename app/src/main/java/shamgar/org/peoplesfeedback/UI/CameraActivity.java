package shamgar.org.peoplesfeedback.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import shamgar.org.peoplesfeedback.BuildConfig;
import shamgar.org.peoplesfeedback.Model.Posts;
import shamgar.org.peoplesfeedback.Model.UserAddress;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;
import shamgar.org.peoplesfeedback.ConstantName.*;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.CONSTITUANCY;
import static shamgar.org.peoplesfeedback.ConstantName.NamesC.INDIA;

public class CameraActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG_CAM = CameraActivity.class.getSimpleName();
    //    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 123;
//
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ImageView cameraImage;
    private EditText imageDesc;
    // private TextView imageLoc;
    private Button submit;
//    String provider;

    private FloatingActionButton camera;

    private String tag = "";

    private CheckBox chkGvmc, chkTraffic, chkPolice, chkOthers, chklocality;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private SharedPreferenceConfig sharedPreference;
    private String key;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final String CHAR_LIST =
            "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitude, logntude;

    private boolean checkper=false;

    private String mlaID, MLAname, constituency;
    private UserAddress address;
    ArrayList<String> mlalist = new ArrayList<String>();

    private String imageId;
    private FirebaseAuth mAuth;

    private Spinner spinnerState, spinnerDistrict, spinnerConstituency;
    private ArrayAdapter stateAdapter, districtAdapter, constituencyAdapter;
    private ArrayList<String> state = new ArrayList<>();
    private ArrayList<String> districts = new ArrayList<>();
    private ArrayList<String> constituencies = new ArrayList<>();
    private String currentState, currentDistrict, currentConstituency;

    private Button dialogConfirm,dialogBtnCancel;
    private String mCurrentPhotoPath;


    Location mLocation;
    TextView latLng;
    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ProgressDialog loadingbar;

    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    private Bitmap bitmap;


    @Override
    protected void onStop() {

        super.onStop();

        mRequestingLocationUpdates = false;
//        stopLocationUpdates();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        init();
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        startUpdates();


        loadingbar=new ProgressDialog(this);
        loadingbar.setTitle("Fetching current location with constituency location");
        loadingbar.setMessage("please wait...");
        loadingbar.setCanceledOnTouchOutside(false);


        mlalist.add("select constituency");
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Take Live photo");


        sharedPreference = new SharedPreferenceConfig(this);
        cameraImage = findViewById(R.id.camera_imageView);
        imageDesc = findViewById(R.id.desc);

        submit = findViewById(R.id.submit);
        camera = findViewById(R.id.camera);

        chkGvmc = findViewById(R.id.gvmc);
        chkTraffic = findViewById(R.id.trafficPolice);
        chkPolice = findViewById(R.id.Police);
        chkOthers = findViewById(R.id.others);
        chklocality = findViewById(R.id.locality);

        address = new UserAddress();

        submit.setOnClickListener(this);
        camera.setOnClickListener(this);


        //checking self permissions

            dispatchTakePictureIntent();

//        permissions.add(ACCESS_FINE_LOCATION);
//        permissions.add(ACCESS_COARSE_LOCATION);

//        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (permissionsToRequest.size() > 0)
//                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//        }

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();


    }

    private void startUpdates() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    private void init() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            mLocation = mCurrentLocation;

            // location last updated time
            Log.e("Last updated on: ", ""+ mLastUpdateTime);
        }
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                       // Log.i(TAG_CAM, "All location settings are satisfied.");

                       // Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                              //  Log.i(TAG_CAM, "Location settings are not satisfied. Attempting to upgrade " +
                                    //    "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(CameraActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                  //  Log.i(TAG_CAM, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                              //  Log.e(TAG_CAM, errorMessage);

                                Toast.makeText(CameraActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }



//    public boolean checkLocationPermission() {
//
//        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) &&
//                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//            Toast.makeText(CameraActivity.this, "not Per", Toast.LENGTH_LONG).show();
//
//            ActivityCompat.requestPermissions(CameraActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                    PERMISSIONS_REQUEST_FINE_LOCATION);
//            checkper=true;
//        }
//        return true;
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//
//
//                        //Request location updates:
//                        //locationManager.requestLocationUpdates("gps", 500, 1000, (LocationListener) this);
//                        configureButton();
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//
//                }
//                return;
//            }
//
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.camera:
                dispatchTakePictureIntent();
                break;

            case R.id.submit:
                //check weather image is blank or not
                //check Edit text is blank or not

                if (TextUtils.isEmpty(tag)){
                    Toast.makeText(getApplicationContext(),"please select tag",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bitmap==null){
                    Toast.makeText(getApplicationContext(),"please set image",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCurrentLocation != null) {
                    loadingbar.show();
                    latitude = mCurrentLocation.getLatitude();
                    logntude = mCurrentLocation.getLongitude();
                    checkOwnConstituancyOrNot(latitude, logntude);
                  //  Toast.makeText(this, "Location Find Success", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Location ON Issue", Toast.LENGTH_SHORT).show();
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

                        Location startPoint = new Location("locationA");
                        startPoint.setLatitude(lat);
                        startPoint.setLongitude(lan);

                        Location endPoint = new Location("locationA");
                        endPoint.setLatitude(latitude);
                        endPoint.setLongitude(logntude);

                        double dist = startPoint.distanceTo(endPoint);
                        int distance = (int) (dist / 1000);

                        if (distance >= 33) {
                            loadingbar.dismiss();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                            builder.setTitle("You are out of registered constituency, please select below");
                            builder.setCancelable(false);
                            View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

                            spinnerState = mView.findViewById(R.id.spinnerGetStates);
                            spinnerDistrict = mView.findViewById(R.id.spinnerGetDistricts);
                            spinnerConstituency = mView.findViewById(R.id.spinnerGetConstituencies);
                            dialogBtnCancel = mView.findViewById(R.id.dialogBtnCancel);

                            dialogConfirm = mView.findViewById(R.id.dialogBtnConfirm);
                            builder.setView(mView);
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.setCancelable(false);
                            dialogConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (currentConstituency != "Select constituency" && currentDistrict != "Select district" && currentState != "Select state") {
                                        alertDialog.setCancelable(true);
                                        alertDialog.dismiss();
                                        pushImageToFirebase(1);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "please select fields", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                            dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                   alertDialog.setCancelable(true);
                                   alertDialog.dismiss();
                                }
                            });
                            getStates();




                            //open dailogbox
                            // select constituancy
                           // Log.e("distance greater then 5", "" + distance);
                        } else {
                            loadingbar.dismiss();
                            pushImageToFirebase(0);
                          //  Log.e("distance less then 5", "" + distance);
                        }

                       // Log.e("laf", "" + lat);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        loadingbar.dismiss();
                    }
                });
    }

    private void pushImageToFirebase(final int i) {

        loadingbar.setTitle("Uploading your post");
        loadingbar.setMessage("please wait...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        imageId = FirebaseDatabase.getInstance().getReference().push().getKey();
        imageId = "images/" + imageId + ".jpg";
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
                loadingbar.dismiss();
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
                if (i == 0) {
                    posts = new Posts(
                            sharedPreference.readPhoneNo(),
                            String.valueOf(latitude),
                            String.valueOf(logntude),
                            address.getCity() + ", " + address.getDistrict() + ", " + address.getFulladdress() + ", " + address.getKnownName() + ", " + address.getState(),
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
                if (i == 1) {
                    posts = new Posts(
                            sharedPreference.readPhoneNo(),
                            String.valueOf(latitude),
                            String.valueOf(logntude),
                            address.getCity() + ", " + address.getDistrict() + ", " + address.getFulladdress() + ", " + address.getKnownName() + ", " + address.getState(),
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


                postIntoFirebase(posts, i);

                key = randomString(10);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingbar.dismiss();

            }
        });
    }

    private void postIntoFirebase(Posts posts, final int i) {
        final String postKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                   // Toast.makeText(CameraActivity.this, "Success Post", Toast.LENGTH_SHORT).show();

                    postIntoUserAccount(postKey);
                    postIntoConstituancyAndTaggedArea(postKey, i);
                }
                else {
                    loadingbar.dismiss();
                }
            }
        });

    }

    private void postIntoConstituancyAndTaggedArea(final String postKey, final int i) {
        Date now = new Date();
        HashMap<String, String> messageinfo = new HashMap<>();
        messageinfo.put("id", postKey);
        messageinfo.put("date", now.toString());

        if (i == 0) {
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
                    if (task.isSuccessful()){
                      //  Toast.makeText(CameraActivity.this, "posted in state con", Toast.LENGTH_SHORT).show();
                        postInTagArea(postKey, i);
                    }
                    else {
                        loadingbar.dismiss();
                    }

                }
            });
        } else if (i == 1) {
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
                    if (task.isSuccessful()){
                       // Toast.makeText(CameraActivity.this, "posted in state con", Toast.LENGTH_SHORT).show();
                        postInTagArea(postKey, i);
                    }
                    else {
                        loadingbar.dismiss();
                    }

                }
            });
        }


    }

    private void postInTagArea(String postKey, int i) {
        if (i == 0) {
            FirebaseDatabase.getInstance().getReference().child(INDIA)
                    .child(sharedPreference.readState())
                    .child(sharedPreference.readDistrict())
                    .child(tag)
                    .child("postID")
                    .child(postKey)
                    .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                       // Toast.makeText(CameraActivity.this, "posted in tag area", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                        finish();
                    }
                    else {
                       loadingbar.dismiss();
                    }

                }
            });
        } else if (i == 1) {
            FirebaseDatabase.getInstance().getReference().child(INDIA)
                    .child(currentState)
                    .child(currentDistrict)
                    .child(tag)
                    .child("postID")
                    .child(postKey)
                    .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                      //  Toast.makeText(CameraActivity.this, "posted in tag area", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                        finish();
                    }
                    else {
                        loadingbar.dismiss();
                    }
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
                if (task.isSuccessful()){
                    // Toast.makeText(CameraActivity.this, "success on people post", Toast.LENGTH_SHORT).show();
                }

                else
                    loadingbar.dismiss();
            }
        });
    }

//    private void get_listof_constituency_mla() {
//        Query query = FirebaseDatabase.getInstance().getReference("Politicians")
//                .orderByChild("district")
//                .equalTo(address.getCity());
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // Toast.makeText(getApplicationContext(),"res"+dataSnapshot,Toast.LENGTH_LONG).show();
//                    for (DataSnapshot mlaname : dataSnapshot.getChildren()) {
//                        mlalist.add(mlaname.child("constituancy").getValue().toString());
//                    }
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mlalist);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    editMLatag.setAdapter(adapter);
//                    editMLatag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//                            TextView mlaname = (TextView) adapterView.getSelectedView();
//                            constituency = mlaname.getText().toString();
//                            Query query2 = FirebaseDatabase.getInstance().getReference("Politicians")
//                                    .orderByChild("constituancy")
//                                    .equalTo(constituency);
//                            query2.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot mlaid : dataSnapshot.getChildren()) {
//                                        mlaID = mlaid.child("id").getValue().toString();
//                                        MLAname = mlaid.child("name").getValue().toString();
//                                        //Toast.makeText(getApplicationContext(), "mla name"+dataSnapshot, Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "no results", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
   private void dispatchTakePictureIntent() {
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
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "shamgar.org.peoplesfeedback.fileprovider",
//                        photoFile);
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        this.getApplicationContext()
                                .getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
               // Log.e("uri", photoURI.toString());

            }
        }
    }
//    private Location getcurrentLocation()
//    {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) &&
//                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//            Toast.makeText(CameraActivity.this, "not Per", Toast.LENGTH_LONG).show();
//
//            ActivityCompat.requestPermissions(CameraActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                    PERMISSIONS_REQUEST_FINE_LOCATION);
//
//            return null;
//        }

//        @SuppressLint("MissingPermission")
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
////        double longitude = location.getLongitude();
////        double latitude = location.getLatitude();
//        return location;
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
    // }


//    private void configureButton() {
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//
//            }
//        };
//        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates("gps", 5000, 1000, locationListener);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        Log.e("req",""+requestCode+permissions[0]);
//       switch (requestCode)
//       {
//           case 123:
//            {
//               if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                   configureButton();
//               }
//           }
//       }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:

                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                         bitmap = MediaStore.Images.Media
                                .getBitmap(this.getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            cameraImage.setImageBitmap(bitmap);
                        }
                    }
                    break;

                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                           // Log.e(TAG_CAM, "User agreed to make required location settings changes.");
                            // Nothing to do. startLocationupdates() gets called in onResume again.
                            break;
                        case Activity.RESULT_CANCELED:
                           // Log.e(TAG_CAM, "User chose not to make required location settings changes.");
                            mRequestingLocationUpdates = false;
                            break;
                    }
                    break;
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
        //Log.e("camera", image.toString());
        return image;
    }

    public void chechedTag(View view) {
        switch (view.getId()) {
            case R.id.gvmc:
                chkTraffic.setChecked(false);
                chkPolice.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag = "Municipality";
                break;

            case R.id.trafficPolice:

                chkGvmc.setChecked(false);
                chkPolice.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag = "Traffic";

                break;

            case R.id.Police:

                chkGvmc.setChecked(false);
                chkTraffic.setChecked(false);
                chkOthers.setChecked(false);
                chklocality.setChecked(false);
                this.tag = "Police";
                break;

            case R.id.others:

                chkGvmc.setChecked(false);
                chkPolice.setChecked(false);
                chkTraffic.setChecked(false);
                chklocality.setChecked(false);
                this.tag = "My World";
                break;

            case R.id.locality:

                chkGvmc.setChecked(false);
                chkPolice.setChecked(false);
                chkTraffic.setChecked(false);
                chkOthers.setChecked(false);
                this.tag = "Collector";

        }
    }

    public String randomString(int length) {
        StringBuffer randStr = new StringBuffer(length);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++)
            randStr.append(CHAR_LIST.charAt(secureRandom.nextInt(CHAR_LIST.length())));
        return randStr.toString();
    }

    private void getStates() {
        Query query = FirebaseDatabase.getInstance().getReference().child("States");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    state.clear();
                    state.add("Select state");
                    for (DataSnapshot states : dataSnapshot.getChildren()) {
                        Log.e("states", states.getKey());
                        state.add(states.getKey());
                    }
                    stateAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, state);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerState.setAdapter(stateAdapter);

                    spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            currentState = parent.getSelectedItem().toString();
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
        if (currentState.equalsIgnoreCase("Select state") || currentState.equalsIgnoreCase("All")) {
            districts.clear();
            constituencies.clear();
        }
        Query query = FirebaseDatabase.getInstance().getReference().child("States")
                .child(currentState).child("MLA").child("district");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    districts.clear();
                    districts.add("Select district");
                    for (DataSnapshot states : dataSnapshot.getChildren()) {
                      //  Log.e("states", states.getKey());
                        districts.add(states.getKey());
                    }

                    districtAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, districts);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);

                    spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            currentDistrict = parent.getSelectedItem().toString();
                            getConstituency(currentState, currentDistrict);
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
        if (currentDistrict.equalsIgnoreCase("Select district") || currentDistrict.equalsIgnoreCase("All")) {
            constituencies.clear();
        }
        Query query = FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(currentDistrict).child("Constituancy");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    constituencies.clear();
                    constituencies.add("Select constituency");
                    for (DataSnapshot states : dataSnapshot.getChildren()) {
                      //  Log.e("states", states.getKey());
                        constituencies.add(states.getKey());
                    }
                    constituencyAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, constituencies);
                    constituencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerConstituency.setAdapter(constituencyAdapter);

                    spinnerConstituency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            currentConstituency = adapterView.getSelectedItem().toString();
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

//    @Override
//    public void onLocationChanged(Location location) {
//        if(location!=null)
//           // Toast.makeText(getApplicationContext(),"Latitude : "+location.getLatitude()+" , Longitude : "+location.getLongitude(),Toast.LENGTH_LONG).show();
//            Log.e("location aaaa","Latitude : "+location.getLatitude()+" , Longitude : "+location.getLongitude());
//        Toast.makeText(this, "changed: "+location.getLatitude(), Toast.LENGTH_SHORT).show();
//        mLocation = location;
//    }


//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//
//        if(mLocation!=null)
//        {
//            Log.e("location","Latitude : "+mLocation.getLatitude()+" , Longitude : "+mLocation.getLongitude());
//        }
//
//        startLocationUpdates();
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }

//    protected void startLocationUpdates() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
//        }
//
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location != null){
//                    mLocation = location;
//                    Toast.makeText(getApplicationContext(), "location"+mLocation.getLatitude(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });



//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);


//    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(String.valueOf(permission)) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return ;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CameraActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // stopLocationUpdates();
    }
    public void stopLocationUpdates()
    {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {

            mLocation = mCurrentLocation;
           // Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
           //         + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }else {
//            startLocationUpdates();
//            openSettings();
        }
//        startLocationUpdates();

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            //stopLocationUpdates();
        }
    }





//    public String compressImage(String imageUri) {
//
//        String filePath = getRealPathFromURI(imageUri);
//        Bitmap scaledBitmap = null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
////      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
////      you try the use the bitmap here, you will get null.
//        options.inJustDecodeBounds = true;
//        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//
//        int actualHeight = options.outHeight;
//        int actualWidth = options.outWidth;
//
////      max Height and width values of the compressed image is taken as 816x612
//
//        float maxHeight = 816.0f;
//        float maxWidth = 612.0f;
//        float imgRatio = actualWidth / actualHeight;
//        float maxRatio = maxWidth / maxHeight;
//
////      width and height values are set maintaining the aspect ratio of the image
//
//        if (actualHeight > maxHeight || actualWidth > maxWidth) {
//            if (imgRatio < maxRatio) {
//                imgRatio = maxHeight / actualHeight;
//                actualWidth = (int) (imgRatio * actualWidth);
//                actualHeight = (int) maxHeight;
//            } else if (imgRatio > maxRatio) {
//                imgRatio = maxWidth / actualWidth;
//                actualHeight = (int) (imgRatio * actualHeight);
//                actualWidth = (int) maxWidth;
//            } else {
//                actualHeight = (int) maxHeight;
//                actualWidth = (int) maxWidth;
//
//            }
//        }
//
////      setting inSampleSize value allows to load a scaled down version of the original image
//
//        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
//
////      inJustDecodeBounds set to false to load the actual bitmap
//        options.inJustDecodeBounds = false;
//
////      this options allow android to claim the bitmap memory if it runs low on memory
//        options.inPurgeable = true;
//        options.inInputShareable = true;
//        options.inTempStorage = new byte[16 * 1024];
//
//        try {
////          load the bitmap from its path
//            bmp = BitmapFactory.decodeFile(filePath, options);
//        } catch (OutOfMemoryError exception) {
//            exception.printStackTrace();
//
//        }
//        try {
//            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
//        } catch (OutOfMemoryError exception) {
//            exception.printStackTrace();
//        }
//
//        float ratioX = actualWidth / (float) options.outWidth;
//        float ratioY = actualHeight / (float) options.outHeight;
//        float middleX = actualWidth / 2.0f;
//        float middleY = actualHeight / 2.0f;
//
//        Matrix scaleMatrix = new Matrix();
//        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.setMatrix(scaleMatrix);
//        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//
////      check the rotation of the image and display it properly
//        ExifInterface exif;
//        try {
//            exif = new ExifInterface(filePath);
//
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, 0);
//            Log.d("EXIF", "Exif: " + orientation);
//            Matrix matrix = new Matrix();
//            if (orientation == 6) {
//                matrix.postRotate(90);
//                Log.d("EXIF", "Exif: " + orientation);
//            } else if (orientation == 3) {
//                matrix.postRotate(180);
//                Log.d("EXIF", "Exif: " + orientation);
//            } else if (orientation == 8) {
//                matrix.postRotate(270);
//                Log.d("EXIF", "Exif: " + orientation);
//            }
//            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
//                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
//                    true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        FileOutputStream out = null;
//        String filename = getFilename();
//        try {
//            out = new FileOutputStream(filename);
//
////          write the compressed bitmap at the destination specified by filename.
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return filename;
//
//    }
//
//    public String getFilename() {
//        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
//        return uriSting;
//
//    }
//
//    private String getRealPathFromURI(String contentURI) {
//        Uri contentUri = Uri.parse(contentURI);
//        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
//        if (cursor == null) {
//            return contentUri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            return cursor.getString(index);
//        }
//    }
//
//    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//        }
//        final float totalPixels = width * height;
//        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
//        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//            inSampleSize++;
//        }
//
//        return inSampleSize;
//    }
}
//firebase key
//tag to all option
//
