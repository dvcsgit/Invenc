package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.LoginDataModel;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class Login extends Activity {

    private PreferenceUtils mPreferences;
    private Button mBtnLogin;
    private EditText edAccount;
    private EditText edPassword;
    private static String mRouteUrl = "";
    private RequestQueue mQueue;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPreferences = PreferenceUtils.getInstance(this);
        edAccount=(EditText)findViewById(R.id.account);
        edPassword=(EditText)findViewById(R.id.password);
        mBtnLogin=(Button)findViewById(R.id.btn_login);
        mQueue = Volley.newRequestQueue(this);

        final AlertDialog.Builder Logindialog = new AlertDialog.Builder(this);

        mBtnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                final String strAccount=edAccount.getText().toString().trim();
                final String strPassword=edPassword.getText().toString().trim();

                if(strAccount.equals("") || strPassword.equals(""))
                {
                    Logindialog.setTitle(Constants.LoginMessage);
                    Logindialog.setMessage(Constants.LoginMessageEnterAccount);
                    Logindialog.setIcon(android.R.drawable.ic_dialog_alert);
                    Logindialog.setCancelable(false);
                    Logindialog.setNegativeButton(Constants.Sure, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    });
                    Logindialog.show();
                }else
                {
                    mRouteUrl = Constants.HTTP + mPreferences.getServerIP() + Constants.URL +
                            Constants.Login + Constants.account + "=" + strAccount + "&" + Constants.password + "=" + strPassword;
                    fetchingData();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_ServerIPSetting:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View custom_view = getLayoutInflater().inflate(R.layout.dialog_serversetting, null);

                builder.setTitle("Server IP設定");

                final AlertDialog dialog = builder.setView(custom_view).create();

                final Button mbtnCancel=(Button)custom_view.findViewById(R.id.btn_ServerIPCancel);
                Button mbtnSave=(Button)custom_view.findViewById(R.id.btn_ServerIPSave);
                final EditText etServerIP=(EditText)custom_view.findViewById(R.id.edServerIP);

                etServerIP.setText(mPreferences.getServerIP());

                mbtnSave.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        mPreferences.setServerIP(etServerIP.getText().toString().trim());
                        Toast.makeText(v.getContext(), "IP：" + etServerIP.getText().toString().trim() + " 設定成功！", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    };
                });

                mbtnCancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    };
                });
                dialog.show();
                break;
        }
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchingData()
    {
        final AlertDialog.Builder Logindialog = new AlertDialog.Builder(Login.this);
        Logindialog.setTitle(Constants.LoginMessage);
        Logindialog.setMessage("資料驗證中，請稍候...");
        Logindialog.setIcon(android.R.drawable.ic_dialog_alert);

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(mRouteUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        System.out.println("----------:" + obj);
                        Gson gson = new Gson();
                        LoginDataModel loginDataModel = gson.fromJson(obj.toString(), LoginDataModel.class);

                        dialog.dismiss();

                        if(loginDataModel.getIsLoginValid())
                        {
                            mPreferences.setAccount(loginDataModel.getAccount());
                            mPreferences.setAccountName(loginDataModel.getName());
                            Toast.makeText(Login.this, "登入驗證成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), FunctionList.class);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }else
                        {
                            final AlertDialog.Builder LoginFaildialog = new AlertDialog.Builder(Login.this);
                            LoginFaildialog.setTitle(Constants.LoginMessage);
                            LoginFaildialog.setMessage(loginDataModel.getErrorMessage());
                            LoginFaildialog.setIcon(android.R.drawable.ic_dialog_alert);
                            LoginFaildialog.setCancelable(false);
                            LoginFaildialog.setNegativeButton(Constants.Sure, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            });
                            LoginFaildialog.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "與Server端連線有問題", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        error.printStackTrace();
                    }
                }
        );
        dialog=Logindialog.show();
        mQueue.add(jsObjectRequest);
    }

}
