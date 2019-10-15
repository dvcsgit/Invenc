package fpg.ftc.si.pfg_inventory.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.OutputLocationDetail;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.adapter.ExportInventoryListAdapter;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGD;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGI;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;


public class LocationOutputFragment extends Fragment {
    private static final String TAG = makeLogTag(LocationFragment.class);

    private View mRootView;
    private OutputLocationDetail mActivity;
    private LocationModel mLocationModel;
    private ExportInventoryListAdapter mAdapter;
    private ListView mList;
    private PreferenceUtils mPreferences;

    //頁面控制項
    private TextView mPagerInfo;//用來顯示目前總有幾筆資料,目前在第幾筆

    private TextView tvLocation;
    private EditText edBarcodeScan;

    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL = "total";
    public static final String ARG_LOCATION= "check_item";

    private int mPageNumber;
    private int mTotalCount;

    public static LocationOutputFragment create(int pageNumber,int totalCount,LocationModel locationModel) {
        LocationOutputFragment fragment = new LocationOutputFragment();
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
        mActivity = (OutputLocationDetail) getActivity();
        mRootView = inflater.inflate(R.layout.fragment_location_output, container, false);
        mPreferences = PreferenceUtils.getInstance(mActivity);
        mList=(ListView) mRootView.findViewById(R.id.Inventory_list);
        tvLocation=(TextView) mRootView.findViewById(R.id.location);
        edBarcodeScan=(EditText) mRootView.findViewById(R.id.barcodescan);

        tvLocation.setText(mLocationModel.getLoactionID());
        mAdapter=new ExportInventoryListAdapter(mActivity,mLocationModel.getInventoryModel());
        mList.setAdapter(mAdapter);

        edBarcodeScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(event.getKeyCode()== KeyEvent.KEYCODE_ENTER && !edBarcodeScan.getText().toString().equals(""))
                {
                    final AlertDialog.Builder BarcodeScanFaildialog = new AlertDialog.Builder(mActivity);
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
                    if(strBarCode.length()==23)
                    {
                        String strProductCode = strBarCode.substring(0, 5);
                        String strClassLevel = strBarCode.substring(5, 7);
                        String strBoxNumber = strBarCode.substring(7, 14);
                        String strGrossWeight = strBarCode.substring(15, 18);
                        String strNetWeight = strBarCode.substring(19, 22);
                        String strModifyAccount=mPreferences.getAccount();
                        String strDataModify=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String strIndex = Integer.toString(mLocationModel.getInventoryModel().size() + 1);
                        InventoryModel item = new InventoryModel(mLocationModel.getWarehouseID(), mLocationModel.getLoactionID(), strBoxNumber
                                , strProductCode, strClassLevel, strGrossWeight, strNetWeight
                                , "O", "", "", strModifyAccount, strDataModify, strIndex, "","");

                        boolean resault=mAdapter.update(item);

                        if(!resault){
                            BarcodeScanFaildialog.setMessage(strBarCode+" 該排並無此箱號，請確認後重新掃描出庫！");
                            BarcodeScanFaildialog.show();
                        }else{
                            mActivity.blnHasSave=false;
                        }
                    }else
                    {
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

                final AlertDialog.Builder ReturnBarcodedialog = new AlertDialog.Builder(mActivity);
                ReturnBarcodedialog.setTitle(Constants.Messsage);
                ReturnBarcodedialog.setMessage(inventoryModel.getBoxNumber()+Constants.SuretoReturn);
                ReturnBarcodedialog.setIcon(android.R.drawable.ic_dialog_alert);
                ReturnBarcodedialog.setCancelable(false);

                ReturnBarcodedialog.setPositiveButton(Constants.Sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        inventoryModel.setStatusCode("Y");//不出庫了，改回Y
                        inventoryModel.setModifierAccount(mPreferences.getAccount());
                        inventoryModel.setDateModified(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        mAdapter.update(inventoryModel);
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

        hideKeyboard();
        return mRootView;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
