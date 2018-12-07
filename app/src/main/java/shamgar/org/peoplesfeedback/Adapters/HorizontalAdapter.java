package shamgar.org.peoplesfeedback.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shamgar.org.peoplesfeedback.Fragments.Chat;
import shamgar.org.peoplesfeedback.Fragments.Home;
import shamgar.org.peoplesfeedback.Fragments.Notifications;
import shamgar.org.peoplesfeedback.Fragments.Politicians;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.UI.HomeScreenActivity;
import shamgar.org.peoplesfeedback.UI.MainActivity;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private List<String> horizontalList;
    private List<Integer> imageList;
    private  Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        private ImageView imageView;
        private LinearLayout rootLayout;



        public MyViewHolder(final View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
            imageView = (ImageView) view.findViewById(R.id.imageview_horizantal_listview);
            rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
            rootLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(context,txtView.getText().toString(),Toast.LENGTH_SHORT).show();

                    if (txtView.getText().toString().equals("Home"))
                    {
                        ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new Home(),"Home")
                                .commit();
                    }
                    if (txtView.getText().toString().equals("Politicians"))
                    {
                        ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new Politicians(),"Politicians")
                                .commit();
                    }
                    if (txtView.getText().toString().equals("Chat"))
                    {
                        ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new Chat(),"Chat")
                                .commit();
                    }
                    if (txtView.getText().toString().equals("Notifications"))
                    {
                        ((FragmentActivity)view.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new Notifications(),"Notifications")
                                .commit();
                    }
                }
            });

        }
    }


    public HorizontalAdapter(Context context, List<String> horizontalList, ArrayList<Integer> imagesList) {
        this.horizontalList = horizontalList;
        this.context=context;
        this.imageList=imagesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizantal_recyclerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtView.setText(horizontalList.get(position));
        holder.imageView.setImageResource(imageList.get(position));


    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }





}
