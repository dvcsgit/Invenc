package fpg.ftc.si.pfg_inventory.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.R;

/**
 * Created by AndroidDev on 2014/10/17.
 */
public class ExportInventoryListAdapter extends ArrayAdapter<InventoryModel> {

    private List<InventoryModel> mDataSource;
    private final int mLayoutId;

    public ExportInventoryListAdapter(final Activity context, List<InventoryModel> dataSource)
    {
        super(context, 0);
        // Get the layout Id
        mDataSource = dataSource;
        mLayoutId = R.layout.list_row_inventory;
    }

    public int getCount() {
        if (mDataSource != null)
            return mDataSource.size();
        return 0;
    }

    public void add(InventoryModel inventoryModel) {
        if (mDataSource != null)
        {
            mDataSource.add(inventoryModel);
            Collections.sort(mDataSource,new FilesizeComparator());
            notifyDataSetChanged();
        }
    }

    public void Del(InventoryModel inventoryModel){
        if(mDataSource != null)
        {
            mDataSource.remove(inventoryModel);
            notifyDataSetChanged();
        }
    }

    public boolean update(InventoryModel inventoryModel){

        boolean resault=false;
        if(mDataSource !=null)
        {
            for(InventoryModel item:mDataSource)
            {
                if(item.getBoxNumber().equals(inventoryModel.getBoxNumber()))
                {
                    item.setStatusCode(inventoryModel.getStatusCode());
                    item.setModifierAccount(inventoryModel.getModifierAccount());
                    item.setDateModified(inventoryModel.getDateModified());
                    resault=true;
                }
            }
            Collections.sort(mDataSource,new DatetimeComparator());
            notifyDataSetChanged();
        }
        return resault;
    }

    public InventoryModel getItem(int position) {
        if (mDataSource != null)
            return mDataSource.get(position);
        return null;
    }

    public boolean updateProductCode(String BoxNumber,String ProductCode){
        if(mDataSource!=null)
        {
            for(InventoryModel item:mDataSource)
            {
                if(item.getBoxNumber().equals(BoxNumber))
                {
                    item.setProductCode(ProductCode);
                    return true;
                }
            }
            notifyDataSetChanged();
        }
        return false;
    }


    public void setItemList(List<InventoryModel> itemList) {
        this.mDataSource = itemList;
        //initDate();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        ExportInventoryListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent, false);
            holder = new ViewHolder();

            holder.BoxNumber=(TextView)convertView.findViewById(R.id.BoxNumber);
            holder.ProductCode=(TextView)convertView.findViewById(R.id.ProductCode);
            holder.ClassLevel=(TextView)convertView.findViewById(R.id.ClassLevel);
            holder.GrossWeight=(TextView)convertView.findViewById(R.id.GrossWeight);
            holder.NetWeight=(TextView)convertView.findViewById(R.id.NetWeight);
            holder.StatusCode=(LinearLayout)convertView.findViewById(R.id.DBStatus);
            holder.InOut=(TextView)convertView.findViewById(R.id.InOut);
            holder.Row=(TextView)convertView.findViewById(R.id.Row);
            holder.linRemark=(LinearLayout)convertView.findViewById(R.id.linRemark);
            holder.Remark=(TextView)convertView.findViewById(R.id.Remark);
            holder.linAll=(LinearLayout)convertView.findViewById(R.id.linAll);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final InventoryModel dataHolder = mDataSource.get(position);

        if(dataHolder.getStatusCode().equals("Y"))//來源為DB，表示在庫
        {
            holder.StatusCode.setBackgroundColor(Color.BLUE);
            holder.InOut.setText("在");
            holder.Row.setText("庫");
            holder.linRemark.setVisibility(View.GONE);
        }else if(dataHolder.getStatusCode().equals("NewI") || dataHolder.getStatusCode().equals("I"))//入
        {
            holder.StatusCode.setBackgroundColor(Color.RED);
            holder.InOut.setText("入");
            holder.Row.setText("庫");
            holder.linRemark.setVisibility(View.GONE);
        }else if(dataHolder.getStatusCode().equals("O"))//出
        {
            holder.StatusCode.setBackgroundColor(Color.RED);
            holder.InOut.setText(dataHolder.getLocation());
            holder.Row.setText("排");
            //holder.InOut.setText("出");
            //holder.Row.setText("庫");
            if(!dataHolder.Remark.equals("")) {
                holder.linRemark.setVisibility(View.VISIBLE);
            }else{
                holder.linRemark.setVisibility(View.GONE);
            }
        }else
        {
            holder.StatusCode.setBackgroundColor(Color.BLUE);
            holder.InOut.setText(dataHolder.getLocation());
            holder.Row.setText("排");
            holder.linRemark.setVisibility(View.GONE);
        }
        holder.BoxNumber.setText(dataHolder.getBoxNumber());
        holder.ProductCode.setText(dataHolder.getProductCode());
        holder.ClassLevel.setText(dataHolder.getClassLevel());
        holder.GrossWeight.setText(dataHolder.getGrossWeight());
        holder.NetWeight.setText(dataHolder.getNetWeight());
        holder.Remark.setText(dataHolder.getRemark());
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView BoxNumber;
        public TextView ProductCode;
        public TextView ClassLevel;
        public TextView GrossWeight;
        public TextView NetWeight;
        public LinearLayout StatusCode;
        public TextView InOut;
        public TextView Row;
        public LinearLayout linRemark;
        public TextView Remark;
        public LinearLayout linAll;
    }


    public class FilesizeComparator implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            InventoryModel file1 = (InventoryModel) obj1;
            InventoryModel file2 = (InventoryModel) obj2;

            int file1W = Integer.parseInt(file1.getIndex());
            int file2W = Integer.parseInt(file2.getIndex());
            if(file1W > file2W)
                return -1;
            if (file1W < file2W)
            {
                return 1;
            }
            if  (file1W == file2W)
                return 0;
                    return 0;
        }
    }

    public class DatetimeComparator implements Comparator{
        @Override
        public int compare(Object obj1, Object obj2) {
            InventoryModel inventoryModel1 = (InventoryModel) obj1;
            InventoryModel inventoryModel2 = (InventoryModel) obj2;

            String s1="1900-01-01 00:00:00";
            String s2="1900-01-01 00:00:00";

            if(!inventoryModel1.getDateModified().equals(""))
                s1=inventoryModel1.getDateModified();
            if(!inventoryModel2.getDateModified().equals(""))
                s2=inventoryModel2.getDateModified();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try
            {
                Date file1W = df.parse(s1);
                Date file2W = df.parse(s2);

                if(file1W.compareTo(file2W)>0){return -1;}
                else if(file1W.compareTo(file2W)<0){return 1;}
                else if(file1W.compareTo(file2W)==0){return 0;}

            } catch (ParseException e)
            {
                e.printStackTrace();
            }

            return 0;
        }
    }

}
