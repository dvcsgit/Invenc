/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.pfg_inventory.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;

import fpg.ftc.si.pfg_inventory.FunctionList;
import fpg.ftc.si.pfg_inventory.Input;
import fpg.ftc.si.pfg_inventory.InputLocation;
import fpg.ftc.si.pfg_inventory.MakeInventoryLocation;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGD;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGI;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;

/**
 * 上傳檔案使用
 * http://blog.csdn.net/u010142437/article/details/14639651
 * Created by MarlinJoe on 2014/5/8.
 */
public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

    private static final String TAG = makeLogTag(HttpMultipartPost.class);
    private Context mContext;
    private String mFilePath;
    private String mRequestUrl;
    private ProgressDialog mDialog;
    private long mTotalSize;
    private String WarehouseID;
    private String WarehouseName;
    private String Function;
    private PreferenceUtils mPreferences;

    public HttpMultipartPost(Context context, String filePath, String requestUrl,String WarehouseID,String WarehouseName,String Function) {
        this.mContext = context;
        this.mFilePath = filePath;
        this.mRequestUrl = requestUrl;
        this.WarehouseID=WarehouseID;
        this.WarehouseName=WarehouseName;
        this.Function=Function;
    }

    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMessage("檔案上傳中...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(mRequestUrl);

        try {
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                    new CustomMultipartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) mTotalSize) * 100));
                        }
                    }
            );
            //
            multipartContent.addPart("value", new FileBody(new File(mFilePath)));
            mTotalSize = multipartContent.getContentLength();

            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());

            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
            {
                serverResponse = "";
            }else
            {
                //伺服器回傳失敗內容
                serverResponse = EntityUtils.toString(response.getEntity());
                LOGD(TAG,"伺服器發生錯誤:" + serverResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGD(TAG,"發生錯誤:"+e.getMessage());
            serverResponse = e.getMessage();
        }
        return serverResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mDialog.setProgress((int) (progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        //LOGD(TAG,"上傳結果:"+result);
        if(TextUtils.isEmpty(result))
        {
            Toast.makeText(mContext, "上傳成功", Toast.LENGTH_SHORT).show();
            mPreferences = PreferenceUtils.getInstance(mContext);

            File file;
            boolean reasult=false;
            file=new File(mPreferences.getFilePath()+"/"+ Constants.DBASE_NAME);
            reasult=file.delete();
            if(reasult)
                Toast.makeText(mContext, "檔案刪除成功", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext.getApplicationContext(), FunctionList.class);
            mContext.startActivity(intent);
            if(!Function.equals("M")) {
                ((InputLocation) mContext).finish();
            }else{
                ((MakeInventoryLocation) mContext).finish();
            }
        }else
        {
            Toast.makeText(mContext, "上傳失敗",Toast.LENGTH_LONG).show();
        }
        mDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        LOGD(TAG,"上傳已被取消");
    }
}
