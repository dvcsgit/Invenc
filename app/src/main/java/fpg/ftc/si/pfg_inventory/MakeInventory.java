package fpg.ftc.si.pfg_inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.fragment.MakeInventoryFragment;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;

public class MakeInventory extends FragmentActivity {

    //分頁元件
    private ViewPager mPager;
    public ScreenSlidePagerAdapter mPagerAdapter;
    private DBHelper_Input dbHelper_input;
    public String WarehouseID;
    public String WarehouseName;
    public String LocationID;
    public Button btnSave;
    private TextView tvtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_inventory);

        dbHelper_input=new DBHelper_Input(this);

        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");
        LocationID=getIntent().getStringExtra("LocationID");

        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText(WarehouseName+"("+WarehouseID+")-入庫");

        mPager = (ViewPager) findViewById(R.id.pager);
        btnSave=(Button) findViewById(R.id.btnSave);

        ArrayList<LocationModel> LocationArrayList=dbHelper_input.getLocation(WarehouseID);

        for(LocationModel item:LocationArrayList)
        {
            List<InventoryModel> InventoryModelList=dbHelper_input.getInventory(WarehouseID,item.getLoactionID(),"I");
            item.setInventoryModel(InventoryModelList);
        }

        mPagerAdapter =new ScreenSlidePagerAdapter(getSupportFragmentManager(),LocationArrayList);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(Integer.valueOf(LocationID)-1);
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public List<LocationModel> mDataSource;
        public ScreenSlidePagerAdapter(FragmentManager fm,ArrayList<LocationModel> dataSource) {
            super(fm);
            this.mDataSource = dataSource;
        }

        private Map<Integer, MakeInventoryFragment> mPageReferenceMap = new HashMap<Integer, MakeInventoryFragment>();
        @Override
        public Fragment getItem(int position) {
            LocationModel locationModel = mDataSource.get(position);
            MakeInventoryFragment makeInventoryFragment =MakeInventoryFragment.create(position, getCount(), locationModel);
            mPageReferenceMap.put(position,makeInventoryFragment );
            return makeInventoryFragment;
        }
        @Override
        public int getCount() {
            return mDataSource.size();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.make_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
