package fpg.ftc.si.pfg_inventory.utils;


public final class Constants {

    // Call Webservice 用
    public static final String HTTP = "http://";
    public static final String URL="/PFGWarehouse/api/";
    //public static final String URL="/PFGWebApi/api/";
    //public static final String URL="/SparePartsApi/api/";
    public static final String Upload="upload?account=";
    public static final String StockUpload="StockUpload?account=";

    public static final String Login="login?";
    public static final String password="password";
    public static final String LoginMessage="登入訊息";
    public static final String LoginMessageEnterAccount="請輸入帳號密碼";
    public static final String Warehouse="/warehouse";
    public static final String WarehouseID="download?WarehouseID=";

    public static final String DB_Folder="Data_PFG";
    public static final String KEY_FILE_PATH_PROCESS = "KEY_FILE_PATH_PROCESS";
    public static final String DOWNLOAD_PROCESS_FILE_NAME = "SQLiteDB.zip";
    public static final String DBASE_NAME="PFGInventory.db";

    //Preference用
    public static final String ServerIP="ServerIP";
    public static final String account="account";
    public static final String accountName="accountName";

    //Dialog
    public static final String Sure="確定";
    public static final String Cancel="取消";
    public static final String SuretoDownload="下載會覆蓋原本資料，確定要下載？";

    //Scan
    public static final String ScanFail="掃描錯誤!";
    public static final String ScanDel="刪除掃描紀錄！";
    public static final String SuretoDel="確定要刪除？";
    public static final String SuretoReturn="確定回復為在庫？";

    //Save
    public static final String Messsage="訊息";
    public static final String SaveSuccess="存檔成功！";
    public static final String SaveFail="存檔失敗！";
}
