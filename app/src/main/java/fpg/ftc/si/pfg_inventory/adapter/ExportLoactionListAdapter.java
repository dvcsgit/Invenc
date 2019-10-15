package fpg.ftc.si.pfg_inventory.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.ExportLoactionModel;
import fpg.ftc.si.pfg_inventory.R;

/**
 * Created by AndroidDev on 2014/10/14.
 */
public class ExportLoactionListAdapter extends ArrayAdapter<ExportLoactionModel>{

    private List<ExportLoactionModel> mDataSource;
    private final int mLayoutId;

    public ExportLoactionListAdapter(final Activity context, List<ExportLoactionModel> dataSource)
    {
        super(context, 0);
        // Get the layout Id
        mDataSource = dataSource;
        mLayoutId = R.layout.list_row_loaction;
    }
    public int getCount() {
        if (mDataSource != null)
            return mDataSource.size();
        return 0;
    }

    public ExportLoactionModel getItem(int position) {
        if (mDataSource != null)
            return mDataSource.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (mDataSource != null)
            return mDataSource.get(position).hashCode();
        return 0;
    }

    public void setItemList(List<ExportLoactionModel> itemList) {
        this.mDataSource = itemList;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        ExportLoactionListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();

            holder.LoactionID=(TextView)convertView.findViewById(R.id.loctionId);

            holder.Total=(TextView)convertView.findViewById(R.id.total);
            holder.linTotal=(LinearLayout)convertView.findViewById(R.id.lintotal);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final ExportLoactionModel dataHolder = mDataSource.get(position);


        if(!dataHolder.getTotal().equals("")) {
            holder.LoactionID.setText(dataHolder.getLoactionID());
            holder.Total.setText(dataHolder.getTotal());
            holder.linTotal.setVisibility(View.VISIBLE);
        }else{
            holder.linTotal.setVisibility(View.GONE);
            holder.LoactionID.setText(dataHolder.getLoactionID());
        }

        holder.LoactionID.setText(dataHolder.getLoactionID());
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView LoactionID;
        public TextView Total;
        public LinearLayout linTotal;
    }
}
