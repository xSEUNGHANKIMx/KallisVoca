package com.kallis.satvocabulary;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017-10-04.
 */

public class VocabDao {
    public static final String ID = "_id";
    public static final String WORD = "word";
    public static final String DESC = "desc";
    public static final String GROUPING = "grouping";
    public static final String BOOKMARK = "bookmark";

    public static final Uri CONTENT_URI = Uri.parse("content://" + VocabDao.class.getName());
    public static final String TABLE = "VOCABULARY";
    protected Handler mCallbackHandler = new Handler(Looper.getMainLooper());
    private static VocabDao sInstance;
    private final Context mAppContext;

    public VocabDao(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public interface VocabDaoCallBack<T> {
        void onCompleted(T data);

        void onFailure(String data);
    }

    static void onDbCreate(SQLiteDatabase db) throws SQLException {
        // @formatter:on
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WORD + " TEXT,"
                + DESC + " TEXT,"
                + GROUPING + " TEXT,"
                + BOOKMARK + " INTEGER DEFAULT 0);";
        db.execSQL(sql);
    }

    public static synchronized VocabDao getInstance(Context context) {
        if (sInstance == null) {
            synchronized (VocabDao.class) {
                if(sInstance == null) {
                    sInstance = new VocabDao(context);
                }
            }
        }
        return sInstance;
    }

    public VocabModelLoader<ArrayList<VocabModel>> getLoader() {
        return new VocabModelLoader<ArrayList<VocabModel>>(mAppContext, CONTENT_URI) {
            @Override
            public ArrayList<VocabModel> loadInBackground() {
                return loadAll();
            }
        };
    }

    public synchronized ArrayList<VocabModel> loadAll() {
        ArrayList<VocabModel> result = new ArrayList<VocabModel>();

        VocabDBManager dbMgr = VocabDBManager.getInstance(mAppContext);
        SQLiteDatabase db = dbMgr.getReadableDB();

        if (db != null) {
            try {
                db.beginTransaction();
                Cursor cursor = db.query(TABLE, null, null, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst() == true) {
                        do {
                            VocabModel model = fromCursor(cursor);
                            if (model != null) {
                                result.add(model);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        return result;
    }

    private VocabModel fromCursor(Cursor cursor) {
        VocabModel model = new VocabModel();
        ArrayList<VocabModel> memberList = null;

        model.setWord(cursor.getString(cursor.getColumnIndex(WORD)));
        model.setDesc(cursor.getString(cursor.getColumnIndex(DESC)));
        model.setGrouping(cursor.getString(cursor.getColumnIndex(GROUPING)));
        model.setBookmark(cursor.getInt(cursor.getColumnIndex(BOOKMARK)) == 1 ? true : false);

        return model;
    }

    public static boolean isTableExists(SQLiteDatabase db) {
        boolean isExist = false;

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    public static void migrateDataToSQLiteDB(final SQLiteDatabase db, final ArrayList<VocabModel> datalist) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SQLiteStatement sqlStatement;
                StringBuffer sql = new StringBuffer();

                sql.append("INSERT INTO " + TABLE + " (")
                        .append(ID + ",")
                        .append(WORD + ",")
                        .append(DESC + ",")
                        .append(GROUPING + ",")
                        .append(BOOKMARK + ") ")
                        .append("values(?,?,?,?,?)");

                sqlStatement = db.compileStatement(sql.toString());
                db.beginTransaction();

                try {
                    for (int i = 0; i < datalist.size(); ++i) {
                        VocabModel model = datalist.get(i);
                        if (model != null) {
                            int column = 1;

                            sqlStatement.bindNull(column++);
                            sqlStatement.bindString(column++, model.getWord());
                            sqlStatement.bindString(column++, model.getDesc());
                            sqlStatement.bindString(column++, model.getGrouping());
                            sqlStatement.bindLong(column++, model.isBookmark() == true ? 1 : 0);
                            sqlStatement.execute();
                        }
                    }
                    sqlStatement.close();

                    db.setTransactionSuccessful();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    if (db != null) {
                        db.endTransaction();
                    }
                }
            }
        }).start();
    }

    public static ArrayList<VocabModel>  getData(Context context) {
        ArrayList<VocabModel> dataset = new ArrayList<VocabModel>();
        try {
            JSONObject obj = new JSONObject(loadJsonFromAsset(context));
            JSONArray words = obj.getJSONArray("vocabulary");
            for (int i = 0; i < words.length(); i++){
                JSONObject jsonObject = words.getJSONObject(i);
                String word = jsonObject.getString("word");
                String desc = jsonObject.getString("description");
                String group = jsonObject.getString("grouping");
                dataset.add(new VocabModel(i, word, desc, group, false, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private static String loadJsonFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("vocabulary.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
