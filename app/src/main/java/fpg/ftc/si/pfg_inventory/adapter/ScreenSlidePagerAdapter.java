package fpg.ftc.si.pfg_inventory.adapter;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.fragment.LocationFragment;

/**
 * Created by AndroidDev on 2014/10/15.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Used to cache the data
     */
    private List<LocationModel> mDataSource;

    /**
     * 保存目前的 Fragment
     * 為了要讓Activity可以操作
     */
    private Map<Integer,LocationFragment> mPageReferenceMap = new HashMap<Integer, LocationFragment>();

    public ScreenSlidePagerAdapter(FragmentManager fm,ArrayList<LocationModel> dataSource) {
        super(fm);
        this.mDataSource = dataSource;
    }

    @Override
    public Fragment getItem(int position) {
        LocationModel LocationModel = mDataSource.get(position);
        LocationFragment locationFragment = LocationFragment.create(position, getCount(), LocationModel);
        mPageReferenceMap.put(position,locationFragment );
        return locationFragment;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    /**
     * 取出目前的 Fragment
     * @param key
     * @return
     */
    public LocationFragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container,position,object);
        mPageReferenceMap.remove(position);
    }
}
