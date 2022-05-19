package com.johnny.sms;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.alibaba.fastjson.JSONObject;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.common.UniModule;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationUtils extends UniModule {
  private LocationManager locationManager;
  static LocationUtils locationUtils;

  public static LocationUtils getInstance() {
    if (locationUtils == null) {
      locationUtils = new LocationUtils();
    }
    return locationUtils;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @UniJSMethod()
  public JSONObject getLatAndLng(Context context) {
    JSONObject locationMJson = new JSONObject();
    if (!isOPenGPS(context)){
      return locationMJson;
    }

    String strLocation = "";
    DecimalFormat df = new DecimalFormat("#####0.0000");
    if (!checkPermission(context, permission.ACCESS_COARSE_LOCATION)) {
    }
    try {
      //获取系统的服务，
      locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      //创建一个criteria对象
      Criteria criteria = new Criteria();
      criteria.setAccuracy(Criteria.ACCURACY_COARSE);
      //设置不需要获取海拔方向数据
      criteria.setAltitudeRequired(false);
      criteria.setBearingRequired(false);
      //设置允许产生资费
      criteria.setCostAllowed(true);
      //要求低耗电
      criteria.setPowerRequirement(Criteria.POWER_LOW);
      String provider = locationManager.getBestProvider(criteria, true);
      Log.e("wqs", "Location Provider is " + provider);
      @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
      Log.w("wqs", "经纬度信息: " + location.getLatitude()+"---"+location.getLongitude());
      /**
       * 重要函数，监听数据测试
       * 位置提供器、监听位置变化的时间间隔（毫秒），监听位置变化的距离间隔（米），LocationListener监听器
       */
//           locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    lm.removeUpdates(locationListener);
//                }
//            },2000);

      //第一次获得设备的位置
      if (location != null) {
//                strLocation = df.format(location.getLatitude()) + "," + df.format(location.getLongitude());
        // 耗时操作
        strLocation += "" + getLocationAddress(context, location);
        if (strLocation.equals("")) {
          strLocation += "" + convertAddress(context, location.getLatitude(), location.getLongitude());
        }
      }
      locationMJson.put("latitude", location.getLatitude());
      locationMJson.put("longitude", location.getLongitude());

    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return locationMJson;
  }

  /**
   * @param latitude  经度
   * @param longitude 纬度
   * @return 详细位置信息 GeoCoder是基于后台backend的服务，因此这个方法不是对每台设备都适用。
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  @UniJSMethod()
  public String convertAddress(Context context, double latitude, double longitude) {
    Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
    StringBuilder mStringBuilder = new StringBuilder();

    try {
      List<Address> mAddresses = mGeocoder.getFromLocation(latitude, longitude, 1);
      if (!mAddresses.isEmpty()) {
        Address address = mAddresses.get(0);
        mStringBuilder.append(address.getLocality()).append(address.getThoroughfare());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return mStringBuilder.toString();
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @UniJSMethod()
  private boolean checkPermission(Context context, permission permName) {
    int perm = context.checkCallingOrSelfPermission("android.permission." + permName.toString());
    return perm == PackageManager.PERMISSION_GRANTED;
  }

  private enum permission {
    ACCESS_COARSE_LOCATION,
    ACCESS_FINE_LOCATION
  }
  @RequiresApi(api = Build.VERSION_CODES.M)
  @UniJSMethod()
  private String getLocationAddress(Context mContext, Location location) {
    String addNow = "";
    Geocoder geoCoder = new Geocoder(mContext, Locale.CHINESE);
    try {
      List<Address> addresses = geoCoder.getFromLocation(
          location.getLatitude(), location.getLongitude(),
          1);
      Address address = addresses.get(0);
      Log.w("wqs", "远程获取定位全部为: " + address.toString());
      // Address[addressLines=[0:"中国",1:"北京市海淀区",2:"华奥饭店公司写字间中关村创业大街"]latitude=39.980973,hasLongitude=true,longitude=116.301712]

      if (address.getAddressLine(0) != null && !address.getAddressLine(0).equals("")) {
        addNow = address.getAddressLine(0);
        Log.w("wqs", "获取成功第一种: " + addNow);
      } else if (addNow.equals("") && address.getFeatureName() != null && !address.getFeatureName().equals("")) {
        addNow = address.getLocality() + address.getFeatureName();
        Log.w("wqs", "获取成功第二种: " + addNow);
      } else {
        int maxLine = address.getMaxAddressLineIndex();
        if (maxLine >= 2) {
          addNow = address.getAddressLine(1) + address.getAddressLine(2);
        } else {
          addNow = address.getAddressLine(1);
        }
        Log.w("wqs", "获取成功第三种: " + addNow);
      }


    } catch (IOException e) {
      addNow = "";
      e.printStackTrace();
    }
    if (addNow.contains("null")) {
      addNow = addNow.replaceAll("null", "");
    }
    return addNow;
  }

  /**
   * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
   *
   * @param context
   * @return true 表示开启
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  @UniJSMethod()
  private boolean isOPenGPS(final Context context) {
    LocationManager locationManager
        = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    // GPS定位
    boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    // 网络服务定位
    boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    if (gps || network) {
      return true;
    }
    return false;
  }
}
