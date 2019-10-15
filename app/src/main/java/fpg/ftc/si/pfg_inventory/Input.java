package fpg.ftc.si.pfg_inventory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.ExportWarehouseModel;
import fpg.ftc.si.pfg_inventory.adapter.ExportWarehouseListAdapter;
import fpg.ftc.si.pfg_inventory.fragment.DialogAlertFragment;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.DialogConstants;
import fpg.ftc.si.pfg_inventory.utils.FileZipUtils;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.*;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;

public class Input extends Activity {

    private static final String TAG = makeLogTag(Input.class);
    private ListView mList;
    private static String mRouteUrl = "";
    private RequestQueue mQueue;
    private PreferenceUtils mPreferences;
    private ProgressDialog mDialog;
    private ExportWarehouseListAdapter mAdapter;
    private View mRequestStatusView;//progress use
    private String WarehouseID;
    private String WarehouseName;
    private String Function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Function=getIntent().getStringExtra("Function");
        if(Function.equals("I")){
            setTitle("外倉選擇-入庫管理");
        }else if(Function.equals("O")){
            setTitle("外倉選擇-出庫管理");
        }else{
            setTitle("外倉選擇-盤點管理");
        }

        mQueue = Volley.newRequestQueue(this);
        mList = (ListView) findViewById(R.id.warehouse_list);
        mRequestStatusView = (View) findViewById(R.id.request_status);
        mPreferences = PreferenceUtils.getInstance(this);
        mAdapter=new ExportWarehouseListAdapter(this,new ArrayList<ExportWarehouseModel>());
        mList.setAdapter(mAdapter);
        mRouteUrl= Constants.HTTP+mPreferences.getServerIP()+Constants.URL+Constants.Warehouse;
        fetchingData();

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExportWarehouseModel ExportWarehouseListAdapter = mAdapter.getItem(position);
                WarehouseID=ExportWarehouseListAdapter.getWarehouseID();
                WarehouseName=ExportWarehouseListAdapter.getWarehouseName();
                final String sqlite_url = Constants.HTTP + mPreferences.getServerIP()+Constants.URL+Constants.WarehouseID+ExportWarehouseListAdapter.getWarehouseID();

                String strDBName=mPreferences.getFilePath()+"/"+Constants.DBASE_NAME;
                File file = new File(strDBName);

                if(!file.exists()){
                    new DownloadFileFromURL().execute(sqlite_url);
                }else {
                    final AlertDialog.Builder DownloadDialog = new AlertDialog.Builder(Input.this);
                    DownloadDialog.setTitle(Constants.Messsage);
                    DownloadDialog.setMessage(Constants.SuretoDownload);
                    DownloadDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    DownloadDialog.setCancelable(false);
                    DownloadDialog.setNegativeButton(Constants.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            if(!Function.equals("M")){
                                intent.setClass(getApplicationContext(), InputLocation.class);
                                //intent.setClass(getApplicationContext(), MakeInventoryLocation.class);
                            }else{
                                intent.setClass(getApplicationContext(), MakeInventoryLocation.class);
                            }
                           // intent = new Intent(getApplication(), InputLocation.class);
                            intent.putExtra("WarehouseID", WarehouseID);
                            intent.putExtra("WarehouseName", WarehouseName);
                            intent.putExtra("Function", Function);
                            finish();
                            startActivity(intent);
                        }
                    });
                    DownloadDialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new DownloadFileFromURL().execute(sqlite_url);
                        }
                    });
                    DownloadDialog.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.input, menu);
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

    private void fetchingData()
    {
        //LOGD(TAG,"RouteUrl:" + mRouteUrl);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(mRouteUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        List<ExportWarehouseModel> result = new ArrayList<ExportWarehouseModel>();
                        int total = response.length();
                        for (int i = 0; i < total; i++) {
                            try {
                                result.add(convertRoute(response
                                        .getJSONObject(i)));
                            } catch (JSONException e) {
                                LOGE(TAG,e.getMessage());
                            }
                        }
                        mAdapter.setItemList(result);
                        mAdapter.notifyDataSetChanged();
                        showProgress(false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(Input.this, "與Server端連線有問題", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        showProgress(true);
        mQueue.add(jsArrayRequest);
    }

    private final ExportWarehouseModel convertRoute(JSONObject obj) throws JSONException {

        String WarehouseID=obj.getString("WarehouseID");
        String WarehouseName=obj.getString("WarehouseName");
        String Capacity=obj.getString("Capacity");
        return new ExportWarehouseModel(WarehouseID,WarehouseName,Capacity);
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            mRequestStatusView.setVisibility(View.VISIBLE);
            mRequestStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRequestStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });
        } else {
            mRequestStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DialogConstants.Progress:
                mDialog = new ProgressDialog(this);
                mDialog.setMessage(getResources().getString(R.string.progress_download));
                mDialog.setIndeterminate(false);
                mDialog.setMax(100);
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDialog.setCancelable(true);
                mDialog.show();
                return mDialog;
            default:
                return null;
        }
    }
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DialogConstants.Progress);
        }
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                String file_path = mPreferences.getFilePath();

                String save_full_path = file_path +"/"+ Constants.DOWNLOAD_PROCESS_FILE_NAME;
                LOGD(TAG,"save_full_path:" + save_full_path);
                // Output stream
                OutputStream output = new FileOutputStream(save_full_path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }
                output.flush();

                // 進行解壓縮
                String arryfileName[]= FileZipUtils.unzip(save_full_path, file_path + "/","");
 //               dbHelper.addBookList(arryfileName);
                output.close();
                input.close();
            } catch (Exception e) {
                String error_message = e.getMessage();
                Log.e("Error: ", error_message);
                return error_message;
            }
            return "";
        }
        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            mDialog.setProgress(Integer.parseInt(progress[0]));
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String error_message) {
            // dismiss the dialog after the file was downloaded
            if(TextUtils.isEmpty(error_message))
            {
                DialogAlertFragment dialogFragment = DialogAlertFragment.newInstance(
                        getString(R.string.system_download_finish));
                dialogFragment.show(getFragmentManager(), "dialog");
                Intent intent = new Intent();
                if(!Function.equals("M")){
                    intent.setClass(getApplicationContext(), InputLocation.class);
                }else{
                    intent.setClass(getApplicationContext(), MakeInventoryLocation.class);
                }
                //intent=new Intent(getApplication(),InputLocation.class);
                intent.putExtra("WarehouseID", WarehouseID);
                intent.putExtra("WarehouseName",WarehouseName);
                intent.putExtra("Function",Function);
                finish();
                startActivity(intent);
            }
            else
            {
                DialogAlertFragment dialogFragment = DialogAlertFragment.newInstance(
                        getString(R.string.error_download_crash));
                dialogFragment.show(getFragmentManager(), "dialog");
            }
            dismissDialog(DialogConstants.Progress);
        }
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
