package com.example.mengqi.sportsdemo.DrawUtils;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.mengqi.sportsdemo.Dao.DrawLineDaoImpl;

import java.util.ArrayList;
import java.util.List;

public class DrawOnMap {
    private static final String TAG = "DrawOnMap";
    private Context context;
    private List<LatLng> latiLongsList = new ArrayList<>();
    private DrawLineDaoImpl drawLineFormDB;


    public DrawOnMap(Context context) {
        this.context = context;
        drawLineFormDB = new DrawLineDaoImpl(context);
    }

    public List<OverlayOptions> drawRoad(BitmapDescriptor arrowBitmap , int lineWidth) {
        // 查询点
        latiLongsList = drawLineFormDB.queryLatlng();

        // 形成customTextureList添加纹理
        List<BitmapDescriptor> arrowList = new ArrayList<>();
        arrowList.add(arrowBitmap);

        //textureIndex
        List<Integer> textureIndex = new ArrayList<>();
        for (int i = 1; i < latiLongsList.size(); i++) {
            textureIndex.add(i);
        }

        // 新建数组存储OverLayOptions
        List<OverlayOptions> roadList = new ArrayList<>();

        // 画线路和箭头
        OverlayOptions arrowPolyline = new PolylineOptions().textureIndex(textureIndex)
                .customTextureList(arrowList)
                .zIndex(2)
                .color(0xFFFF0000)
                .dottedLine(true)
                .width(10)
                .points(latiLongsList);

        OverlayOptions ooPolyline = new PolylineOptions()
                .zIndex(1)
                .color(0xFFFF0000)
                .width(lineWidth)
                .points(latiLongsList);

        roadList.add(arrowPolyline);
        roadList.add(ooPolyline);
        return roadList;
    }

    public List<OverlayOptions> drawStartAndEnd (BitmapDescriptor startBitmap,BitmapDescriptor endBitmap) {
        // 新建数组存储OverLayOptions
        List<OverlayOptions> startAndEndList = new ArrayList<>();

        // 获取起点和终点的纬度
        LatLng latLngStart = drawLineFormDB.queryStartPointLatlng();
        LatLng latLngEnd = drawLineFormDB.queryEndPointLatlng();
        Log.d(TAG, "drawLine: LatlngEnd" + latLngEnd);

        //起点和终点OverLayOptions
        OverlayOptions startMarker = new MarkerOptions()
                .zIndex(3)
                .icon(startBitmap)
                .position(latLngStart);
        OverlayOptions endMarker = new MarkerOptions()
                .zIndex(4)
                .icon(endBitmap)
                .position(latLngEnd);

        startAndEndList.add(startMarker);
        startAndEndList.add(endMarker);
        return startAndEndList;
    }
}
