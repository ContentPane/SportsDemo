package com.example.mengqi.sportsdemo.interfaces;

import com.baidu.mapapi.model.LatLng;
import com.example.mengqi.sportsdemo.Model.LatiLong;

import java.util.List;

public interface DrawLineDao {
    // 增
    boolean insertLatlng(LatiLong latiLong);
    // 删
    boolean deleteLatlng(LatiLong latiLong);
    // 改
    boolean updateLatlng(LatiLong latiLong);
    // 查
    List<LatLng> queryLatlng();
    //获取Count
    int getCount();
}
