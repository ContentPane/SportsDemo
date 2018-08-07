package com.example.mengqi.sportsdemo.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.mengqi.sportsdemo.Model.LatiLong;
import com.example.mengqi.sportsdemo.Dao.DrawLineDaoImpl;
import com.example.mengqi.sportsdemo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SportsDemo";
    private static final int SPAN_SECOND = 1000000;


    private MapView mapView;
    private BaiduMap mBaiduMap;
    private Button mDlBtn;
    private Button mCdbBtn;


    // 权限初始化
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    // 是否第一次加载地图
    private boolean firstIn = true;

    // 默认显示Zoom
    private static final float BASE_LOCATION_ZOOM = 18.0f;

    // 百度地图初始化
    public LocationClient mLocationClient = null;
    MyLocationListener locListener = new MyLocationListener();

    // DrawLine数据库初始化
    private DrawLineDaoImpl drawLineFormDB;
    // 存储SQLite中经纬度的数组
    List<LatLng> latiLongsList = new ArrayList<>();

    //Maker
    View view = null;
    BitmapDescriptor bitmap = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.maker, null);
        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);



        //获取权限
        getPersimmions();

        //加载地图
        mapView = (MapView) findViewById(R.id.bmpView);
        mDlBtn = (Button) findViewById(R.id.btn_drawline);
        mCdbBtn = (Button) findViewById(R.id.btn_createdb);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //使用定位
        mLocationClient = new LocationClient(getApplicationContext());

        mLocationClient.registerLocationListener(locListener);//注册位置监听
        //地图参数初始化
        initLocation();
        //初始化地图滑动监听
        initListerner();
        //开启定位
        mLocationClient.start();

        drawLineFormDB = new DrawLineDaoImpl(this);
        mDlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawLine();
            }
        });


    }

    private Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }

    //初始换百度地图SDK各个参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(SPAN_SECOND);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);

        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

//        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setLocationNotify(true);
//
//        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationDescribe(true);
//
//        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIsNeedLocationPoiList(true);
//
//        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.setIgnoreKillProcess(false);
//
//        //可选，默认false，设置是否收集CRASH信息，默认收集
//        option.SetIgnoreCacheException(false);

        mLocationClient.setLocOption(option);
    }

    //位置信息的监听
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, "Long" + location.getLongitude());
            Log.d(TAG, "Lat" + location.getLatitude());
            Log.d(TAG, "Loctype" + location.getLocType());
            setPosition2Center(mBaiduMap, location, true);
            setLatlng2Db(location);
            int count = drawLineFormDB.getCount();
            Log.d(TAG, "onCreate:count " + count);
        }
    }

    /**
     * 设置中心点和添加marker
     *
     * @param map
     * @param bdLocation
     * @param isShowLoc
     */
    public void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);  // 设置定位中心点数据

        if (isShowLoc) {
            if (firstIn) {
                //把定位set入地图并更新地图
                updateMap(map, bdLocation, BASE_LOCATION_ZOOM);
                firstIn = false;
            } else {
                float zoomLevel = mBaiduMap.getMapStatus().zoom;
                updateMap(map, bdLocation, zoomLevel);
                Log.d(TAG, "setPosition2Center: " + zoomLevel);
            }
        }
    }

    //把经纬度插入到数据库
    private void setLatlng2Db(BDLocation location) {
        if (location != null) {
            // 根据BDLocation 对象获得经纬度以及详细地址信息
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, "setLatlng2Db: latitude:" + latitude);
            Log.d(TAG, "setLatlng2Db: longitude:" + longitude);
            LatiLong latiLong = new LatiLong();
            latiLong.setLatitude(latitude);
            latiLong.setLongitude(longitude);
            drawLineFormDB.insertLatlng(latiLong);
        }
    }

    private void queryLatlngFromDB() {
        latiLongsList = drawLineFormDB.queryLatlng();
    }

    // 更新地图
    private void updateMap(BaiduMap map, BDLocation bdLocation, float zoomLevel) {
        LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(zoomLevel);  //百度地图将地图的级别定义为【3~19】
        map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));// 把定位set入地图并更新地图
    }

    private void drawLine() {
        //查询经纬度的点
        queryLatlngFromDB();
//        List<OverlayOptions> arrowMarkerList = new ArrayList<>();
        /**
         *OverlayOptions:地图覆盖物选型基类
         *
         *PolylineOptions:创建折线覆盖物选项类
         * 	width(int width):设置折线线宽， 默认为 5， 单位：像素
         *  color(int color):设置折线颜色
         *  points(java.util.List<LatLng> points):设置折线坐标点列表
         * */

        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xFFFF0000).customTexture(bitmap).points(latiLongsList);
//        arrowMarkerList.add(ooPolyline);
//        for (LatLng markerLatlng : latiLongsList) {
//            OverlayOptions arrowMarker = new MarkerOptions().icon(bitmap).position(markerLatlng);
//            arrowMarkerList.add(arrowMarker);
//        }

        /**
         *addOverlay(OverlayOptions options):
         *			向地图添加一个 Overlay
         * */
        mBaiduMap.addOverlay(ooPolyline);


        //添加弧线
        /**
         *ArcOptions:弧线构造选项，继承自 #OverlayOptions
         * 	color(int color):设置弧线的颜色
         *  width(int width):设置弧线的线宽
         *  points(LatLng start, LatLng middle, LatLng end):设置弧线的起点、中点、终点坐标
         * */
//        OverlayOptions ooArc = new ArcOptions().color(0xAA00FF00).width(4).points(p1, p2, p3);
//        mBaiduMap.addOverlay(ooArc);
//        bitmap.recycle();
    }

    private void initListerner() {
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        });
    }

    //获取当前经纬度
    private void updateMapState(MapStatus status) {
        LatLng mCurrentLatLng = status.target;
        /*获取经纬度*/
        double lat = mCurrentLatLng.latitude;
        double lng = mCurrentLatLng.longitude;
        Log.d(TAG, "updateMapState: latlong" + lat + " " + lng);
    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            // 定位权限为必须权限，用户如果禁止，则每次进入都会申请
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.INTERNET);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {  // 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case SDK_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "请允许所有权限才能允许本软件", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
//        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
//        db.execSQL("DROP TABLE LATLNG");
        mapView = null;
        mLocationClient.unRegisterLocationListener(locListener);
        mLocationClient.stop();
    }


}

