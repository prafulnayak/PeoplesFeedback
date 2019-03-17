package shamgar.org.peoplesfeedback;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import shamgar.org.peoplesfeedback.Adapters.MySplashScreenAdapter;
import shamgar.org.peoplesfeedback.UI.MainActivity;
import shamgar.org.peoplesfeedback.UI.PhoneActivity;

public class SplashScreen extends AppCompatActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] slideImages= {R.drawable.follow_and_chat,R.drawable.live_pic,R.drawable.politics};
    private ArrayList<Integer> slidImagesArray = new ArrayList<Integer>();
    private Button btnGetStarted;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        for(int i=0;i<slideImages.length;i++)
            slidImagesArray.add(slideImages[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        btnGetStarted = (Button) findViewById(R.id.btnGetStarted);
        mPager.setAdapter(new MySplashScreenAdapter(SplashScreen.this,slidImagesArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainActivity);
                finish();

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
