package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.adapter.ExportLoactionListAdapter;
import fpg.ftc.si.pfg_inventory.adapter.ExportMakeInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.adapter.ExportMakeInventoryLoactionListAdapter;
import fpg.ftc.si.pfg_inventory.async.HttpMultipartPost;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_MakeInventory;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Output;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.FileZipUtils;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class MakeInventoryLocation extends Activity {

    private ListView mList;
    private ExportMakeInventoryLoactionListAdapter mAdapter;
    private String WarehouseID;
    private String WarehouseName;
    private String Function;
    private String Position;
    private List<ExportLoactionModel> mDataSource;
    private DBHelper_Input dbHelper_input;
    private DBHelper_Output dbHelperOutput;
    private DBHelper_MakeInventory dbHelper_makeInventory;
    private Button btn_upload;
    private PreferenceUtils mPreferences;
    private TextView tvfinishrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_inventory_location);

        btn_upload=(Button)findViewById(R.id.btn_upload);
        tvfinishrate=(TextView)findViewById(R.id.finishrate);

        mList=(ListView)findViewById(R.id.loaction_list);
        WarehouseID=getIntent().getStringExtra("WarehouseID");
        WarehouseName=getIntent().getStringExtra("WarehouseName");
        Function=getIntent().getStringExtra("Function");

        dbHelper_input=new DBHelper_Input(this);
        dbHelperOutput=new DBHelper_Output(this);
        dbHelper_makeInventory=new DBHelper_MakeInventory(this);
        mPreferences=PreferenceUtils.getInstance(this);

        mDataSource = dbHelper_input.getLocationID(WarehouseID);
        mAdapter=new ExportMakeInventoryLoactionListAdapter(this,mDataSource);
        setTitle(WarehouseName+"("+WarehouseID+")-盤點管理");

        tvfinishrate.setText(dbHelper_makeInventory.GetTotalBoxHasMake(WarehouseID,"")+" / "+dbHelper_makeInventory.GetTotalBox(WarehouseID,""));

        mList.setAdapter(mAdapter);

        if(Function.equals("MD")){
            Function="M";
            Position=getIntent().getStringExtra("LocationID");
            mList.setSelectionFromTop(Integer.valueOf(Position)-1, 5);
        }

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),MakeInventoryD.class);
                ExportLoactionModel LoactionList = mAdapter.getItem(position);
                intent.putExtra("WarehouseID", WarehouseID);
                intent.putExtra("WarehouseName", WarehouseName);
                intent.putExtra("LocationID", LoactionList.getLoactionID());
                intent.putExtra("Position",position);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder Uploaddialog = new AlertDialog.Builder(MakeInventoryLocation.this);
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

                        //String uploadUrl=Constants.HTTP+mPreferences.getServerIP()+Constants.URL+Constants.Upload+mPreferences.getAccount();
                        String uploadUrl=Constants.HTTP+mPreferences.getServerIP()+Constants.URL+Constants.StockUpload+mPreferences.getAccount();
                        HttpMultipartPost post = new HttpMultipartPost(MakeInventoryLocation.this, sourceFilePath+"/temp.zip", uploadUrl,WarehouseID,WarehouseName,Function);
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
        getMenuInflater().inflate(R.menu.make_inventory_location, menu);
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
