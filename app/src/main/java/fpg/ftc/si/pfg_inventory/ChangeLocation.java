package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.ExportLoactionModel;
import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.adapter.ExportInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_ChangeLocation;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class ChangeLocation extends Activity {

    private Spinner spLoaction;
    public String WarehouseID;
    private DBHelper_Input dbHelper_input;
    private DBHelper_ChangeLocation dbHelper_changeLocation;
    private EditText edBarcodeScan;
    private PreferenceUtils mPreferences;
    private ExportInventoryListAdapter mAdapter;
    private ListView mList;
    private InventoryModel mDataSource;
    private Button btnSave;
    private Button btnEnterCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);

        dbHelper_input=new DBHelper_Input(this);
        dbHelper_changeLocation=new DBHelper_ChangeLocation(this);
        spLoaction=(Spinner)findViewById(R.id.spLoaction);
        edBarcodeScan=(EditText) findViewById(R.id.barcodescan);
        edBarcodeScan.setInputType(0);
        mList=(ListView)findViewById(R.id.Inventory_list);
        btnSave=(Button)findViewById(R.id.btnSave);
        WarehouseID=getIntent().getStringExtra("WarehouseID");
        btnEnterCode=(Button)findViewById(R.id.btnEnterCode);


        mDataSource=null;

        mPreferences = PreferenceUtils.getInstance(this);
        mAdapter=new ExportInventoryListAdapter(this,new ArrayList<InventoryModel>());
        mList.setAdapter(mAdapter);

        List<String> LocationID=dbHelper_changeLocation.getLocationID(WarehouseID);
        final ArrayAdapter locationID_adapter=new ArrayAdapter<String>(ChangeLocation.this,R.layout.spinner_layout,LocationID);
        locationID_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spLoaction.setAdapter(locationID_adapter);

        edBarcodeScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

                if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER && !edBarcodeScan.getText().toString().equals(""))
                {
                    String strBarCode=edBarcodeScan.getText().toString().trim();
                    edBarcodeScan.setText("");
                    if(strBarCode.length()==23  && strBarCode.substring(7, 14).trim().length()==7)
                    {
                        String strBoxNumber = strBarCode.substring(7, 14);
                        SaveScan(strBoxNumber);

                    }else if(strBarCode.length()==27)
                    {
                        String strBoxNumber = strBarCode.substring(11, 18);
                        SaveScan(strBoxNumber);
                    }
                    else
                    {
                        final AlertDialog.Builder BarcodeScanFaildialog = new AlertDialog.Builder(ChangeLocation.this);
                        BarcodeScanFaildialog.setTitle(Constants.ScanFail);
                        BarcodeScanFaildialog.setMessage(strBarCode+" BarCode格式錯誤，請重新掃描！");
                        BarcodeScanFaildialog.setIcon(android.R.drawable.ic_dialog_alert);
                        BarcodeScanFaildialog.setCancelable(false);
                        BarcodeScanFaildialog.setNegativeButton(Constants.Sure, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        });
                        BarcodeScanFaildialog.show();
                    }
                }
                return true;
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAdapter.getCount()==0)
                {
                    Toast.makeText(ChangeLocation.this, "尚未掃描箱號！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(spLoaction.getSelectedItem().equals(""))
                {
                    Toast.makeText(ChangeLocation.this, "尚未選擇目的排！", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<InventoryModel> reasult=new ArrayList<InventoryModel>();
                for(int i=0;i<mAdapter.getCount();i++)
                {
                    InventoryModel item=new InventoryModel(
                             mAdapter.getItem(i).getWarehouseID()
                            ,spLoaction.getSelectedItem().toString()
                            ,mAdapter.getItem(i).getBoxNumber()
                            ,mAdapter.getItem(i).getProductCode()
                            ,mAdapter.getItem(i).getClassLevel()
                            ,mAdapter.getItem(i).getGrossWeight()
                            ,mAdapter.getItem(i).getNetWeight()
                            ,mAdapter.getItem(i).getStatusCode()
                            ,mAdapter.getItem(i).getCreatorAccount()
                            ,mAdapter.getItem(i).getDateCreated()
                            ,mAdapter.getItem(i).getModifierAccount()
                            ,mAdapter.getItem(i).getDateModified()
                            ,mAdapter.getItem(i).getIndex()
                            ,""
                            ,""

                    );
                    reasult.add(item);
                }
                boolean updatereasult=dbHelper_changeLocation.updateChangeLocation(reasult);
                if(updatereasult)
                {
                    Toast.makeText(ChangeLocation.this,Constants.SaveSuccess, Toast.LENGTH_SHORT).show();
                    mAdapter.setItemList(reasult);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(ChangeLocation.this,Constants.SaveFail, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEnterCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){

                final AlertDialog.Builder codebuilder = new AlertDialog.Builder(ChangeLocation.this);
                View custom_view = getLayoutInflater().inflate(R.layout.dialog_entercode, null);
                codebuilder.setTitle("Barcode自行輸入");
                final AlertDialog codedialog = codebuilder.setView(custom_view).create();
                final Button btnCacel=(Button)custom_view.findViewById(R.id.btnCancel);
                final Button btnSave=(Button)custom_view.findViewById(R.id.btnSave);
                final EditText edBoxNumber=(EditText)custom_view.findViewById(R.id.edBoxNumber);
                final LinearLayout linEnterBarcode=(LinearLayout)custom_view.findViewById(R.id.linEnterBarcode);
                linEnterBarcode.setVisibility(View.GONE);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strBoxNumber = edBoxNumber.getText().toString();
                        if(strBoxNumber.length()!=7)
                        {
                            Toast.makeText(ChangeLocation.this, "箱號不足7碼，請重新輸入！", Toast.LENGTH_LONG).show();
                            return;
                        }
                        SaveScan(strBoxNumber);
                        codedialog.dismiss();
                    }
                });

                btnCacel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        codedialog.dismiss();
                    }
                });
                codedialog.show();
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final InventoryModel inventoryModel=mAdapter.getItem(position);
                final AlertDialog.Builder DelBarcodedialog = new AlertDialog.Builder(ChangeLocation.this);
                DelBarcodedialog.setTitle(Constants.ScanDel);
                DelBarcodedialog.setMessage(inventoryModel.getBoxNumber()+Constants.SuretoDel);
                DelBarcodedialog.setIcon(android.R.drawable.ic_dialog_alert);
                DelBarcodedialog.setCancelable(false);

                DelBarcodedialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            mAdapter.Del(inventoryModel);
                    }
                });
                DelBarcodedialog.setNegativeButton(Constants.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                DelBarcodedialog.show();

                return true;
            }
        });

    }
    private void SaveScan(String strBoxNumber)
    {
        String strModifyAccount=mPreferences.getAccount();
        String strDateModify=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String strIndex=Integer.toString(mAdapter.getCount()+1);

        mDataSource=null;
        mDataSource=dbHelper_changeLocation.getInventory(strBoxNumber,strModifyAccount,strDateModify,strIndex,"C");

        if(mDataSource==null)//資料庫沒有，所以無法換排
        {
            Toast.makeText(ChangeLocation.this, "箱號："+strBoxNumber+"不在帳上！", Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i=0;i<mAdapter.getCount();i++)//判斷是否重複輸入
        {
            if(strBoxNumber.equals(mAdapter.getItem(i).getBoxNumber()))
            {
                Toast.makeText(ChangeLocation.this, "箱號："+strBoxNumber+"重複輸入！", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mAdapter.add(mDataSource);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.change_location, menu);
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
