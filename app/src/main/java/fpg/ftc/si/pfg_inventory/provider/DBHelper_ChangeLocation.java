package fpg.ftc.si.pfg_inventory.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.pfg_inventory.Model.InventoryModel;
import fpg.ftc.si.pfg_inventory.utils.Constants;

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGD;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.LOGE;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;

/**
 * Created by AndroidDev on 2014/10/24.
 */
public class DBHelper_ChangeLocation extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(DBHelper_Input.class);
    public static final String DATABASENAME = Constants.DBASE_NAME;
    public static final int DB_VERSION = 1;
    private Context mContext ;

    public DBHelper_ChangeLocation(Context context)
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

    public List<String> getLocationID(String warehouseID)
    {
        List<String> resault = new ArrayList<String>();
        final SQLiteDatabase rDb = getReadableDatabase();
        resault.add("");
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns=new String[] {DBHelper_Input.BasicSettingLoactionColumn.Location};
            final String selection=DBHelper_Input.BasicSettingLoactionColumn.WarehouseID+"=?";
            final String selectionargs[]=new String[] {warehouseID};
            cursor=rDb.query(DBHelper_Input.BasicSettingLoactionColumn.TableName,fetch_columns,selection,selectionargs,null,null,null);
            if(cursor.moveToFirst())
            {
                do{
                    resault.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.BasicSettingLoactionColumn.Location)));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        catch (Exception ex)
        {
            LOGE(TAG,"getSpinnerLoactionID="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return resault;
    }
    public InventoryModel getInventory(String strBoxNumber,String strModifyAccount,String strDateModify,String strIndex,String strStatusCode)
    {
        InventoryModel resault = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns=new String[] {DBHelper_Input.InventoryColumn.WarehouseID, DBHelper_Input.InventoryColumn.Location
                    ,DBHelper_Input.InventoryColumn.BoxNumber,DBHelper_Input.InventoryColumn.ProductCode
                    ,DBHelper_Input.InventoryColumn.ClassLevel, DBHelper_Input.InventoryColumn.GrossWeight
                    ,DBHelper_Input.InventoryColumn.NetWeight, DBHelper_Input.InventoryColumn.StatusCode,DBHelper_Input.InventoryColumn.Remark
                    ,DBHelper_Input.InventoryColumn.CreatorAccount, DBHelper_Input.InventoryColumn.DateCreated
                    ,DBHelper_Input.InventoryColumn.ModifierAccount,DBHelper_Input.InventoryColumn.DateModified};

            final String selection=DBHelper_Input.InventoryColumn.BoxNumber+"=?";
            final String selectionargs[]=new String[] {strBoxNumber};

            cursor=rDb.query(DBHelper_Input.InventoryColumn.TableName,fetch_columns,selection,selectionargs,null,null,null);

            if(cursor!=null && cursor.moveToFirst())
            {

              resault=new InventoryModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.WarehouseID))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.Location))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.BoxNumber))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.ProductCode))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.ClassLevel))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.GrossWeight))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.NetWeight))
                            ,strStatusCode
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.CreatorAccount))
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.DateCreated))
                            ,strModifyAccount
                            ,strDateModify
                            ,strIndex
                            ,""
                            ,cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.Remark)));

            }
        }catch (Exception ex)
        {
            LOGE(TAG,"getInventoryfromBoxNumber="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return resault;
    }

    public boolean updateChangeLocation(List<InventoryModel> inventoryModelList)
    {
        boolean resault = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();
            for(InventoryModel item:inventoryModelList)
            {
                final ContentValues values = new ContentValues(4);
                values.put(DBHelper_Input.InventoryColumn.Location,item.getLocation());
                values.put(DBHelper_Input.InventoryColumn.StatusCode,item.getStatusCode());
                values.put(DBHelper_Input.InventoryColumn.ModifierAccount,item.getModifierAccount());
                values.put(DBHelper_Input.InventoryColumn.DateModified,item.getDateModified());
                values.put(DBHelper_Input.InventoryColumn.CarNo,item.getCarNo());
                final String selection=DBHelper_Input.InventoryColumn.BoxNumber+"=?";
                final String selectionargs[]=new String[] {item.getBoxNumber()};
                long update_row_id=wDb.update(DBHelper_Input.InventoryColumn.TableName,values,selection,selectionargs);
                LOGD(TAG," 換排更新  update_row_id:"  + String.valueOf(update_row_id));
            }
            wDb.setTransactionSuccessful();
            resault=true;
        }catch (Exception ex)
        {
            LOGE(TAG,"UpdateCaangeLocationInventoryModelList="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
        return resault;
    }
}
