package fpg.ftc.si.pfg_inventory.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
 * Created by AndroidDev on 2015/1/15.
 */
public class ExportMakeInventoryLoactionListAdapter  extends ArrayAdapter<ExportLoactionModel>  {

    private List<ExportLoactionModel> mDataSource;
    private final int mLayoutId;

    public ExportMakeInventoryLoactionListAdapter(final Activity context, List<ExportLoactionModel> dataSource)
    {
        super(context, 0);
        // Get the layout Id
        mDataSource = dataSource;
        mLayoutId = R.layout.list_row_makeinventorylocaiton;
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
        ExportMakeInventoryLoactionListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();

            holder.LoactionID=(TextView)convertView.findViewById(R.id.loctionId);
            holder.FinishRate=(TextView)convertView.findViewById(R.id.finishrate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final ExportLoactionModel dataHolder = mDataSource.get(position);

        holder.LoactionID.setText(dataHolder.getLoactionID());
        holder.FinishRate.setText(dataHolder.getHasMake()+" / "+dataHolder.getTotal());

        if(dataHolder.getHasMake().equals(dataHolder.getTotal())){
            holder.FinishRate.setTextColor(Color.BLACK);
        }else{
            holder.FinishRate.setTextColor(Color.RED);
        }
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView LoactionID;
        public TextView FinishRate;
    }
}
