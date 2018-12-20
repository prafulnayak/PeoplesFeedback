package shamgar.org.peoplesfeedback.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import shamgar.org.peoplesfeedback.Adapters.Politicians.Tag_Profile_Images_Adapter;
import shamgar.org.peoplesfeedback.R;

public class Profile_TagActivity extends AppCompatActivity {

    private ImageButton tag_gridViewImages;

    private Tag_Profile_Images_Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_profile_updated);
        tag_gridViewImages=(ImageButton)findViewById(R.id.tag_gridViewImages);
        recyclerView=(RecyclerView)findViewById(R.id.profile_tag_gridImages_rv);
        tag_gridViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int mod = position % 4;

                        if(position == 0 || position == 1)
                            return 2;
                        else if(position < 4)
                            return 2;
                        else if(mod == 0 || mod == 1)
                            return 1;
                        else
                            return 2;
                    }
                });
                adapter=new Tag_Profile_Images_Adapter(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setNestedScrollingEnabled(false);
            }
        });
    }
}
