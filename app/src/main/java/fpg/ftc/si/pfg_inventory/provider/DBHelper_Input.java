package fpg.ftc.si.pfg_inventory.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.ExportLoactionModel;
import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.Model.LocationModel;
import fpg.ftc.si.pfg_inventory.utils.Constants;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.*;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;

/**
 * Created by AndroidDev on 2014/10/14.
 */
public class DBHelper_Input  extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(DBHelper_Input.class);
    public static final String DATABASENAME = Constants.DBASE_NAME;
    public static final int DB_VERSION = 1;
    private Context mContext ;
    //private DBHelper_Input dbHelper_Input;
    private DBHelper_MakeInventory dbHelper_makeInventory;

    public DBHelper_Input(Context context)
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
    //Listview用
    public List<ExportLoactionModel> getLocationID(String warehouseID)
    {
        List<ExportLoactionModel> reasult=new ArrayList<ExportLoactionModel>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns=new String[] {BasicSettingLoactionColumn.WarehouseID,BasicSettingLoactionColumn.Location};
            final String selection=BasicSettingLoactionColumn.WarehouseID+"=?";
            final String selectionargs[]=new String[] {warehouseID};

            dbHelper_makeInventory=new DBHelper_MakeInventory(mContext);
            cursor=rDb.query(BasicSettingLoactionColumn.TableName,fetch_columns,selection,selectionargs,null,null,null);

            if(cursor!=null && cursor.moveToFirst())
            {
                do{
                        String LoactionID=cursor.getString(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Location));
                        ExportLoactionModel item=new ExportLoactionModel(cursor.getString(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Location))
//                                                                          ,""
//                                                                          ,"");
                                                                        ,dbHelper_makeInventory.GetTotalBox(warehouseID,LoactionID)
                                                                        ,dbHelper_makeInventory.GetTotalBoxHasMake(warehouseID,LoactionID));
                        reasult.add(item);
                }while(cursor.moveToNext());
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"getLoactionID="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return reasult;
    }
    //取得所有庫位，翻頁用
    public ArrayList<LocationModel> getLocation(String warehouseID)
    {
        ArrayList<LocationModel> reasult=new ArrayList<LocationModel>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns=new String[] {BasicSettingLoactionColumn.WarehouseID,BasicSettingLoactionColumn.Location};
            final String selection=BasicSettingLoactionColumn.WarehouseID+"=?";
            final String selectionargs[]=new String[] {warehouseID};

            cursor=rDb.query(BasicSettingLoactionColumn.TableName,fetch_columns,selection,selectionargs,null,null,null);

            if(cursor!=null && cursor.moveToFirst())
            {
                do{
                    LocationModel item=new LocationModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.WarehouseID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Location)));
                    reasult.add(item);
                }while(cursor.moveToNext());
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"getLoactionID="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return reasult;
    }
    //下拉式選單篩選用
    public ArrayList<ExportLoactionModel> getLoactionFromSpinner(String warehouseID,String productCode,String classLevel)
    {
        ArrayList<ExportLoactionModel> reasult=new ArrayList<ExportLoactionModel>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns=new String[] {InventoryColumn.Location,BasicSettingLoactionColumn.Total};

            if(productCode.equals(""))
            {
                final String selection= InventoryColumn.WarehouseID + "=?";
                final String selectionargs[] = new String[]{warehouseID};
                cursor=rDb.query(BasicSettingLoactionColumn.TableName,fetch_columns,selection,selectionargs,InventoryColumn.Location,null,null);
            }else if(classLevel.equals(""))
            {
                final String selection= InventoryColumn.WarehouseID + "=? and " + InventoryColumn.ProductCode + "=?";
                final String selectionargs[] = new String[]{warehouseID, productCode};
                cursor=rDb.query(InventoryColumn.TableName,fetch_columns,selection,selectionargs,InventoryColumn.Location,null,null);
            }else
            {
                final String strSQL="select Location ,count(*) as Total From Inventory where ProductCode ='"+productCode +"' and Class='"+classLevel+"' group by Location";
                cursor=rDb.rawQuery(strSQL,null);
            }

            if(cursor!=null && cursor.moveToFirst())
            {
                do{
                    ExportLoactionModel item=new ExportLoactionModel(
                             cursor.getString(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Location))
                            ,Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Total)))
                            ,"");
                    reasult.add(item);
                }while(cursor.moveToNext());
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"getLoactionID="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return reasult;
    }

    public List<InventoryModel> getInventory(String warehouseID,String loactionID,String InOut)
    {
        List<InventoryModel> reasult=new ArrayList<InventoryModel>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns=new String[] {InventoryColumn.WarehouseID,InventoryColumn.Location,InventoryColumn.BoxNumber,InventoryColumn.ProductCode
                                                        ,InventoryColumn.ClassLevel,InventoryColumn.GrossWeight,InventoryColumn.NetWeight,InventoryColumn.StatusCode,InventoryColumn.Remark
                                                        ,InventoryColumn.CreatorAccount,InventoryColumn.DateCreated,InventoryColumn.ModifierAccount,InventoryColumn.DateModified};
//            final String selection=InventoryColumn.WarehouseID+"=? and "+InventoryColumn.Location+"=?";
//            final String selectionargs[]=new String[] {warehouseID,loactionID};

            if(InOut.equals("I")) {
                final String selection=InventoryColumn.WarehouseID+"=? and "+InventoryColumn.Location+"=?";
                final String selectionargs[]=new String[] {warehouseID,loactionID};
                cursor = rDb.query(InventoryColumn.TableName, fetch_columns, selection, selectionargs, null, null, InventoryColumn.DateCreated + " DESC", null);
            }else {
                final String selection = InventoryColumn.WarehouseID + "=? and " + InventoryColumn.StatusCode + "=?";
                final String selectionargs[] = new String[]{warehouseID, InOut};

//                if (InOut.equals("I")) {
//                    cursor = rDb.query(InventoryColumn.TableName, fetch_columns, selection, selectionargs, null, null, InventoryColumn.DateCreated + " DESC", null);
//                } else {
                  cursor = rDb.query(InventoryColumn.TableName, fetch_columns, selection, selectionargs, null, null, InventoryColumn.DateModified + " DESC", null);
//                }
            }
                if (cursor != null && cursor.moveToFirst()) {
                    int i = 1;
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
                                , Integer.toString(i)
                                , ""
                                ,cursor.getString(cursor.getColumnIndexOrThrow(InventoryColumn.Remark)));
                        reasult.add(item);
                        i++;
                    } while (cursor.moveToNext());
                }

        }catch (Exception ex)
        {
            LOGE(TAG,"getInventory="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return reasult;
    }

    public boolean InsertDB(List<InventoryModel> inventoryModelList)
    {
        boolean resault = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();
            for(InventoryModel item:inventoryModelList)
            {
                final String[] fetch_columnsQ={InventoryColumn.BoxNumber};
                final String selectionargsQ[]=new String[]{item.getBoxNumber()};
                final String selectionQ=InventoryColumn.BoxNumber+"=?";

                Cursor cursorQ = null;
                cursorQ = wDb.query(InventoryColumn.TableName, fetch_columnsQ, selectionQ, selectionargsQ, null, null, null, null);
                if(cursorQ != null && cursorQ.moveToFirst()) {
                    LOGD(TAG,"有資料");
                }else{
                    final ContentValues values = new ContentValues(7);
                    values.put(InventoryColumn.WarehouseID, item.getWarehouseID());
                    values.put(InventoryColumn.Location, item.getLocation());
                    values.put(InventoryColumn.BoxNumber, item.getBoxNumber());
                    values.put(InventoryColumn.ProductCode, item.getProductCode());
                    values.put(InventoryColumn.ClassLevel, item.getClassLevel());
                    values.put(InventoryColumn.GrossWeight, item.getGrossWeight());
                    values.put(InventoryColumn.NetWeight, item.getNetWeight());
                    values.put(InventoryColumn.CreatorAccount, item.getCreatorAccount());
                    values.put(InventoryColumn.DateCreated, item.getDateCreated());
                    values.put(InventoryColumn.StatusCode, "I");
                    long insert_row_id = wDb.insert(InventoryColumn.TableName, null, values);
                    LOGD(TAG, "新增 紀錄 成功 insert_row_id:" + String.valueOf(insert_row_id));
                    LOGD(TAG, "新增 抄表紀錄 成功 insert_row_id:" + String.valueOf(insert_row_id));
                }
            }
            wDb.setTransactionSuccessful();
            resault = true;
        }catch (Exception ex)
        {
            LOGE(TAG,"InsertInventoryModelList="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
        return resault;
    }
    //提供已存檔，尚未上傳，但使用者變更為
    public boolean DeleteDB(String BoxNumber)
    {
        boolean resault = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();
            final String selection=InventoryColumn.BoxNumber+"=?";
            final String selectionargs[]=new String[]{BoxNumber};
            wDb.delete(InventoryColumn.TableName,selection,selectionargs);
            wDb.setTransactionSuccessful();
            resault = true;
        }catch (Exception ex)
        {
            LOGE(TAG,"UpdateInventory="+ex.getMessage());
        }finally
        {
            wDb.endTransaction();
        }
        return resault;
    }

    //目前入庫箱數
    public String getInsertCount(String WarehouseID,String Location)
    {
        String  TotalInsert="";
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String strSQL="select count(*) as Total From Inventory where "+InventoryColumn.WarehouseID+"='"+WarehouseID+"' and "+InventoryColumn.Location+"='"+Location+"' and "+InventoryColumn.StatusCode+" in ('I','NewI')";
            cursor=rDb.rawQuery(strSQL,null);
            if(cursor!=null && cursor.moveToFirst())
            {
                TotalInsert=Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Total)));
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"Select Insert Count="+ex.getMessage());
        }finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return TotalInsert;
    }
    //目前在庫
    public String getLocationCount(String WarehouseID,String Location)
    {
        String  LocationTotal="";
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String strSQL="select count(*) as Total From Inventory where "+InventoryColumn.WarehouseID+"='"+WarehouseID+"' and "+InventoryColumn.Location+"='"+Location+"' and "+InventoryColumn.StatusCode+" in ('Y')";;
            cursor=rDb.rawQuery(strSQL,null);
            if(cursor!=null && cursor.moveToFirst())
            {
                LocationTotal=Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(BasicSettingLoactionColumn.Total)));
            }

        }catch (Exception ex)
        {
            LOGE(TAG,"Select LocationCount Count="+ex.getMessage());
        }finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return LocationTotal;
    }



    public interface BasicSettingLoactionColumn
    {
        public static final String TableName="BasicSettingLoaction";

        public static final String WarehouseID="WarehouseID";

        public static final String Location="Location";

        public static final String Total="Total";

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

    }
}
