package shamgar.org.peoplesfeedback.UI;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import shamgar.org.peoplesfeedback.Adapters.TabsForFollowing;
import shamgar.org.peoplesfeedback.Fragments.Home;
import shamgar.org.peoplesfeedback.Fragments.PeopleFollow;
import shamgar.org.peoplesfeedback.Fragments.PloticiansFollow;
import shamgar.org.peoplesfeedback.R;

public class following extends AppCompatActivity {

    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        tabLayout= (TabLayout) findViewById(R.id.followingTabs);
        frameLayout=(FrameLayout)findViewById(R.id.frameLay);

        number=getIntent().getExtras().get("number").toString();
        Log.e("number",number);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Following");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.gradient_background));
        getSupportActionBar().setElevation(0);

        TabsForFollowing adaptor=new TabsForFollowing(getSupportFragmentManager());
        tabLayout.setTabsFromPagerAdapter(adaptor);
        TabLayout.Tab tab=tabLayout.getTabAt(0);
        tab.select();

        Bundle bundle=new Bundle();
        bundle.putString("number",number);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PloticiansFollow llf = new PloticiansFollow();
        llf.setArguments(bundle);
        ft.replace(R.id.frameLay, llf);
        ft.commit();

        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#EF5857"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    Bundle bundle=new Bundle();
                    bundle.putString("number",number);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    PloticiansFollow llf = new PloticiansFollow();
                    llf.setArguments(bundle);
                    ft.replace(R.id.frameLay, llf);
                    ft.commit();
                }
                if (tab.getPosition()==1){
                    Bundle bundle=new Bundle();
                    bundle.putString("number",number);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    PeopleFollow llf = new PeopleFollow();
                    llf.setArguments(bundle);
                    ft.replace(R.id.frameLay, llf);
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
}
