package fpg.ftc.si.pfg_inventory.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.ExportWarehouseModel;
import fpg.ftc.si.pfg_inventory.R;

/**
 * Created by AndroidDev on 2014/10/13.
 */
public class ExportWarehouseListAdapter extends ArrayAdapter<ExportWarehouseModel> {

    private List<ExportWarehouseModel> mDataSource;
    private final int mLayoutId;

    public ExportWarehouseListAdapter(final Activity context, List<ExportWarehouseModel> dataSource)
    {
        super(context, 0);
        // Get the layout Id
        mDataSource = dataSource;
        mLayoutId = R.layout.list_row_warehouse;
    }
    public int getCount() {
        if (mDataSource != null)
            return mDataSource.size();
        return 0;
    }

    public ExportWarehouseModel getItem(int position) {
        if (mDataSource != null)
            return mDataSource.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (mDataSource != null)
            return mDataSource.get(position).hashCode();
        return 0;
    }

    public void setItemList(List<ExportWarehouseModel> itemList) {
        this.mDataSource = itemList;
        //initDate();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        ExportWarehouseListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();
            holder.WarehouseID=(TextView)convertView.findViewById(R.id.warehouseid);
            holder.WarehouseName=(TextView)convertView.findViewById(R.id.warehousename);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final ExportWarehouseModel dataHolder = mDataSource.get(position);

        holder.WarehouseID.setText(dataHolder.getWarehouseID());
        holder.WarehouseName.setText(dataHolder.getWarehouseName());
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView WarehouseID;
        public TextView WarehouseName;
    }
}
