package shamgar.org.peoplesfeedback.UI;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import shamgar.org.peoplesfeedback.Adapters.TabsAccessorAdaptor;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class HomeScreenActivity extends AppCompatActivity
{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAccessorAdaptor tabsAccessorAdaptor;
    FirebaseAuth mAuth;
    SharedPreferenceConfig sharedPreference;
    private Toolbar toolbar;

    final int[] ICONS = new int[]

            {
            R.drawable.ic_notifications_white_24dp,
            R.drawable.ic_people_black_24dp,
            R.drawable.ic_home_white_24dp
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        sharedPreference = new SharedPreferenceConfig(this);

        mAuth = FirebaseAuth.getInstance();

//        toolbar=(Toolbar)findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Peoples feedback");
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.profile);




        SharedPreferences pref = getApplicationContext().getSharedPreferences("signUp", MODE_PRIVATE);

        String gender=pref.getString("Gender",null);
        String state=pref.getString("state",null);
        String district=pref.getString("district",null);
        String phone=pref.getString("phone no",null);
        String constit=pref.getString("constit",null);

//        Toast.makeText(getApplicationContext(),"Gender="+gender+mAuth.getCurrentUser().getEmail()+"\n"+"state="+state+"\n"+"district="+district+"\n"+"number="+phone+"\n"+"constituency="+constit,Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(),"state: "+sharedPreference.readState()+"\n"+
//                " district "+sharedPreference.readDistrict()+"\n"+
//                "constituency: "+sharedPreference.readConstituancy(),Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(),""+sharedPreference.readPhoneNo()+sharedPreference.readName()+sharedPreference.readEmail(),Toast.LENGTH_LONG).show();
        viewPager=(ViewPager)findViewById(R.id.mainviewpager);
        tabsAccessorAdaptor=new TabsAccessorAdaptor(getSupportFragmentManager());




        viewPager.setAdapter(tabsAccessorAdaptor);
        tabLayout=(TabLayout)findViewById(R.id.maintabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);

    }


}
