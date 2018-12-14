package shamgar.org.peoplesfeedback.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import shamgar.org.peoplesfeedback.Adapters.Politicians.ViewAllPoliticiansAdapter;
import shamgar.org.peoplesfeedback.R;

import static android.widget.LinearLayout.VERTICAL;

public class ViewAllPoliticiansActivity extends AppCompatActivity {

    private RecyclerView viewAllRecyclerView;
    private ViewAllPoliticiansAdapter allPoliticiansAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_politicians);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        viewAllRecyclerView=(RecyclerView)findViewById(R.id.viewAllRcyclerView);
        allPoliticiansAdapter=new ViewAllPoliticiansAdapter(this);
        viewAllRecyclerView.setHasFixedSize(true);
        viewAllRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewAllRecyclerView.setAdapter(allPoliticiansAdapter);
        viewAllRecyclerView.addItemDecoration(decoration);

    }
}
