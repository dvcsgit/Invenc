package fpg.ftc.si.pfg_inventory.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fpg.ftc.si.pfg_inventory.InputLocationDetail;
import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.adapter.ExportInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_Input;

import fpg.ftc.si.pfg_inventory.provider.DBHelper_Output;

import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGD;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGI;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;

public class LocationFragment extends Fragment {
    private static final String TAG = makeLogTag(LocationFragment.class);

    private View mRootView;
    private InputLocationDetail mActivity;
    private LocationModel mLocationModel;
    private ExportInventoryListAdapter mAdapter;
    private ListView mList;
    private PreferenceUtils mPreferences;
    private DBHelper_Input dbHelper_Input;
    private TextView tvTotalIn;
    private TextView tvLocationTotal;

    //頁面控制項
    private TextView mPagerInfo;//用來顯示目前總有幾筆資料,目前在第幾筆

    private TextView tvLocation;
    private EditText edBarcodeScan;
    private Button btnEnterCode;
    private int intTotalIn;
    private int intLocationTotal;

    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL = "total";
    public static final String ARG_LOCATION= "check_item";

    private int mPageNumber;
    private int mTotalCount;

    public static LocationFragment create(int pageNumber,int totalCount,LocationModel locationModel) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt(ARG_TOTAL, totalCount);
        args.putSerializable(ARG_LOCATION, locationModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTotalCount = getArguments().getInt(ARG_TOTAL);
        mLocationModel=(LocationModel)getArguments().getSerializable(ARG_LOCATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LOGD(TAG,"onCreateView ... "+ String.valueOf(mPageNumber+1));
        mActivity = (InputLocationDetail) getActivity();
        mRootView = inflater.inflate(R.layout.fragment_location, container, false);
        mPreferences = PreferenceUtils.getInstance(mActivity);
        mList=(ListView) mRootView.findViewById(R.id.Inventory_list);
        tvLocation=(TextView) mRootView.findViewById(R.id.location);
        tvTotalIn=(TextView)mRootView.findViewById(R.id.TotalIn);
        tvLocationTotal=(TextView)mRootView.findViewById(R.id.LocationTotal);
        edBarcodeScan=(EditText) mRootView.findViewById(R.id.barcodescan);
        btnEnterCode=(Button)mRootView.findViewById(R.id.btnEnterCode);
        dbHelper_Input=new DBHelper_Input(mActivity);

        tvLocation.setText(mLocationModel.getLoactionID());
        mAdapter=new ExportInventoryListAdapter(mActivity,mLocationModel.getInventoryModel());
        mList.setAdapter(mAdapter);

        intTotalIn=Integer.valueOf(dbHelper_Input.getInsertCount(mActivity.WarehouseID,mLocationModel.getLoactionID()));
        tvTotalIn.setText(Integer.toString(intTotalIn));
        intLocationTotal=Integer.valueOf(dbHelper_Input.getLocationCount(mActivity.WarehouseID,mLocationModel.getLoactionID()));
        tvLocationTotal.setText(Integer.toString(intLocationTotal));

        edBarcodeScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

                if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER && !edBarcodeScan.getText().toString().equals(""))
                {
                    String strBarCode=edBarcodeScan.getText().toString().trim();
                    edBarcodeScan.setText("");
                    if(strBarCode.length()==23 && strBarCode.substring(7, 14).trim().length()==7 && !strBarCode.substring(7,8).equals("9"))
                    {
                        String strProductCode = strBarCode.substring(0, 5);
                        String strClassLevel = strBarCode.substring(5, 7).trim();
                        String strBoxNumber = strBarCode.substring(7, 14);
                        String strGrossWeight = strBarCode.substring(14, 18).trim();
                        String strNetWeight = strBarCode.substring(18, 22).trim();

                        boolean bolGW=isNumeric(strGrossWeight);
                        boolean bolNW=isNumeric(strNetWeight);
                        if(bolGW && bolNW) {
                            SaveScan(strProductCode, strClassLevel, strBoxNumber, strGrossWeight, strNetWeight);
                        }else{
                            Toast.makeText(mActivity.getApplication(), "重量不為數字，請重新掃描", Toast.LENGTH_LONG).show();
                            Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(600);
                            createNotification(mActivity.getApplicationContext());
                        }

                    }else if(strBarCode.length()==27)
                    {
                        String strProductCode = strBarCode.substring(0, 5);
                        String strClassLevel = strBarCode.substring(9, 11).trim();
                        String strBoxNumber = strBarCode.substring(11, 18);
                        String strGrossWeight = strBarCode.substring(18, 22).trim();
                        String strNetWeight = strBarCode.substring(22, 26).trim();
                        boolean bolGW=isNumeric(strGrossWeight);
                        boolean bolNW=isNumeric(strNetWeight);
                        if(bolGW && bolNW) {
                            SaveScan(strProductCode, strClassLevel, strBoxNumber, strGrossWeight, strNetWeight);
                        }else{
                            Toast.makeText(mActivity.getApplication(), "重量不為數字，請重新掃描", Toast.LENGTH_LONG).show();
                            Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(600);
                            createNotification(mActivity.getApplicationContext());
                        }
                    }
                    else
                    {
                        Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(600);
                        createNotification(mActivity.getApplicationContext());
                        final AlertDialog.Builder BarcodeScanFaildialog = new AlertDialog.Builder(mActivity);
                        BarcodeScanFaildialog.setTitle(Constants.ScanFail);
                        if(strBarCode.substring(7,8).equals("9")){
                            BarcodeScanFaildialog.setMessage(strBarCode + " 年份為9，BarCode格式錯誤，請重新掃描！");
                        }else {
                            BarcodeScanFaildialog.setMessage(strBarCode + " BarCode格式錯誤，請重新掃描！");
                        }
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

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final InventoryModel inventoryModel=mAdapter.getItem(position);
                final AlertDialog.Builder DelBarcodedialog = new AlertDialog.Builder(mActivity);
                DelBarcodedialog.setTitle(Constants.ScanDel);
                DelBarcodedialog.setMessage(inventoryModel.getBoxNumber()+Constants.SuretoDel);
                DelBarcodedialog.setIcon(android.R.drawable.ic_dialog_alert);
                DelBarcodedialog.setCancelable(false);

                DelBarcodedialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(inventoryModel.getStatusCode().equals("I") || inventoryModel.getStatusCode().equals("NewI"))
                        {
                            mAdapter.Del(inventoryModel);
                            dbHelper_Input.DeleteDB((inventoryModel.getBoxNumber()));
                            intTotalIn--;
                            tvTotalIn.setText(Integer.toString(intTotalIn));
                        }
                        else
                        {
                            Toast.makeText(mActivity.getApplication(), "來源為資料庫，不允許刪除！", Toast.LENGTH_LONG).show();
                            return;
                        }
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
        hideKeyboard();

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!mAdapter.getItem(position).getStatusCode().equals("Y")) {
                    final AlertDialog.Builder changebuilder = new AlertDialog.Builder(mActivity);
                    View custom_view = mActivity.getLayoutInflater().inflate(R.layout.dialog_changeproductcode, null);
                    changebuilder.setTitle("變更產品批號");

                    final AlertDialog changedialog = changebuilder.setView(custom_view).create();
                    final Button btnCacel = (Button) custom_view.findViewById(R.id.btnCancel);
                    final Button btnSave = (Button) custom_view.findViewById(R.id.btnSave);
                    final EditText edProductCode = (EditText) custom_view.findViewById(R.id.edProductCode);

                    edProductCode.setText(mAdapter.getItem(position).getProductCode());
                    final String strBoxNumber = (mAdapter.getItem(position).getBoxNumber());

                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String strProductCode = edProductCode.getText().toString();

                            if (strProductCode.length() != 5) {
                                Toast.makeText(mActivity, "產品批號不足5碼，請重新輸入！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (mAdapter.updateProductCode(strBoxNumber, strProductCode)) {
                                Toast.makeText(mActivity, "產品批號修改成功！", Toast.LENGTH_SHORT).show();
                                changedialog.dismiss();
                            }
                        }
                    });
                    btnCacel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changedialog.dismiss();
                        }
                    });

                    changedialog.show();
                }else
                {
                    return;
                }
            }
        });

        btnEnterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder codebuilder = new AlertDialog.Builder(mActivity);
                View custom_view = mActivity.getLayoutInflater().inflate(R.layout.dialog_entercode, null);

                codebuilder.setTitle("Barcode自行輸入");

                final AlertDialog codedialog = codebuilder.setView(custom_view).create();
                final Button btnCacel=(Button)custom_view.findViewById(R.id.btnCancel);
                final Button btnSave=(Button)custom_view.findViewById(R.id.btnSave);
                final EditText edBoxNumber=(EditText)custom_view.findViewById(R.id.edBoxNumber);
                final EditText edProductCode=(EditText)custom_view.findViewById(R.id.edProductCode);
                final EditText edClassLevel=(EditText)custom_view.findViewById(R.id.edClassLevel);
                final EditText edGrossWeight=(EditText)custom_view.findViewById(R.id.edGrossWeight);
                final EditText edNetWeight=(EditText)custom_view.findViewById(R.id.edNetWeight);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strProductCode = edProductCode.getText().toString();
                        String strClassLevel = edClassLevel.getText().toString();
                        String strBoxNumber = edBoxNumber.getText().toString();
                        String strGrossWeight = edGrossWeight.getText().toString();
                        String strNetWeight = edNetWeight.getText().toString();

                        if(strBoxNumber.length()!=7)
                        {
                            Toast.makeText(mActivity, "箱號不足7碼，請重新輸入！", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(strProductCode.length()!=5)
                        {
                            Toast.makeText(mActivity, "產品批號不足5碼，請重新輸入！", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(strClassLevel.equals("") || strGrossWeight.equals("") || strNetWeight.equals(""))
                        {
                            Toast.makeText(mActivity, "訊息不完整，請重新輸入！", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(SaveScan(strProductCode,strClassLevel,strBoxNumber,strGrossWeight,strNetWeight)) {
                            codedialog.dismiss();
                        }
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

        return mRootView;
    }

    private boolean SaveScan(String strProductCode,String strClassLevel,String strBoxNumber,String strGrossWeight,String strNetWeight)
    {
        String strCreatorAccount=mPreferences.getAccount();
        String strDateCreator=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String strIndex = Integer.toString(mLocationModel.getInventoryModel().size() + 1);

        //先檢查該筆是否已存在資料庫，或被重複輸入
        for(int i=0;i<mActivity.mPagerAdapter.getCount();i++){
            for(InventoryModel item:mActivity.mPagerAdapter.mDataSource.get(i).getInventoryModel()){
                if(strBoxNumber.equals(item.getBoxNumber())){
                    Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(600);
                    createNotification(mActivity.getApplicationContext());
                    Toast.makeText(mActivity, "箱號：" + strBoxNumber + "已存在第"+item.getLocation()+"排！", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        InventoryModel item = new InventoryModel(mLocationModel.getWarehouseID(), mLocationModel.getLoactionID(), strBoxNumber
            , strProductCode, strClassLevel, strGrossWeight, strNetWeight,"NewI",strCreatorAccount,strDateCreator
            , "","",strIndex,"","");
        mAdapter.add(item);
        intTotalIn++;
        tvTotalIn.setText(Integer.toString(intTotalIn));
        mActivity.blnHasSave=false;
        return true;
    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG,"onResume ... "+ String.valueOf(mPageNumber+1));
    }

    private void hideKeyboard() {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
