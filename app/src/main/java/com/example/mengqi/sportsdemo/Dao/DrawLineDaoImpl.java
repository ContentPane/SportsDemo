package com.example.mengqi.sportsdemo.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.example.mengqi.sportsdemo.Model.LatiLong;
import com.example.mengqi.sportsdemo.MyDatabaseHelper;
import com.example.mengqi.sportsdemo.interfaces.DrawLineDao;

import java.util.ArrayList;
import java.util.List;

public class DrawLineDaoImpl implements DrawLineDao {
    private final MyDatabaseHelper mDbHelper;
    private Context context;
    private static final String TAG = "DaoDrawLine";


    public DrawLineDaoImpl(Context context) {
        this.context = context;
        mDbHelper = new MyDatabaseHelper(context, "Latlng.db", null, 1);
    }

    @Override
    //往数据库中添加经纬度
    public boolean insertLatlng(LatiLong latiLong) {
        if (latiLong == null) {
            Log.d(TAG, "插入对象为空或者没有参数传入");
            return false;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Double latitude = latiLong.getLatitude();
        Double longitude = latiLong.getLongtitude();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        long insert = db.insert("Latlng", null, values);
        values.clear();
        db.close();
        if (insert != 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteLatlng(LatiLong latiLong) {
        return false;
    }

    @Override
    public boolean updateLatlng(LatiLong latiLong) {
        return false;
    }

    @Override
    public List<LatLng> queryLatlng() {
        List<LatLng> latiLongsList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query("Latlng", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            LatLng latiLong = new LatLng(latitude, longitude);
            latiLongsList.add(latiLong);
        }
        cursor.close();
        return latiLongsList;
    }

    //查询和数据库的个数
    public int getCount() {
        int count = 0;
        // 1得到一个可读的数据库
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from Latlng ", null);
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        return count;

    }


}
