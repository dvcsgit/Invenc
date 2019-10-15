package fpg.ftc.si.pfg_inventory.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.MakeInventoryCountModel;
import fpg.ftc.si.pfg_inventory.utils.Constants;

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGD;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGE;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;

/**
 * Created by AndroidDev on 2015/1/6.
 */
public class DBHelper_MakeInventory extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(DBHelper_Input.class);
    public static final String DATABASENAME = Constants.DBASE_NAME;
    public static final int DB_VERSION = 1;
    private Context mContext ;

    public DBHelper_MakeInventory(Context context)
    {
        super(new DatabaseContext(context), DATABASENAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public List<MakeInventoryCountModel> getMakeInventory(String warehouseID,String loactionID)
    {
        List<MakeInventoryCountModel> reasult=new ArrayList<MakeInventoryCountModel>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try{
            final String strSQL="Select ProductCode,Class,MakeInventory,Count(*) as Count from Inventory " +
                    "where "+InventoryColumn.WarehouseID+"='"+warehouseID+
                    "' and "+InventoryColumn.Location+"='"+loactionID+"'  group by ProductCode,Class";
            cursor=rDb.rawQuery(strSQL,null);
            if(cursor!=null && cursor.moveToFirst())
            {
                do {
                    MakeInventoryCountModel makeInventoryCountModel = new MakeInventoryCountModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.ProductCode))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.ClassLevel))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.MakeInventory))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.Count)));

                    reasult.add(makeInventoryCountModel);
                } while (cursor.moveToNext());
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"getMakeInventory："+ex.getMessage());
        }finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            rDb.close();
        }
        return reasult;
    }

    public List<InventoryModel> getMakeInventoryDetail(String warehouseID,String loactionID,String productCode,String classLevel)
    {
        List<InventoryModel> reasult=new ArrayList<InventoryModel>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try{
            final String[] fetch_columns=new String[] {InventoryColumn.WarehouseID,InventoryColumn.Location,InventoryColumn.BoxNumber,InventoryColumn.ProductCode
                    ,InventoryColumn.ClassLevel,InventoryColumn.GrossWeight,InventoryColumn.NetWeight,InventoryColumn.StatusCode,InventoryColumn.Remark
                    ,InventoryColumn.CreatorAccount,InventoryColumn.DateCreated,InventoryColumn.ModifierAccount,InventoryColumn.DateModified};

            final String selection=InventoryColumn.WarehouseID+"=? and "+InventoryColumn.Location+"=? and "+InventoryColumn.ProductCode+"=? and "+InventoryColumn.ClassLevel+"=?";
            final String selectionargs[]=new String[] {warehouseID,loactionID,productCode,classLevel};

            cursor = rDb.query(InventoryColumn.TableName, fetch_columns, selection, selectionargs, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    InventoryModel item = new InventoryModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.WarehouseID))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.Location))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.BoxNumber))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.ProductCode))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.ClassLevel))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.GrossWeight))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.NetWeight))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.StatusCode))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.CreatorAccount))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.DateCreated))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.ModifierAccount))
                            , cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.DateModified))
                            , ""
                            , ""
                            ,cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.Remark)));
                    reasult.add(item);
                } while (cursor.moveToNext());
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"getMakeInventoryDetail："+ex.getMessage());
        }finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            rDb.close();
        }
        return reasult;
    }

    public String GetTotalBox(String warehouseID,String locationID)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        String strSQL;
        if(!locationID.equals("")) {
            strSQL = "Select Count(*) as Count from Inventory " +
                    "where " + InventoryColumn.WarehouseID + "='" + warehouseID +
                    "' and " + InventoryColumn.Location + "='" + locationID + "'";
        }else{
            strSQL = "Select Count(*) as Count from Inventory " +
                    "where " + InventoryColumn.WarehouseID + "='" + warehouseID +"'";
        }
        Cursor cursor =rDb.rawQuery(strSQL,null);
        String Total="";
        if(cursor!=null && cursor.moveToFirst()) {
            Total = cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.Count));
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return Total;
    }

    public String GetTotalBoxHasMake(String warehouseID,String locationID)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        String strSQL;
        if(!locationID.equals("")) {
            strSQL = "Select Count(*) as Count from Inventory " +
                    "where " + InventoryColumn.WarehouseID + "='" + warehouseID +
                    "' and " + InventoryColumn.Location + "='" + locationID + "' and " +
                    "" + InventoryColumn.MakeInventory + "='Y'";
        }else{
            strSQL = "Select Count(*) as Count from Inventory " +
                    "where " + InventoryColumn.WarehouseID + "='" + warehouseID +
                    "' and  "+ InventoryColumn.MakeInventory + "='Y'";
        }
        Cursor cursor =rDb.rawQuery(strSQL,null);
        String TotalBoxHasMake="";
        if(cursor!=null && cursor.moveToFirst()) {
            TotalBoxHasMake=cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.Count));
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return TotalBoxHasMake;
    }

    public boolean UpdateMakeInventory(List<MakeInventoryCountModel> makeInventoryCountModelList,String LoactionID)
    {
        boolean resault=false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.beginTransaction();

            final ContentValues valuesN=new ContentValues(1);//先一律改為N
            valuesN.put(InventoryColumn.MakeInventory,"");
            final String selectionN=InventoryColumn.Location+"=?";
            final String selectionNargs[]=new String[]{LoactionID};
            long update_row_idN=wDb.update(InventoryColumn.TableName,valuesN,selectionN,selectionNargs);
            LOGD(TAG," 盤點存檔先一律改為空  update_row_idN:"  + String.valueOf(update_row_idN));

            for(MakeInventoryCountModel item:makeInventoryCountModelList)
            {
                final ContentValues values = new ContentValues(1);
                values.put(InventoryColumn.MakeInventory,"Y");
                final String selection=InventoryColumn.Location+"=? and "+InventoryColumn.ProductCode+"=? and "+InventoryColumn.ClassLevel+"=?";
                final String selectionargs[]=new String[]{LoactionID,item.getProductCode(),item.getClassLevel()};
                long update_row_id=wDb.update(InventoryColumn.TableName,values,selection,selectionargs);
                LOGD(TAG," 盤點存檔  update_row_id:"  + String.valueOf(update_row_id));
            }
            wDb.setTransactionSuccessful();
            resault=true;
        }catch (Exception ex)
        {
            LOGE(TAG,"UpdateMakeInventoryModelList="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
            wDb.close();
        }
        return resault;
    }

    public interface InventoryColumn
    {
        public static final String TableName="Inventory";

        public static final String WarehouseID="WarehouseID";

        public static final String Location="Location";

        public static final String BoxNumber="BoxNumber";

        public static final String ProductCode="ProductCode";

        public static final String ClassLevel="Class";

        public static final String GrossWeight="GrossWeight";

        public static final String NetWeight="NetWeight";

        public static final String StatusCode="StatusCode";

        public static final String CreatorAccount="CreatorAccount";

        public static final String DateCreated="DateCreated";

        public static final String ModifierAccount="ModifierAccount";

        public static final String DateModified="DateModified";

        public static final String CarNo="CarNo";

        public static final String Remark="Remark";

        public static final String Count="Count";

        public static final String MakeInventory="MakeInventory";

    }
}
