package com.johnny.sms;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

public class LocationUtils {

  private static double[] sLatAndLng = new double[]{0.0, 0.0};

  public static void registerLocationListener(Context context, LocationListener locationListener) {
    try {
      LocationManager locationManager = (LocationManager) context
          .getSystemService(Context.LOCATION_SERVICE);
      //从gps获取经纬度
      if (locationManager != null
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        locationManager
            .requestLocationUpdates(getBestProvider(context), 60 * 1000, 5, locationListener);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void unregisterLocationListener(Context context,
      LocationListener locationListener) {
    try {
      LocationManager locationManager = (LocationManager) context
          .getSystemService(Context.LOCATION_SERVICE);
      //从gps获取经纬度
      if (locationManager != null) {
        locationManager.removeUpdates(locationListener);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }



  public static JSONObject getLatAndLng(Context context) {
    JSONObject locationJson = new JSONObject();

    double latitude = 0.0;
    double longitude = 0.0;
    try {
      LocationManager locationManager = (LocationManager) context
          .getSystemService(Context.LOCATION_SERVICE);
      //从gps获取经纬度
      if (locationManager != null
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
          Location location = locationManager.getLastKnownLocation(getBestProvider(context));
          if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
          } else {//当GPS信号弱没获取到位置的时候又从网络获取
            locationJson.put("latitude", latitude);
            locationJson.put("longitude", longitude);
            return locationJson;
          }
        } else {    //从网络获取经纬度
          Location location = locationManager
              .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
          if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    locationJson.put("latitude", latitude);
    locationJson.put("longitude", longitude);
    return locationJson;
  }

//  public static double[] getLatAndLng(Context context) {
//    if (sLatAndLng[0] != 0 && sLatAndLng[1] != 0) {
//      LatLng latLng = CoordinateUtil
//          .transformFromWGSToGCJ(new LatLng(sLatAndLng[0], sLatAndLng[1]));
//      return new double[]{latLng.latitude, latLng.longitude};
//    }
//
//    double latitude = 0.0;
//    double longitude = 0.0;
//    try {
//      LocationManager locationManager = (LocationManager) context
//          .getSystemService(Context.LOCATION_SERVICE);
//      //从gps获取经纬度
//      if (locationManager != null
//          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
//          == PackageManager.PERMISSION_GRANTED
//          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
//          == PackageManager.PERMISSION_GRANTED) {
//
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//          Location location = locationManager.getLastKnownLocation(getBestProvider(context));
//          if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//          } else {//当GPS信号弱没获取到位置的时候又从网络获取
//            return getLngAndLatWithNetwork(context);
//          }
//        } else {    //从网络获取经纬度
//          Location location = locationManager
//              .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//          if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//          }
//        }
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    LatLng latLng = CoordinateUtil.transformFromWGSToGCJ(new LatLng(latitude, longitude));
//    return new double[]{latLng.latitude, latLng.longitude};
//  }


  private static String getBestProvider(Context context) {
    Location bestLocation = null;
    String bestProvider = null;
    try {
      LocationManager locationManager = (LocationManager) context
          .getSystemService(Context.LOCATION_SERVICE);
      if (locationManager != null
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
          Location l = locationManager.getLastKnownLocation(provider);
          if (l == null) {
            continue;
          }
          if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
            // Found best last known location: %s", l);
            bestLocation = l;
            bestProvider = provider;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bestProvider;

  }

  //从网络获取经纬度
  public static double[] getLngAndLatWithNetwork(Context context) {
    double latitude = 0.0;
    double longitude = 0.0;

    try {
      LocationManager locationManager = (LocationManager) context
          .getSystemService(Context.LOCATION_SERVICE);
      if (locationManager != null
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
          latitude = location.getLatitude();
          longitude = location.getLongitude();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new double[]{latitude, longitude};
  }

  public static void setLastLatAndLng(double latitude, double longitude) {
    sLatAndLng[0] = latitude;
    sLatAndLng[1] = longitude;
  }
}
