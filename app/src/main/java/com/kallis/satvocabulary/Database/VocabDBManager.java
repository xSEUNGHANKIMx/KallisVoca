package com.kallis.satvocabulary.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.kallis.satvocabulary.VocabConfig.VOCABULARY_DB_NAME;

/**
 * Created by Administrator on 2017-10-04.
 */

public class VocabDBManager {
    private volatile static VocabDBManager sInstance;

    private final Context mAppContext;
    private volatile VocabDBHelper mHelper;
    private volatile SQLiteDatabase mReadbleDB, mWritableDB;

    public static VocabDBManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (VocabDBManager.class) {
                if(sInstance == null) {
                    sInstance = new VocabDBManager(context);
                }
            }
        }
        return sInstance;
    }

    private VocabDBManager(Context context) {
        mAppContext = context.getApplicationContext();
        mHelper = new VocabDBHelper(mAppContext);
        openDB();
    }

    private void openDB() throws SQLException {
        mReadbleDB = mHelper.getReadableDatabase();
        mWritableDB = mHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDB() {
        if ((mHelper != null) && (mReadbleDB != null)) {
            return mReadbleDB;
        } else {
            return null;
        }
    }

    public SQLiteDatabase getWritableDB() {
        if ((mHelper != null) && (mWritableDB != null)) {
            return mWritableDB;
        } else {
            return null;
        }
    }

    static class VocabDBHelper extends SQLiteOpenHelper {

        protected static final int DB_VERSION = 1;
        protected Context mContext;

        VocabDBHelper(Context context) {
            super(context, VOCABULARY_DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                VocabDao.onDbCreate(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                VocabDao.migrateDataToSQLiteDB(db, VocabDao.getData(mContext));
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 0:
                case 1:
                    // fall through
                    break;
            }
        }
    }
}
