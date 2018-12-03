package shamgar.org.peoplesfeedback.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import shamgar.org.peoplesfeedback.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Politicians extends Fragment {


    public Politicians() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_politicians, container, false);

        //Toast.makeText(getActivity(),"Politicians",Toast.LENGTH_LONG).show();


        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            Log.d("Politicians","visible");
        }else{
            // fragment is not visible
        }
    }

}
