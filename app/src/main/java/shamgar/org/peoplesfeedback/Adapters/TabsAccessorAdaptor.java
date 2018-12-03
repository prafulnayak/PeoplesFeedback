package shamgar.org.peoplesfeedback.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import shamgar.org.peoplesfeedback.Fragments.Home;
import shamgar.org.peoplesfeedback.Fragments.Notifications;
import shamgar.org.peoplesfeedback.Fragments.Politicians;

public class TabsAccessorAdaptor extends FragmentPagerAdapter
{
    public TabsAccessorAdaptor(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
                case 0:
                Home home=new Home();

                return home;

                case 1:
                    Politicians politicians=new Politicians();
                    return politicians;

                case 2:
                    Notifications notifications=new Notifications();
                    return notifications;

                default:
                    return null;

        }



    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:

                return "Home";


            case 1:

                return "Politicians";

            case 2:

                return "Notifications";

            default:
                return null;
        }
    }


}
