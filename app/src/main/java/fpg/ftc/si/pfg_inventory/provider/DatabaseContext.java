/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.pfg_inventory.provider;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import java.io.File;

import fpg.ftc.si.pfg_inventory.utils.PreferenceUtils;

public class DatabaseContext extends ContextWrapper {

    //private static final String TAG = makeLogTag(DatabaseContext.class);

    private PreferenceUtils mPreferences;
    private String mDatabaseFullPath;

    /**
     * 建構子
     * @param base
     */
        public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        mPreferences = PreferenceUtils.getInstance(this);
        mDatabaseFullPath = mPreferences.getFilePath();
        return new File(mDatabaseFullPath,name);
    };

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name,int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler)
    {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

}
