//package com.example.myapplication400;
package com.logicsoft.myapplication30;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class DBManager {

    SQLiteDatabase db;
    //com.example.myapplication400.DBOpenHelper helper;
    com.logicsoft.myapplication30.DBOpenHelper helper;
    Context context;

    public DBManager(Context context) {
        this.context = context;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public String openDB(String dbName) {
        helper = new DBOpenHelper(context, dbName);
        db = helper.getReadableDatabase();
        String path = db.getPath();
        return path;
    }

    public void query(String query) {
        Cursor cursor;

        cursor = db.rawQuery(query, null);
        Log.d("db", "query :" + query);

        String[] names = cursor.getColumnNames();
        for(int i = 0; i < names.length; i++) {

            Log.d("ColumnNames", names[i]);
            Log.d("prj", "query done");
        }
        Log.d("prj", "total query");
        // db.close();
    }
}
