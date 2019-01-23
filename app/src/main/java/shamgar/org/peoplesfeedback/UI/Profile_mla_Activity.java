package shamgar.org.peoplesfeedback.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import shamgar.org.peoplesfeedback.R;

public class Profile_mla_Activity extends AppCompatActivity {
    private String mlaName,mlaConstituency;

    private TextView mlanametxt,txtmlaConstituency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_mla_);

        mlanametxt=findViewById(R.id.mlaDistrictName);
        txtmlaConstituency=findViewById(R.id.mlaName);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mlaName=getIntent().getExtras().getString("mlaName");
        mlaConstituency=getIntent().getExtras().getString("mlaConstituency");

        txtmlaConstituency.setText(mlaConstituency);
        mlanametxt.setText(mlaName);

    }
}
