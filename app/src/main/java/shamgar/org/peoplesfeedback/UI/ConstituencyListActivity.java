package shamgar.org.peoplesfeedback.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import shamgar.org.peoplesfeedback.Adapters.Politicians.VerticalPoliticianAdapter;
import shamgar.org.peoplesfeedback.R;

import static android.widget.LinearLayout.VERTICAL;

public class ConstituencyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VerticalPoliticianAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constituency_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=(RecyclerView)findViewById(R.id.vertical_politicians_sub_list);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),VERTICAL);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        adapter = new VerticalPoliticianAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home)
        {
            Intent backpressed=new Intent(ConstituencyListActivity.this, HomeScreenActivity.class);
            startActivity(backpressed);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
