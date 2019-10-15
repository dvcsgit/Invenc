package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.ExportLoactionModel;
import fpg.ftc.si.pfg_inventory.adapter.ExportLoactionListAdapter;
import fpg.ftc.si.pfg_inventory.async.HttpMultipartPost;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Output;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.FileZipUtils;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class InputLocation extends Activity {

    private ListView mList;
    private ExportLoactionListAdapter mAdapter;
    public String WarehouseID;
    private String WarehouseName;
    private String Function;
    private List<ExportLoactionModel> mDataSource;
    private DBHelper_Input dbHelper_input;
    private DBHelper_Output dbHelperOutput;
    private Spinner spProductCode;
    private Spinner spClassLevel;
    private String strSelectProductCode;
    private LinearLayout linSelector;
    private Button btn_upload;
    private PreferenceUtils mPreferences;
    private LinearLayout linTotal;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_location);

        spProductCode=(Spinner)findViewById(R.id.spProductCode);
        spClassLevel=(Spinner)findViewById(R.id.spClassLevel);
        linSelector=(LinearLayout)findViewById(R.id.linSelector);
        linTotal=(LinearLayout)findViewById(R.id.linTotal);
        tvTotal=(TextView)findViewById(R.id.Total);
        btn_upload=(Button)findViewById(R.id.btn_upload);

        mList=(ListView)findViewById(R.id.loaction_list);
        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");
        Function=getIntent().getStringExtra("Function");
        dbHelper_input=new DBHelper_Input(this);
        dbHelperOutput=new DBHelper_Output(this);
        mPreferences=PreferenceUtils.getInstance(this);
        mAdapter = new ExportLoactionListAdapter(this,new ArrayList<ExportLoactionModel>());

        mList=(ListView)findViewById(R.id.loaction_list);
        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");
        final String Function=getIntent().getStringExtra("Function");
        dbHelper_input=new DBHelper_Input(this);
        dbHelperOutput=new DBHelper_Output(this);
        mPreferences=PreferenceUtils.getInstance(this);


        if(Function.equals("I")){
            setTitle(WarehouseName+"("+WarehouseID+")-入庫管理");
            linSelector.setVisibility(View.GONE);
            linTotal.setVisibility(View.GONE);
        }
        else if(Function.equals("O")){
            setTitle(WarehouseName+"("+WarehouseID+")-出庫管理");
            linSelector.setVisibility(View.VISIBLE);
            linTotal.setVisibility(View.VISIBLE);
        }else{
            setTitle(WarehouseName+"("+WarehouseID+")-盤點管理");
            linSelector.setVisibility(View.GONE);
            linTotal.setVisibility(View.GONE);
        }

        //下拉式選單--批號
        List<String> productCode=dbHelperOutput.getProductCode(WarehouseID);
        final ArrayAdapter productCode_adapter=new ArrayAdapter<String>(InputLocation.this,R.layout.spinner_layout,productCode);
        productCode_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spProductCode.setAdapter(productCode_adapter);

        spProductCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strSelectProductCode=spProductCode.getSelectedItem().toString();

                List<String> classLevel=dbHelperOutput.getClassLevel(WarehouseID,strSelectProductCode);
                final ArrayAdapter classlevel_adapter=new ArrayAdapter<String>(InputLocation.this,R.layout.spinner_layout,classLevel);
                classlevel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spClassLevel.setAdapter(classlevel_adapter);

                //先取消，改為等級也要選，才秀出資料
//                ArrayList<ExportLoactionModel> exportLoactionModelArrayList=dbHelper_input.getLoactionFromSpinner(WarehouseID,strSelectProductCode,"");

//                if(exportLoactionModelArrayList.size()!=0) {
//                    mAdapter.setItemList(exportLoactionModelArrayList);
//                    mList.setAdapter(mAdapter);
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spClassLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String strSelectClassLevel=spClassLevel.getSelectedItem().toString();
                int intTotal=0;
                if(!strSelectProductCode.equals("") && !strSelectClassLevel.equals(""))
                {
                    ArrayList<ExportLoactionModel> exportLoactionModelArrayList = dbHelper_input.getLoactionFromSpinner(WarehouseID, strSelectProductCode, strSelectClassLevel);

                    if (exportLoactionModelArrayList.size() != 0) {
                        //計算總箱數
                        for(ExportLoactionModel item:exportLoactionModelArrayList){
                            intTotal=intTotal+Integer.valueOf(item.getTotal());
                        }
                        mAdapter.setItemList(exportLoactionModelArrayList);
                        mList.setAdapter(mAdapter);
                    }
                }else{
                    mList.setAdapter(null);
                }
                tvTotal.setText(Integer.toString(intTotal));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(Function.equals("I") || Function.equals("M"))//入庫才需要一進來就帶庫位
        {

            mDataSource = dbHelper_input.getLocationID(WarehouseID);
            mAdapter.setItemList(mDataSource);
            //mAdapter = new ExportLoactionListAdapter(this, mDataSource);
            mList.setAdapter(mAdapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    if (Function.equals("I")) {
                        intent.setClass(getApplicationContext(), InputLocationDetail.class);
                    } else {
                        //intent.setClass(getApplicationContext(), OutputLocationDetail.class);
                        intent.setClass(getApplicationContext(),MakeInventoryD.class);
                    }
                    ExportLoactionModel LoactionList = mAdapter.getItem(position);
                    intent.putExtra("WarehouseID", WarehouseID);
                    intent.putExtra("WarehouseName", WarehouseName);
                    intent.putExtra("LocationID", LoactionList.getLoactionID());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder Uploaddialog = new AlertDialog.Builder(InputLocation.this);
                Uploaddialog.setTitle(Constants.Messsage);
                Uploaddialog.setIcon(android.R.drawable.ic_dialog_alert);
                Uploaddialog.setMessage("確定要上傳？");
                Uploaddialog.setCancelable(false);
                Uploaddialog.setNegativeButton(Constants.Cancel,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                Uploaddialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String sourceFilePath=mPreferences.getFilePath();
                        String destFilePath=sourceFilePath+"/Temp";

                        File dirFile = new File(destFilePath);
                        if(!dirFile.exists()){//如果資料夾不存在
                            dirFile.mkdir();//建立資料夾
                        }
                        copyfile(sourceFilePath+"/"+Constants.DBASE_NAME,destFilePath+"/"+Constants.DBASE_NAME);
                        FileZipUtils.zip(destFilePath, sourceFilePath + "/temp.zip");

                        String uploadUrl=Constants.HTTP+mPreferences.getServerIP()+Constants.URL+Constants.Upload+mPreferences.getAccount();
                        HttpMultipartPost post = new HttpMultipartPost(InputLocation.this, sourceFilePath+"/temp.zip", uploadUrl,WarehouseID,WarehouseName,Function);
                        post.execute();
                    }
                });
                Uploaddialog.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.input_location, menu);

        final MenuItem menuItemOutput=menu.findItem(R.id.action_Output);
        final MenuItem menuItemChangeLocation=menu.findItem(R.id.action_ChangeLocation);
        if(Function.equals("I"))
            menuItemOutput.setVisible(false);
        if(Function.equals("M"))
        {
            menuItemOutput.setVisible(false);
            menuItemChangeLocation.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent;
        switch(item.getItemId()) {
            case R.id.action_ChangeLocation:
                intent=new Intent(getApplication(),ChangeLocation.class);
                intent.putExtra("WarehouseID", WarehouseID);
                startActivity(intent);
            break;


            case R.id.action_Output:
                intent=new Intent(getApplication(),Output.class);
                intent.putExtra("WarehouseID", WarehouseID);
                intent.putExtra("WarehouseName",WarehouseName);
                startActivity(intent);
            break;

        }
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            }
            return super.onOptionsItemSelected(item);
    }

    private void copyfile(String srFile, String dtFile)
    {
        try{
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            //System.out.println("File copied.");
        }
        catch(FileNotFoundException ex){
            //System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        }
        catch(IOException e){
            //System.out.println(e.getMessage());
        }
    }
}
