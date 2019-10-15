package fpg.ftc.si.pfg_inventory;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.fragment.LocationFragment;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_ChangeLocation;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;

import fpg.ftc.si.pfg_inventory.provider.DBHelper_Output;
import fpg.ftc.si.pfg_inventory.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputLocationDetail extends FragmentActivity {

    //分頁元件
    private ViewPager mPager;
    public ScreenSlidePagerAdapter mPagerAdapter;
    private DBHelper_Input dbHelper_input;
    private DBHelper_ChangeLocation dbHelper_changeLocation;
    public String WarehouseID;
    public String WarehouseName;
    public String LocationID;
    public Button btnSave;
    private Spinner spLocation;
    private TextView tvtitle;
    public boolean blnHasSave=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_location_detail);

        dbHelper_input=new DBHelper_Input(this);

        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");
        LocationID=getIntent().getStringExtra("LocationID");

        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText(WarehouseName+"("+WarehouseID+")-入庫");

        setTitle(WarehouseName+"("+WarehouseID+")-入庫管理");

        ActionBar actionBar = getActionBar();
        actionBar.hide();
        spLocation = (Spinner)findViewById(R.id.spLocation);
        dbHelper_changeLocation=new DBHelper_ChangeLocation(this);
        List<String> locationID_list=dbHelper_changeLocation.getLocationID(WarehouseID);
        final ArrayAdapter locationID_adapter=new ArrayAdapter<String>(InputLocationDetail.this,R.layout.spinner_layout,locationID_list);
        locationID_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spLocation.setAdapter(locationID_adapter);

        spLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spLocation.getSelectedItemPosition()!=0)
                    mPager.setCurrentItem(spLocation.getSelectedItemPosition()-1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder SaveDialog = new AlertDialog.Builder(InputLocationDetail.this);
                SaveDialog.setTitle(Constants.Messsage);
                SaveDialog.setIcon(android.R.drawable.ic_dialog_alert);
                SaveDialog.setCancelable(false);
                SaveDialog.setNegativeButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });

                List<InventoryModel> resault = new ArrayList<InventoryModel>();

                for (int i = 0; i < mPagerAdapter.getCount(); i++)
                {
                    for(InventoryModel item:mPagerAdapter.mDataSource.get(i).getInventoryModel()) {
                        if (item.getStatusCode().equals("NewI"))//若為DB來源(Y)，則不Insert;I表示新入庫，但已存過檔，NewI表示未存過檔
                        {
                               InventoryModel inventoryModel=new InventoryModel(
                                        item.getWarehouseID()
                                       ,item.getLocation()
                                       ,item.getBoxNumber()
                                       ,item.getProductCode()
                                       ,item.getClassLevel()
                                       ,item.getGrossWeight()
                                       ,item.getNetWeight()
                                       ,item.getStatusCode()
                                       ,item.getCreatorAccount()
                                       ,item.getDateCreated()
                                       ,item.getModifierAccount()
                                       ,item.getDateModified()
                                       ,""
                                       ,""//CarNo
                                       ,""//Remark
                               );
                               resault.add(inventoryModel);
                        }
                    }
                }
                boolean insertresault=dbHelper_input.InsertDB(resault);
                if(insertresault)
                {
                    blnHasSave=true;
                    SaveDialog.setMessage(Constants.SaveSuccess+"共入庫："+resault.size()+"箱");
                }
                else
                {
                    SaveDialog.setMessage(Constants.SaveFail);
                }
                SaveDialog.show();
            }
        });
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public List<LocationModel> mDataSource;
        public ScreenSlidePagerAdapter(FragmentManager fm,ArrayList<LocationModel> dataSource) {
            super(fm);
            this.mDataSource = dataSource;
        }

        private Map<Integer, LocationFragment> mPageReferenceMap = new HashMap<Integer, LocationFragment>();
        @Override
        public Fragment getItem(int position) {
            LocationModel locationModel = mDataSource.get(position);
            LocationFragment locationFragment =LocationFragment.create(position, getCount(), locationModel);
            mPageReferenceMap.put(position,locationFragment );
            return locationFragment;

        }
        @Override
        public int getCount() {
            return mDataSource.size();
        }

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (!blnHasSave) {
                final AlertDialog.Builder SaveDialog = new AlertDialog.Builder(InputLocationDetail.this);
                SaveDialog.setTitle(Constants.Messsage);
                SaveDialog.setIcon(android.R.drawable.ic_dialog_alert);
                SaveDialog.setMessage("尚有入庫資料未存檔，確定離開？");
                SaveDialog.setCancelable(false);
                SaveDialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                SaveDialog.setNegativeButton(Constants.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                SaveDialog.show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.input_location_detail, menu);
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
