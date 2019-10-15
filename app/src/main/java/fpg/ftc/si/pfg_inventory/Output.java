package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.adapter.ExportInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_ChangeLocation;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Output;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class Output extends Activity {

    private DBHelper_Input dbHelper_input;
    private DBHelper_Output dbHelper_output;
    private DBHelper_ChangeLocation dbHelper_changeLocation;
    public String WarehouseID;
    public String WarehouseName;
    private ListView mList;
    private EditText edBarcodeScan;
    private EditText edCarNo;
    private PreferenceUtils mPreferences;
    private ExportInventoryListAdapter mAdapter;
    private InventoryModel mDataSource;
    private List<InventoryModel> mListDataSource;
    private Button btnSave;
    private Button btnEnterCode;
    private TextView tvTotalOut;
    private int intTotalOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        dbHelper_input=new DBHelper_Input(this);
        dbHelper_output=new DBHelper_Output(this);
        dbHelper_changeLocation=new DBHelper_ChangeLocation(this);

        mPreferences = PreferenceUtils.getInstance(this);

        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");

        setTitle(WarehouseName+"("+WarehouseID+")-出庫管理");

       // mAdapter=new ExportInventoryListAdapter(this,new ArrayList<InventoryModel>());
        mListDataSource=dbHelper_input.getInventory(WarehouseID,"","O");
        intTotalOut=mListDataSource.size();

        mAdapter=new ExportInventoryListAdapter(this,mListDataSource);
        mList=(ListView)findViewById(R.id.Inventory_list);
        mList.setAdapter(mAdapter);
        edBarcodeScan=(EditText) findViewById(R.id.barcodescan);
        edCarNo=(EditText)findViewById(R.id.carno);
        btnSave=(Button)findViewById(R.id.btnSave);
        btnEnterCode=(Button)findViewById(R.id.btnEnterCode);
        tvTotalOut=(TextView)findViewById(R.id.TotalOut);
        tvTotalOut.setText(Integer.toString(intTotalOut));

        //hidekeyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edBarcodeScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(event.getKeyCode()== KeyEvent.KEYCODE_ENTER && !edBarcodeScan.getText().toString().equals(""))
                {
                    final AlertDialog.Builder BarcodeScanFaildialog = new AlertDialog.Builder(Output.this);
                    BarcodeScanFaildialog.setTitle(Constants.ScanFail);
                    BarcodeScanFaildialog.setIcon(android.R.drawable.ic_dialog_alert);
                    BarcodeScanFaildialog.setCancelable(false);
                    BarcodeScanFaildialog.setNegativeButton(Constants.Sure, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    });

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
                        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(600);
                        createNotification(getApplicationContext());
                        BarcodeScanFaildialog.setMessage(strBarCode+" BarCode格式錯誤，請重新掃描！");
                        BarcodeScanFaildialog.show();
                    }
                }
                return true;
            }
        });

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final InventoryModel inventoryModel=mAdapter.getItem(position);

                if(inventoryModel.getStatusCode().equals("Y"))//Status_code="Y"，表示來源為資料庫，不允許變更
                {
                    return true;
                }

                final AlertDialog.Builder ReturnBarcodedialog = new AlertDialog.Builder(Output.this);
                ReturnBarcodedialog.setTitle(Constants.Messsage);
                ReturnBarcodedialog.setMessage(inventoryModel.getBoxNumber()+Constants.SuretoReturn);
                ReturnBarcodedialog.setIcon(android.R.drawable.ic_dialog_alert);
                ReturnBarcodedialog.setCancelable(false);

                ReturnBarcodedialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        inventoryModel.setStatusCode("Y");//不出庫了，改回Y
                        inventoryModel.setModifierAccount(mPreferences.getAccount());
                        inventoryModel.setDateModified(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        mAdapter.Del(inventoryModel);
                        dbHelper_output.UpdateDB(inventoryModel.getBoxNumber());
                        intTotalOut--;
                        tvTotalOut.setText(Integer.toString(intTotalOut));
                    }
                });
                ReturnBarcodedialog.setNegativeButton(Constants.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                ReturnBarcodedialog.show();
                return true;
            }
        });


        btnEnterCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){

                final AlertDialog.Builder codebuilder = new AlertDialog.Builder(Output.this);
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
                            Toast.makeText(Output.this, "箱號不足7碼，請重新輸入！", Toast.LENGTH_LONG).show();
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(600);
                            createNotification(getApplicationContext());
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getCount()==0)
                {
                    Toast.makeText(Output.this, "尚未掃描箱號！", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog.Builder SaveDialog = new AlertDialog.Builder(Output.this);
                SaveDialog.setTitle(Constants.Messsage);
                SaveDialog.setIcon(android.R.drawable.ic_dialog_alert);
                SaveDialog.setCancelable(false);
                SaveDialog.setMessage("共出庫："+mAdapter.getCount()+"箱");
                SaveDialog.setNegativeButton(Constants.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                SaveDialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        List<InventoryModel> reasult=new ArrayList<InventoryModel>();
                        for(int i=0;i<mAdapter.getCount();i++)
                        {
                            InventoryModel item=new InventoryModel(
                                    mAdapter.getItem(i).getWarehouseID()
                                    ,mAdapter.getItem(i).getLocation()
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
                                    ,edCarNo.getText().toString().trim()
                                    ,""
                            );
                            reasult.add(item);
                        }
                        boolean updatereasult=dbHelper_changeLocation.updateChangeLocation(reasult);

                        if(updatereasult)
                        {
                            final AlertDialog.Builder SaveSuccessDialog = new AlertDialog.Builder(Output.this);
                            SaveSuccessDialog.setTitle(Constants.Messsage);
                            SaveSuccessDialog.setIcon(android.R.drawable.ic_dialog_alert);
                            SaveSuccessDialog.setCancelable(false);
                            SaveSuccessDialog.setMessage("存檔成功，共出庫："+mAdapter.getCount()+"箱");
                            SaveSuccessDialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            SaveSuccessDialog.show();
                        }
                        else
                        {
                            Toast.makeText(Output.this,Constants.SaveFail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                SaveDialog.show();
            }
        });
    }

    private void SaveScan(String strBoxNumber)
    {
        String strModifyAccount=mPreferences.getAccount();
        String strDateModify=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String strIndex=Integer.toString(mAdapter.getCount()+1);
        mDataSource=null;
        mDataSource=dbHelper_changeLocation.getInventory(strBoxNumber,strModifyAccount,strDateModify,strIndex,"O");

        if(mDataSource==null)
        {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(600);
            createNotification(getApplicationContext());
            Toast.makeText(Output.this, "箱號：" + strBoxNumber + "不在倉庫中！", Toast.LENGTH_LONG).show();
            return;
        }

        for(int i=0;i<mAdapter.getCount();i++)//判斷是否重複輸入
        {
            if(strBoxNumber.equals(mAdapter.getItem(i).getBoxNumber()))
            {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(600);
                createNotification(getApplicationContext());
                Toast.makeText(Output.this, "箱號："+strBoxNumber+"重複輸入！", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(!mDataSource.getRemark().trim().equals(""))
        {
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(600);
            createNotification(getApplicationContext());
        }
        mAdapter.add(mDataSource);
        intTotalOut++;
        tvTotalOut.setText(Integer.toString(intTotalOut));
    }

    //異常時，提出警告音
    private void createNotification(Context context){
        NotificationManager notificationManager
                = (NotificationManager)context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        long[] vibratepattern = {100, 400, 500, 400};

        builder

                .setVibrate(vibratepattern)
                .setAutoCancel(false);

        Notification notification = builder.getNotification();
        notification.defaults|= Notification.DEFAULT_SOUND;
        notificationManager.notify(R.drawable.ic_launcher, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.output, menu);
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
