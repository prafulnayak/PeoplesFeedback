package shamgar.org.peoplesfeedback.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;

import shamgar.org.peoplesfeedback.Adapters.HorizontalAdapter;
import shamgar.org.peoplesfeedback.Adapters.TabsAccessorAdaptor;
import shamgar.org.peoplesfeedback.Fragments.Chat;
import shamgar.org.peoplesfeedback.Fragments.Home;
import shamgar.org.peoplesfeedback.Fragments.Notifications;
import shamgar.org.peoplesfeedback.Fragments.Politicians;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.MyFrameLayout;
import shamgar.org.peoplesfeedback.Utils.OnSwipeTouchListener;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class HomeScreenActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAccessorAdaptor tabsAccessorAdaptor;
    FirebaseAuth mAuth;
    SharedPreferenceConfig sharedPreference;
    private RecyclerView horizontal_recycler_view;
    private ArrayList<String> horizontalList;
    private ArrayList<Integer> imagesList;
    private HorizontalAdapter horizontalAdapter;
    private GestureDetector gestureDetector;
    private Toolbar toolbar;



    final int[] ICONS = new int[]

            {
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

        horizontal_recycler_view= (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        horizontalList=new ArrayList<>();
        horizontalList.add("Home");
        horizontalList.add("Politicians");
        horizontalList.add("Chat");
        horizontalList.add("Notifications");

        imagesList=new ArrayList<>();
        imagesList.add(R.drawable.ic_home_white_24dp);
        imagesList.add(R.drawable.ic_people_black_24dp);
        imagesList.add(R.drawable.ic_chat_black_24dp);
        imagesList.add(R.drawable.ic_notifications_white_24dp);

        horizontalAdapter=new HorizontalAdapter(this,horizontalList,imagesList);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(HomeScreenActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);



        Home home = new Home();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container, home,"Home");
        transaction.commit();

        gestureDetector=new GestureDetector(this);

    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result=false;
        float diffy=moveEvent.getY()-downEvent.getY();
        float diffx=moveEvent.getX()-downEvent.getX();
        if (Math.abs(diffx)>Math.abs(diffy)) {
            //swipe left right

            if (Math.abs(diffx)>100 && Math.abs(velocityX)>100)
            {
                if (diffx>0)
                {
                    onSwipeRight();
                }
                else
                {
                    onSwipeLeft();
                }
                result=true;
            }
        }
        else
        {
            //swipe up down
        }
        return result;
    }

    private void onSwipeRight() {
        Toast.makeText(getApplicationContext(), "Swipe to the right",
                Toast.LENGTH_SHORT).show();

    }

    private void onSwipeLeft()
    {
//        Home home=new Home();
//        Politicians politicians=new Politicians();
//        Fragment homeFragment=getHomeFragment(home);
//        Fragment PoliticiansFragment=getPoliticiansFragment(politicians);
//        Fragment chatFragment=getChatFragment(new Chat());
//        Fragment NotificationFragment=getNotificationsFragment(new Notifications());
       Toast.makeText(getApplicationContext(), "Swipe to the left",
                Toast.LENGTH_SHORT).show();

//         strhome=homeFragment.toString();
       // pol=PoliticiansFragment.getTag().toString();


      //   chat=chatFragment.toString();
       // notifications=NotificationFragment.toString();

//        if (strhome.contains("Home"))
//        {
//                getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.frame_container, new Politicians(),"Politicians")
//                                .commit();
//        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

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
}
