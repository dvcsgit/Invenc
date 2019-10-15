package fpg.ftc.si.pfg_inventory.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.MakeInventoryCountModel;
import fpg.ftc.si.pfg_inventory.R;

/**
 * Created by AndroidDev on 2015/1/12.
 */
public class ExportMakeInventoryListAdapter extends ArrayAdapter<MakeInventoryCountModel> {

    private List<MakeInventoryCountModel> mDataSource;
    private static HashMap<Integer,Boolean> isSelected;//選取項目
    private final int mLayoutId;
    private int mSelectedCount;//選取數

    public ExportMakeInventoryListAdapter(final Activity context, List<MakeInventoryCountModel> dataSource){
        super(context, 0);
        // Get the layout Id
        mDataSource = dataSource;
        mLayoutId = R.layout.list_row_make_inventory;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }

    public int getCount() {
        if (mDataSource != null)
            return mDataSource.size();
        return 0;
    }

    public MakeInventoryCountModel getItem(int position) {
        if (mDataSource != null)
            return mDataSource.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (mDataSource != null)
            return mDataSource.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Recycle ViewHolder's items
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();
            holder.chkSelect=(CheckBox)convertView.findViewById(R.id.select);
            holder.tvProductCode=(TextView)convertView.findViewById(R.id.ProductCode);
            holder.tvCount=(TextView)convertView.findViewById(R.id.Count);
            holder.tvClassLevel=(TextView)convertView.findViewById(R.id.ClassLevel);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        final MakeInventoryCountModel dataholder= mDataSource.get(position);

        holder.tvProductCode.setText(dataholder.getProductCode());
        holder.tvClassLevel.setText(dataholder.getClassLevel());
        holder.tvCount.setText(dataholder.getCount());
        holder.chkSelect.setChecked(getIsSelected().get(position));//防止勾選滑動跑掉

        return convertView;
    }

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    /**
     * 初始化 isSelected
     */
    private void initDate(){
//        for(int i=0; i<getCount();i++) {
//            getIsSelected().put(i,false);
//        }
        int pos = 0;
        for(MakeInventoryCountModel item : mDataSource)
        {
            boolean reasult=item.getMakeInventory().equals("Y")?true:false;
            getIsSelected().put(pos,reasult);
            pos++;
        }
    }

    public void setItemList(List<MakeInventoryCountModel> itemList) {
        this.mDataSource = itemList;
        initDate();
    }

    public List<MakeInventoryCountModel> getItemList() {
        return mDataSource;
    }

    public void setItemSelected(int position,boolean isChecked){

        this.getIsSelected().put(position, isChecked);
        if (isChecked) {
            mSelectedCount++;
        } else {
            mSelectedCount--;
        }
    }

    public static class ViewHolder {
        public CheckBox chkSelect;
        public TextView tvProductCode;
        public TextView tvCount;
        public TextView tvClassLevel;
    }

    public List<MakeInventoryCountModel> getSelectedParam(){
        List<MakeInventoryCountModel> result = new ArrayList<MakeInventoryCountModel>();
        int total = getCount();
        for (int i = 0; i < total; i++) {
            boolean test = this.getIsSelected().get(i);
            if(test)
            {
                MakeInventoryCountModel item = getItem(i);
                result.add(item);
            }
        }
        return result;
    };
}
