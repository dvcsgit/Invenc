package fpg.ftc.si.pfg_inventory.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;


public final class PreferenceUtils {

    private static PreferenceUtils sInstance;
    private final SharedPreferences mPreferences;


    public PreferenceUtils(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferenceUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * 取得儲存路徑
     */
    public final String getFilePath() {
        String key = Constants.KEY_FILE_PATH_PROCESS;

        //預設放在SD目錄中
        String sd_path = "";
        //String sd_path = Environment.getExternalStorageDirectory().getAbsolutePath();

        //確定SD卡可讀寫
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + Constants.DB_Folder;

            File dirFile = new File(sd_path);

            if(!dirFile.exists()){//如果資料夾不存在

                dirFile.mkdir();//建立資料夾
            }
        }

        return mPreferences.getString(key, sd_path);
    }

    public void setServerIP(final String value)
    {
        String key = Constants.ServerIP;
        mPreferences.edit().putString(key,value).commit();
    }

    public String getServerIP()
    {
        String key=Constants.ServerIP;
        String ServerIP="122.146.31.93";
        return mPreferences.getString(key,ServerIP);
    }

    //記錄本次登入的account
    public void setAccount(final String value)
    {
        String key=Constants.account;
        mPreferences.edit().putString(key,value).commit();
    }
    //取出使用
    public String getAccount()
    {
        String key=Constants.account;
        String Account="";
        return mPreferences.getString(key,Account);
    }
    //記錄本次登入者的姓名
    public void setAccountName(final String value)
    {
        String key=Constants.accountName;
        mPreferences.edit().putString(key,value).commit();
    }
    //取出使用
    public String getAccountName()
    {
        String key=Constants.accountName;
        String AccountName="";
        return mPreferences.getString(key,AccountName);
    }


}
