package fpg.ftc.si.pfg_inventory.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.R;

/**
 * Created by AndroidDev on 2015/1/19.
 */
public class ExportMakeInventoryDListAdapter extends ArrayAdapter<InventoryModel> {

    private List<InventoryModel> mDataSource;
    private final int mLayoutId;

    public ExportMakeInventoryDListAdapter(final Activity context, List<InventoryModel> dataSource)
    {
        super(context, 0);
        mDataSource = dataSource;
        mLayoutId = R.layout.list_row_make_inventoryd;
    }

    public int getCount() {
        if (mDataSource != null)
            return mDataSource.size();
        return 0;
    }

    public InventoryModel getItem(int position) {
        if (mDataSource != null)
            return mDataSource.get(position);
        return null;
    }

    public void setItemList(List<InventoryModel> itemList) {
        this.mDataSource = itemList;
        //initDate();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        ExportMakeInventoryDListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();

            holder.BoxNumber=(TextView)convertView.findViewById(R.id.BoxNumber);
            holder.ProductCode=(TextView)convertView.findViewById(R.id.ProductCode);
            holder.ClassLevel=(TextView)convertView.findViewById(R.id.ClassLevel);
            holder.GrossWeight=(TextView)convertView.findViewById(R.id.GrossWeight);
            holder.NetWeight=(TextView)convertView.findViewById(R.id.NetWeight);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final InventoryModel dataHolder = mDataSource.get(position);

        holder.BoxNumber.setText(dataHolder.getBoxNumber());
        holder.ProductCode.setText(dataHolder.getProductCode());
        holder.ClassLevel.setText(dataHolder.getClassLevel());
        holder.GrossWeight.setText(dataHolder.getGrossWeight());
        holder.NetWeight.setText(dataHolder.getNetWeight());
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView BoxNumber;
        public TextView ProductCode;
        public TextView ClassLevel;
        public TextView GrossWeight;
        public TextView NetWeight;
    }
}
