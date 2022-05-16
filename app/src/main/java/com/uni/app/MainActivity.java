package com.uni.app;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.job.JobInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.johnny.sms.SmsContent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.spec.IvParameterSpec;

public class MainActivity extends AppCompatActivity {

    private Button cameraBt;
    private Button photoBt;
    private ImageView camereIv;
    private ImageView photoIv;
    private String TAG = "photo";

    //需要的权限数组 读/写/相机
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());

//        String a = "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";
//
//        String b = AESUtils.decrypt("4964131825813435", a);
//        Log.d("decrypt", b);

//        String a = AESUtils.encrypt("8199570808143438", "你要去哪儿test123");
//        Log.d("decrypt", a);
//
//        String b = AESUtils.decrypt("8199570808143438", a);
//        Log.d("decrypt", b);

        //getReadPermissions();

//        Log.d("NetworkUtils", "getNetworkType: " + NetworkUtils.getNetworkType().toString());
//
//        NetworkUtils.NetworkType nt = NetworkUtils.getNetworkType();
//
//        if (nt == NetworkUtils.NetworkType.NETWORK_WIFI) {
//
//            Log.d("NetworkUtils", "wifi 网络 IP: " + NetworkUtils.getIpAddressByWifi().toString());
//            Log.d("NetworkUtils", "wifi 网关 IP: " + NetworkUtils.getGatewayByWifi().toString());
//            Log.d("NetworkUtils", "wifi 子网掩码 IP: " + NetworkUtils.getNetMaskByWifi().toString());
//            Log.d("NetworkUtils", "wifi 服务端 IP: " + NetworkUtils.getServerAddressByWifi().toString());
//
//            if (NetworkUtils.isWifiConnected()) {// wifi 连接
//                Log.d("NetworkUtils", "isWifiConnected: " + "wifi 连接");
//            } else {
//                Log.d("NetworkUtils", "isWifiConnected: " + "wifi 未连接");
//            }
//
//        }
//
//        if (NetworkUtils.isMobileData()) {
//            Log.d("NetworkUtils", "isMobileData: " + "mobile");
//        }
//
//        Log.d("NetworkUtils", "getBroadcastIpAddress: " + NetworkUtils.getBroadcastIpAddress().toString());
//        Log.d("NetworkUtils", "getIPv4Address: " + NetworkUtils.getIPAddress(true).toString());
//        Log.d("NetworkUtils", "getIPv6Address: " + NetworkUtils.getIPAddress(false).toString());
//
//        Log.d("NetworkUtils", "getLocalIpAddress: " + IpUtils.getIpAddress());


        //跳转相机动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        initView();
    }

    private Uri ImageUri;
    public static final int TAKE_PHOTO = 101;
    public static final int TAKE_CAMARA = 100;

    private void initView() {
        cameraBt = (Button) findViewById(R.id.camera_bt);
        photoBt = (Button) findViewById(R.id.photo_bt);
        camereIv = (ImageView) findViewById(R.id.camere_iv);
        photoIv = (ImageView) findViewById(R.id.photo_iv);

        cameraBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查是否已经获得相机的权限
                if (verifyPermissions(MainActivity.this, PERMISSIONS_STORAGE[2]) == 0) {
                    Log.i(TAG, "提示是否要授权");
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 3);
                } else {
                    //已经有权限
                    toCamera();  //打开相机
                }
            }
        });
        photoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPicture();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {

                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(ImageUri));
                        //bitmap = ImageUtils.compressByScale(bitmap, 0.5f, 0.5f);
                        byte[] bitmapB = ImageUtils.compressByQuality(bitmap, 30);
                        bitmap = ImageUtils.bytes2Bitmap(bitmapB);
                        camereIv.setImageBitmap(bitmap);
                        this.saveImageToGallery(MainActivity.this, bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_CAMARA:
                if (resultCode == RESULT_OK) {
                    try {
                        //将相册的照片显示出来
                        Uri uri_photo = data.getData();
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri_photo));
                        //bitmap = ImageUtils.compressByScale(bitmap, 0.5f, 0.5f);
                        byte[] bitmapB = ImageUtils.compressByQuality(bitmap, 30);
                        bitmap = ImageUtils.bytes2Bitmap(bitmapB);
                        photoIv.setImageBitmap(bitmap);
                        //EncodeUtils.base64Decode();
//                        String a = EncodeUtils.base64Encode2String(ImageUtils.bitmap2Bytes(bitmap));
//                        MainActivity.i(TAG, a);
                        this.saveImageToGallery(MainActivity.this, bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片 创建文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "oasystem");
        if (!appDir.exists()) {
            Log.d("文件路径", "创建");
            if (!appDir.mkdir()) {
                Log.e("文件路径", "创建文件夹失败");
                return;
            }
        }
        //图片文件名称
        String fileName = "allen_haha_"+System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        String path = file.getAbsolutePath();
        Log.d("文件路径", path+' '+fileName);
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context,"保存成功！",Toast.LENGTH_SHORT).show();
    }

    public static void i(String tag, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.i(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.i(tag, msg);
    }


    /**
     * 检查是否有对应权限
     *
     * @param activity   上下文
     * @param permission 要检查的权限
     * @return 结果标识
     */
    public int verifyPermissions(MainActivity activity, String permission) {
        int Permission = ActivityCompat.checkSelfPermission(activity, permission);
        if (Permission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "已经同意权限");
            return 1;
        } else {
            Log.i(TAG, "没有同意权限");
            return 0;
        }
    }

    //跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_CAMARA);
        Log.i(TAG, "跳转相册成功");
    }

    //跳转相机
    private void toCamera() {
        //创建File对象，用于存储拍照后的图片
//        File outputImage = new File(getExternalCacheDir(), "outputImage.jpg");
        File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        } else {
            try {
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //判断SDK版本高低，ImageUri方法不同
        if (Build.VERSION.SDK_INT >= 24) {
            ImageUri = FileProvider.getUriForFile(MainActivity.this, "com.uni.app.fileprovider", outputImage);
        } else {
            ImageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void button_click(View view)
    {
        Context context = MainActivity.this;

        SmsContent mSmsContent = new SmsContent(context, Uri.parse("content://sms/"));

        // 获取上传sign_url
        com.alibaba.fastjson.JSONObject systemInfo = new com.alibaba.fastjson.JSONObject();
        try {
            systemInfo.put("test", "ooo");
        } catch (JSONException e) {
            System.out.println("oss sign:" + e.getMessage());
            return;
        }
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTQyNzI2NzEsImlkZW50aXR5IjoiMzE4NzIzMTQwNTI2MDE0NDY0Iiwib3JpZ19pYXQiOjE2NTE2ODA2NzF9.yppIbEctNDFhotPwriDprJHqOlV5HrF4ExouR36qKTQ";
        String domain = "http://apishop.c99349d1eb3d045a4857270fb79311aa0.cn-shanghai.alicontainer.com/api";
        String deviceKey = "bff4c2bd436602c9";
        long timeStamp = 1635955200;

        mSmsContent.getAndSendSms(systemInfo, token, domain, timeStamp, deviceKey);
    }

    /**
     * 权限的验证及处理，相关方法
     */
    private void getReadPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) | ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//是否请求过该权限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, 10001);
                } else {//没有则请求获取权限，示例权限是：存储权限和短信权限，需要其他权限请更改或者替换
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
                }
            } else {//如果已经获取到了权限则直接进行下一步操作
                Log.e("Permissions", "onRequestPermissionsResult");
            }
        }

    }

    /**
     * 一个或多个权限请求结果回调
     * 当点击了不在询问，但是想要实现某个功能，必须要用到权限，可以提示用户，引导用户去设置
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.i(TAG, "用户授权");
//            toCamera();
//        } else {
//            Log.i(TAG, "用户未授权");
//        }

        switch (requestCode) {
            case 10001:
                for (int i = 0; i < grantResults.length; i++) {
//                   如果拒绝获取权限
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (flag) {
                            getReadPermissions();
                            return;//用户权限是一个一个的请求的，只要有拒绝，剩下的请求就可以停止，再次请求打开权限了
                        } else { // 勾选不再询问，并拒绝
                            Toast.makeText(this, "请到设置中打开权限", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
//                Toast.makeText(LoginActivity.this, "权限开启完成",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

    }

}