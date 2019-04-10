package shamgar.org.peoplesfeedback.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import shamgar.org.peoplesfeedback.Adapters.TabsAccessorAdaptor;
import shamgar.org.peoplesfeedback.BuildConfig;
import shamgar.org.peoplesfeedback.Fragments.Chat;
import shamgar.org.peoplesfeedback.Fragments.Home;
import shamgar.org.peoplesfeedback.Fragments.Notifications;
import shamgar.org.peoplesfeedback.Fragments.Politicians;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.AppUpdateDialog;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class HomeScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TabLayout tabLayout;
    private FrameLayout viewPager;
    private TabsAccessorAdaptor tabsAccessorAdaptor;
    FirebaseAuth mAuth;
    SharedPreferenceConfig sharedPreference;
    private RecyclerView horizontal_recycler_view;
    private ArrayList<String> horizontalList;
    private ArrayList<Integer> imagesList;
    private GestureDetector gestureDetector;
    private Toolbar toolbar;
    private DatabaseReference usersRef;
    private String currentUser;

    private CircleImageView profileImage;

    // location updates interval - 2 min
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5*60*1000;

    // fastest updates interval - 1 min
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2*60*1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    SharedPreferenceConfig sharedPreferenceConfig;

    private static double lat, lan;


    final int[] ICONS = new int[]{
            R.drawable.ic_notifications_white_24dp,
            R.drawable.ic_people_black_24dp,
            R.drawable.ic_home_white_24dp
    };
    private String strhome,pol,chat,notifications;

    private static final String FB_RC_KEY_TITLE="update_title";
    private static final String FB_RC_KEY_DESCRIPTION="update_description";
    private static final String FB_RC_KEY_FORCE_UPDATE_VERSION="force_update_version";
    private static final String FB_RC_KEY_LATEST_VERSION="latest_version";
    String TAG = "HomeActivity";

//    AppUpdateDialog appUpdateDialog;

   // FirebaseRemoteConfig mFirebaseRemoteConfig;

//    Timer timer;
//    TimerTask timerTask;
//    final Handler handler = new Handler();

    public HomeScreenActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //checkAppUpdate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        sharedPreference = new SharedPreferenceConfig(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference();

        tabLayout= (TabLayout) findViewById(R.id.maintabs);
        viewPager=(FrameLayout)findViewById(R.id.mainviewpager);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        profileImage=  findViewById(R.id.profileImage);

        Glide.with(this)
                .load(sharedPreference.readPhotoUrl())
                .error(R.drawable.ic_account_circle_black)
                // read original from cache (if present) otherwise download it and decode it
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent user_profile=new Intent(HomeScreenActivity.this,User_profile_Activity.class);
                user_profile.putExtra("mobile",sharedPreference.readPhoneNo());
                startActivity(user_profile);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        imagesList=new ArrayList<>();
        imagesList.add(R.drawable.ic_home_disable);
        imagesList.add(R.drawable.ic_politician_dis);
        imagesList.add(R.drawable.ic_chat_dis);
        imagesList.add(R.drawable.ic_award_dis);



        // tabLayout.setupWithViewPager(viewPager);


        TabsAccessorAdaptor adaptor=new TabsAccessorAdaptor(getSupportFragmentManager());
        tabLayout.setTabsFromPagerAdapter(adaptor);
        setupTabIcons();
        TabLayout.Tab tab=tabLayout.getTabAt(0);
        tab.select();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Home llf = new Home();
        ft.replace(R.id.mainviewpager, llf);
        ft.commit();

        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_home_enbl));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#c2185b"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_home_enbl));
                    tabLayout.getTabAt(1).setIcon(imagesList.get(1));
                    tabLayout.getTabAt(2).setIcon(imagesList.get(2));
                    tabLayout.getTabAt(3).setIcon(imagesList.get(3));

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Home llf = new Home();
                    ft.replace(R.id.mainviewpager, llf);
                    ft.commit();
                }
                if (tab.getPosition()==1){
                    tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_politician_enbl));
                    tabLayout.getTabAt(0).setIcon(imagesList.get(0));
                    tabLayout.getTabAt(2).setIcon(imagesList.get(2));
                    tabLayout.getTabAt(3).setIcon(imagesList.get(3));

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Politicians llf = new Politicians();
                    ft.replace(R.id.mainviewpager, llf);
                    ft.commit();
                }
                if (tab.getPosition()==2){
                    tabLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.ic_chat_enable));
                    tabLayout.getTabAt(1).setIcon(imagesList.get(1));
                    tabLayout.getTabAt(0).setIcon(imagesList.get(0));
                    tabLayout.getTabAt(3).setIcon(imagesList.get(3));
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Chat llf = new Chat();
                    ft.replace(R.id.mainviewpager, llf);
                    ft.commit();
                }
                if (tab.getPosition()==3){
                    tabLayout.getTabAt(3).setIcon(getResources().getDrawable(R.drawable.ic_award_enb));
                    tabLayout.getTabAt(1).setIcon(imagesList.get(1));
                    tabLayout.getTabAt(2).setIcon(imagesList.get(2));
                    tabLayout.getTabAt(0).setIcon(imagesList.get(0));

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Notifications llf = new Notifications();
                    ft.replace(R.id.mainviewpager, llf);
                    ft.commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

//    public void checkAppUpdate() {
//
//        final int versionCode = BuildConfig.VERSION_CODE;
//
//        final HashMap<String, Object> defaultMap = new HashMap<>();
//        defaultMap.put(FB_RC_KEY_TITLE, "Update Available");
//        defaultMap.put(FB_RC_KEY_DESCRIPTION, "A new version of the application is available please click below to update the latest version.");
//        defaultMap.put(FB_RC_KEY_FORCE_UPDATE_VERSION, ""+versionCode);
//        defaultMap.put(FB_RC_KEY_LATEST_VERSION, ""+versionCode);
//
//        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//
//        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build());
//
//        mFirebaseRemoteConfig.setDefaults(defaultMap);
//
//        Task<Void> fetchTask= null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
//            fetchTask = mFirebaseRemoteConfig.fetch(BuildConfig.DEBUG?0: TimeUnit.HOURS.toSeconds(4));
//        }
//
//        fetchTask.addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    // After config data is successfully fetched, it must be activated before newly fetched
//                    // values are returned.
//                    mFirebaseRemoteConfig.activateFetched();
//
//                    String title=getValue(FB_RC_KEY_TITLE,defaultMap);
//                    String description=getValue(FB_RC_KEY_DESCRIPTION,defaultMap);
//                    int forceUpdateVersion= Integer.parseInt(getValue(FB_RC_KEY_FORCE_UPDATE_VERSION,defaultMap));
//                    int latestAppVersion= Integer.parseInt(getValue(FB_RC_KEY_LATEST_VERSION,defaultMap));
//
//                    boolean isCancelable=true;
//
//                    if(latestAppVersion>versionCode)
//                    {
//                        if(forceUpdateVersion>versionCode)
//                            isCancelable=false;
//
//                        appUpdateDialog = new AppUpdateDialog(HomeScreenActivity.this, title, description, isCancelable);
//                        appUpdateDialog.setCancelable(false);
//                        appUpdateDialog.show();
//
//                        Window window = appUpdateDialog.getWindow();
//                        assert window != null;
//                        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//
//                    }
//
//                } else {
//                    Toast.makeText(HomeScreenActivity.this, "Fetch Failed",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(imagesList.get(0));
        tabLayout.getTabAt(1).setIcon(imagesList.get(1));
        tabLayout.getTabAt(2).setIcon(imagesList.get(2));
        tabLayout.getTabAt(3).setIcon(imagesList.get(3));
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Home(), "Home");
        adapter.addFrag(new Politicians(), "Politicians");
        adapter.addFrag(new Chat(), "Chat");
        adapter.addFrag(new Notifications(), "Notification");
        viewPager.setAdapter(adapter);
    }

//    public String getValue(String parameterKey,HashMap<String, Object> defaultMap)
//    {
//        String value=mFirebaseRemoteConfig.getString(parameterKey);
//        if(TextUtils.isEmpty(value))
//            value= (String) defaultMap.get(parameterKey);
//
//        return value;
//    }




//    @Override
//    public boolean onDown(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//    }
//
//    @Override
//    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
//        boolean result=false;
//        float diffy=moveEvent.getY()-downEvent.getY();
//        float diffx=moveEvent.getX()-downEvent.getX();
//        if (Math.abs(diffx)>Math.abs(diffy)) {
//            //swipe left right
//
//            if (Math.abs(diffx)>100 && Math.abs(velocityX)>100)
//            {
//                if (diffx>0)
//                {
//                    onSwipeRight();
//                }
//                else
//                {
//                    onSwipeLeft();
//                }
//                result=true;
//            }
//        }
//        else
//        {
//            //swipe up down
//        }
//        return result;
//    }
//
//    private void onSwipeRight() {
//        Toast.makeText(getApplicationContext(), "Swipe to the right",
//                Toast.LENGTH_SHORT).show();
//
//    }
//
//    private void onSwipeLeft()
//    {
////        Home home=new Home();
////        Politicians politicians=new Politicians();
////        Fragment homeFragment=getHomeFragment(home);
////        Fragment PoliticiansFragment=getPoliticiansFragment(politicians);
////        Fragment chatFragment=getChatFragment(new Chat());
////        Fragment NotificationFragment=getNotificationsFragment(new Notifications());
//        Toast.makeText(getApplicationContext(), "Swipe to the left",
//                Toast.LENGTH_SHORT).show();
//
////         strhome=homeFragment.toString();
//        // pol=PoliticiansFragment.getTag().toString();
//
//
//        //   chat=chatFragment.toString();
//        // notifications=NotificationFragment.toString();
//
////        if (strhome.contains("Home"))
////        {
////                getSupportFragmentManager().beginTransaction()
////                                .replace(R.id.frame_container, new Politicians(),"Politicians")
////                                .commit();
////        }
//
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        gestureDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.home_screen_menu_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.contacts) {
            Intent contacts=new Intent(HomeScreenActivity.this,ContactsActivity.class);
            startActivity(contacts);
        }
        if (item.getItemId()==R.id.profile){
            Intent user_profile=new Intent(HomeScreenActivity.this,User_profile_Activity.class);
            user_profile.putExtra("mobile",sharedPreference.readPhoneNo());
            startActivity(user_profile);

        }
        if (item.getItemId()==R.id.sign_out){
            mAuth.signOut();
            Intent signOut=new Intent(HomeScreenActivity.this,MainActivity.class);
            startActivity(signOut);
            finish();
        }

        return true;
    }

    //updating user status that is user is online or offline
    private void updateUserStatus(String state)
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        HashMap<String,Object> onlineStateMap=new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        usersRef.child("people").child(sharedPreference.readPhoneNo().substring(3))
                .child("userState")
                .updateChildren(onlineStateMap);



    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferenceConfig = new SharedPreferenceConfig(this);
        checkLocationPermission();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser==null){
            Intent mainActiity=new Intent(HomeScreenActivity.this,MainActivity.class);
            startActivity(mainActiity);
            finish();
        }
        else {
            updateUserStatus("online");

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentUser!=null){
            updateUserStatus("offline");
        }
        if(mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void checkLocationPermission() {
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            int locationRequestCode = 11;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted

            EnableGPSAutoMatically();
//            startLocationUpdates();

        }
    }

    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            //Toast.makeText(HomeScreenActivity.this, "success on", Toast.LENGTH_SHORT).show();
                            // All location settings are satisfied.
                            //Schedule the job
                            startLocationUpdates();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            Toast.makeText(HomeScreenActivity.this, "GPS is not on", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(HomeScreenActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(HomeScreenActivity.this, "Setting change not allowed", Toast.LENGTH_SHORT).show();
                            // Location settings are not satisfied.
                            break;
                    }
                }
            });
        }
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 11:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location Granted. Schedule the background Job.
                    startLocationUpdates();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Use Location CallBack if fusedLocation returns null
        // This is required because, fusedLocationProviderClient gives result few time in an hour on devices higher then Nouget
        // in background
        mLocationCallback = new LocationCallback(){
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lan = location.getLongitude();
                        sharedPreferenceConfig.setLatitude(String.valueOf(lat));
                        sharedPreferenceConfig.setLongitude(String.valueOf(lan));


                    }else {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

                    }
                }
                // remove once the location received
                // No need to run it in interval


            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                // Once the GPS on again. Schedule the background job
                startLocationUpdates();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }



}