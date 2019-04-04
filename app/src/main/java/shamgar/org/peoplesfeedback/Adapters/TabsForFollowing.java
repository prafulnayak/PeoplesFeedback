package shamgar.org.peoplesfeedback.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import shamgar.org.peoplesfeedback.Fragments.PeopleFollow;
import shamgar.org.peoplesfeedback.Fragments.PloticiansFollow;


public class TabsForFollowing extends FragmentPagerAdapter {
    public TabsForFollowing(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:

                PloticiansFollow home=new PloticiansFollow();

                return home;

            case 1:
                PeopleFollow politicians=new PeopleFollow();
                return politicians;

            default:
                return null;

        }


    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:

                return "Politicians";

            case 1:

                return "People";

            default:

                return null;
        }
    }

}
