package shamgar.org.peoplesfeedback.UI;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import shamgar.org.peoplesfeedback.Adapters.TabsAccessorAdaptor;
import shamgar.org.peoplesfeedback.Fragments.Chat;
import shamgar.org.peoplesfeedback.Fragments.Home;
import shamgar.org.peoplesfeedback.Fragments.Notifications;
import shamgar.org.peoplesfeedback.Fragments.Politicians;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class HomeScreenActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
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



    final int[] ICONS = new int[]{
                    R.drawable.ic_notifications_white_24dp,
                    R.drawable.ic_people_black_24dp,
                    R.drawable.ic_home_white_24dp
            };
    private String strhome,pol,chat,notifications;

    public HomeScreenActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        sharedPreference = new SharedPreferenceConfig(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference();

        tabLayout= (TabLayout) findViewById(R.id.maintabs);
        viewPager=(ViewPager)findViewById(R.id.mainviewpager);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagesList=new ArrayList<>();
        imagesList.add(R.drawable.ic_home_white_24dp);
        imagesList.add(R.drawable.ic_people_black_24dp);
        imagesList.add(R.drawable.ic_chat_black_24dp);
        imagesList.add(R.drawable.ic_notifications_white_24dp);


        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }
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
}
