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

import static fpg.ftc.si.pfg_inventory.utils.LogUtils.makeLogTag;
import static fpg.ftc.si.pfg_inventory.utils.LogUtils.*;

/**
 * Created by AndroidDev on 2014/10/22.
 */
public class DBHelper_Output extends SQLiteOpenHelper {
    private static final String TAG = makeLogTag(DBHelper_Input.class);
    public static final String DATABASENAME = Constants.DBASE_NAME;
    public static final int DB_VERSION = 1;
    private Context mContext ;

    public DBHelper_Output(Context context)
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

    public List<String> getProductCode(String warehouseID)
    {
        List<String> resault = new ArrayList<String>();
        resault.add("");
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns = new String[] {DBHelper_Input.InventoryColumn.ProductCode};
            final String selection=DBHelper_Input.InventoryColumn.WarehouseID+"=?";
            final String selectionargs[]=new String[] {warehouseID};

            cursor=rDb.query(DBHelper_Input.InventoryColumn.TableName,fetch_columns,selection,selectionargs,DBHelper_Input.InventoryColumn.ProductCode,null,null);
            if(cursor.moveToFirst())
            {
                do{
                      resault.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.ProductCode)));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        catch (Exception ex)
        {
            LOGE(TAG,"getProductCode="+ex.getMessage());
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

    public List<String> getClassLevel(String warehouseID,String productCode)
    {
        List<String> resault = new ArrayList<String>();
        resault.add("");
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try
        {
            final String[] fetch_columns = new String[] {DBHelper_Input.InventoryColumn.ClassLevel};
            final String selection=DBHelper_Input.InventoryColumn.WarehouseID+"=? and "+DBHelper_Input.InventoryColumn.ProductCode+"=?";
            final String selectionargs[]=new String[] {warehouseID,productCode};

            cursor=rDb.query(DBHelper_Input.InventoryColumn.TableName,fetch_columns,selection,selectionargs,DBHelper_Input.InventoryColumn.ClassLevel,null,null);
            if(cursor.moveToFirst())
            {
                do{
                    resault.add(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper_Input.InventoryColumn.ClassLevel)));
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        catch (Exception ex)
        {
            LOGE(TAG,"getProductCode="+ex.getMessage());
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
    public boolean UpdateDB(List<InventoryModel> inventoryModelList)
    {
        boolean resault = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();
            for(InventoryModel item:inventoryModelList)
            {
                final ContentValues values = new ContentValues(3);
                values.put(DBHelper_Input.InventoryColumn.StatusCode,item.getStatusCode());
                values.put(DBHelper_Input.InventoryColumn.ModifierAccount,item.getModifierAccount());
                values.put(DBHelper_Input.InventoryColumn.DateModified,item.getDateModified());
                wDb.update(DBHelper_Input.InventoryColumn.TableName,values,DBHelper_Input.InventoryColumn.BoxNumber+"='"+item.getBoxNumber()+"'",null);
            }
            wDb.setTransactionSuccessful();
            resault = true;
        }catch (Exception ex)
        {
            LOGE(TAG,"UpdateInventoryModelList="+ex.getMessage());
        }finally
        {
            wDb.endTransaction();
        }
        return resault;

    }

    //提供已存檔，尚未上傳，但使用者變更為
    public boolean UpdateDB(String BoxNumber)
    {
        boolean resault = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();
            final ContentValues values = new ContentValues(1);
            values.put(DBHelper_Input.InventoryColumn.StatusCode,"Y");
            wDb.update(DBHelper_Input.InventoryColumn.TableName,values,DBHelper_Input.InventoryColumn.BoxNumber+"='"+BoxNumber+"'",null);
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
}
