package fpg.ftc.si.pfg_inventory.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.TextView;

import fpg.ftc.si.pfg_inventory.MakeInventory;
import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.R;
import fpg.ftc.si.pfg_inventory.provider.DBHelper_MakeInventory;
import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;


public class MakeInventoryFragment extends Fragment {


    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL = "total";
    public static final String ARG_LOCATION= "check_item";


    private LocationModel mLocationModel;
    private View mRootView;
    private MakeInventory mActivity;
    private PreferenceUtils mPreferences;
    private TextView tvLocation;
    private ListView mList;
    private DBHelper_MakeInventory dbHelper_makeInventory;

    private int mPageNumber;
    private int mTotalCount;

    public static MakeInventoryFragment create(int pageNumber,int totalCount,LocationModel locationModel) {
        MakeInventoryFragment fragment = new MakeInventoryFragment();
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

        mActivity = (MakeInventory) getActivity();
        mRootView = inflater.inflate(R.layout.fragment_location, container, false);
        mPreferences = PreferenceUtils.getInstance(mActivity);
        mList=(ListView) mRootView.findViewById(R.id.Inventory_list);
        tvLocation=(TextView) mRootView.findViewById(R.id.location);
        dbHelper_makeInventory=new DBHelper_MakeInventory(mActivity);

        tvLocation.setText(mLocationModel.getLoactionID());


        return inflater.inflate(R.layout.fragment_make_inventory, container, false);
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
