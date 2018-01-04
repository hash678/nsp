package abbasi.com.nixor.misc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import abbasi.com.nixor.soc.soc_main;
import abbasi.com.nixor.portal.portal_main;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                chat_main tab1 = new chat_main();
                return tab1;
            case 1:
                portal_main tab2 = new portal_main();
                return tab2;
            case 2:
                soc_main tab3 = new soc_main();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
