package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.MakeInventoryCountModel;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.adapter.ExportInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.adapter.ExportMakeInventoryDListAdapter;
import fpg.ftc.si.pfg_inventory.adapter.ExportMakeInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.adapter.ExportMakeInventoryListAdapter.ViewHolder;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_MakeInventory;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class MakeInventoryD extends Activity {

    private String WarehouseID;
    private String WarehouseName;
    private String LocationID;
    private TextView tvLocation;
    private TextView tvTotal;
    private Button btnSave;
    private ListView mList;
    private PreferenceUtils mPreferences;
    private ExportMakeInventoryListAdapter mAdapter;
    private ExportMakeInventoryDListAdapter mAdapterDetail;
    private List<MakeInventoryCountModel> mDataSource;
    private List<InventoryModel> mDataSourceDetail;
    private DBHelper_MakeInventory dbHelper_makeInventory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_inventory_d);

        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");
        LocationID=getIntent().getStringExtra("LocationID");

        setTitle(WarehouseName+"("+WarehouseID+")-盤點管理");

        tvLocation=(TextView)findViewById(R.id.Location);
        tvTotal=(TextView)findViewById(R.id.Total);
        mList=(ListView)findViewById(R.id.loactionProduct_list);
        btnSave=(Button)findViewById(R.id.btn_save);

        tvLocation.setText(LocationID);

        dbHelper_makeInventory=new DBHelper_MakeInventory(this);
        mPreferences = PreferenceUtils.getInstance(this);
        mDataSource=dbHelper_makeInventory.getMakeInventory(WarehouseID,LocationID);

        int Total=0;
        for(MakeInventoryCountModel item:mDataSource)
        {
            Total=Total+Integer.valueOf(item.getCount());
        }
        tvTotal.setText(Integer.toString(Total));

        mAdapter=new ExportMakeInventoryListAdapter(this,mDataSource);
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                MakeInventoryCountModel model=mAdapter.getItem(position);
                holder.chkSelect.toggle();
                mAdapter.setItemSelected(position, holder.chkSelect.isChecked());
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final MakeInventoryCountModel item=mAdapter.getItem(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MakeInventoryD.this);
                View custom_view = getLayoutInflater().inflate(R.layout.dialog_makeinventorydetail,null);
                final AlertDialog dialog = builder.setView(custom_view).create();
                dialog.setTitle(Constants.Messsage);
                dialog.setIcon(android.R.drawable.ic_dialog_info);
                //dialog.setCancelable(false);
                ListView mListDetail;
                mListDetail=(ListView)custom_view.findViewById(R.id.Inventory_list);
                mDataSourceDetail=dbHelper_makeInventory.getMakeInventoryDetail(WarehouseID,LocationID,item.getProductCode(),item.getClassLevel());
                mAdapterDetail=new ExportMakeInventoryDListAdapter(MakeInventoryD.this,mDataSourceDetail);
                //mAdapterDetail=new ExportInventoryListAdapter(MakeInventoryD.this,mDataSourceDetail);
                mListDetail.setAdapter(mAdapterDetail);

                dialog.show();
                return true;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dbHelper_makeInventory.UpdateMakeInventory(mAdapter.getSelectedParam(),LocationID)){
                    Toast.makeText(MakeInventoryD.this,Constants.SaveSuccess,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MakeInventoryLocation.class);
                    intent.putExtra("WarehouseID", WarehouseID);
                    intent.putExtra("WarehouseName",WarehouseName);
                    intent.putExtra("Function","MD");
                    intent.putExtra("LocationID", LocationID);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }else{
                    Toast.makeText(MakeInventoryD.this,Constants.SaveFail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.make_inventory_d, menu);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(getApplicationContext(), MakeInventoryLocation.class);
            intent.putExtra("WarehouseID", WarehouseID);
            intent.putExtra("WarehouseName",WarehouseName);
            intent.putExtra("Function","MD");
            intent.putExtra("LocationID", LocationID);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
