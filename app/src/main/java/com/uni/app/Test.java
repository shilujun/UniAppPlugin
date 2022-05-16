package com.uni.app;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.johnny.sms.HttpsUtils;
import com.uni.app.RSAUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Test {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) throws IOException {

        Test too = new Test();

//        //rsa加密 aseKey
//        String rsaEncrypt = "";
//        String aseKey = "bff4c2bd436602c9";
//        try {
//            rsaEncrypt = RSAUtils.encryptRSAToString(aseKey, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4SdE5JJ54SL7wZhOglZNba2vaDC83yLTNQ9ybP6CIO+HDvRCZ/TiFbNNmbfo/xmRx0zU0Y+tZCRrzJhZ9MzzAR7odQnxL/fQlcIuC2MjqvvZ0VpbsbFFqcuqbmzgkbH+p5DdcbrJrDZy5dNr1ccprT3LPGdYgClvRAHodviEcMQIDAQAB");
//        } catch (Exception e) {
//            System.out.println("postReportData rsaEncrypt exception:"+  e.getMessage());
//        }
//        System.out.println("postReportData rsaEncrypt: "+  rsaEncrypt);

        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTQyNzI2NzEsImlkZW50aXR5IjoiMzE4NzIzMTQwNTI2MDE0NDY0Iiwib3JpZ19pYXQiOjE2NTE2ODA2NzF9.yppIbEctNDFhotPwriDprJHqOlV5HrF4ExouR36qKTQ";
        String domain = "http://apishop.c99349d1eb3d045a4857270fb79311aa0.cn-shanghai.alicontainer.com/api";
        String deviceKey = "bff4c2bd436602c9";

        System.out.println("oss sign token:" + token);
        System.out.println("oss sign domain:" + domain + "/v1/oss/sign");
        System.out.println("oss sign deviceKey:" + deviceKey);

        OkHttpClient okHttpClient = new OkHttpClient();

        // 获取上传sign_url
        JSONObject json = new JSONObject();
        try {
            json.put("device_key", "ooo");
        } catch (JSONException e) {
           System.out.println("oss sign:" + e.getMessage());
           return;
        }
        System.out.println("oss sign:" + json.toString());
        RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .url(domain + "/v1/oss/sign")
                .post(requestBody)
                .build();
        Response response = null;
        String bodyStr = "";
        try {
            response = okHttpClient.newCall(request).execute();

            if(response == null || response.code() != 200) {
                System.out.println("oss sign:" + "response is Null or code not 200");
                return;
            }
            bodyStr = response.body().string();
        } catch (Exception e) {
            System.out.println("oss sign:" + e.toString());
        }
        System.out.println("oss sign:" + "bodyStr:" + bodyStr);

        //提炼 response 中的 json 内容里的 sign_url
        Gson gson = new Gson();
        SignResponseInfo signResponseInfo = gson.fromJson(bodyStr, new TypeToken<SignResponseInfo>(){}.getType());
        if(signResponseInfo != null && signResponseInfo.getData() != null) {
            System.out.println("oss sign:" + "signResponseInfo:" + signResponseInfo.toString());
            Map<String, String> signData = signResponseInfo.getData();
            if(signData != null && !signData.isEmpty()) {
                String signUrl = signData.get("sign_url");
                System.out.println("oss sign:" + signUrl);
                if(signUrl != null && !signUrl.isEmpty()) {

                    //转码 sign_url 因 json 提炼导致 \u2006 转义成了 &
                    try{
                        signUrl = signUrl.replaceAll("[&]", "\\u0026").trim();
                        System.out.println("oss sign url:" + signUrl);
                    }
                    catch(Exception e){
                        System.out.println("Wrong!");
                    }

                    //获取16位随机数
                    String aseKey = too.generateRandomStr(16); //秘钥-16位随机数
                    System.out.println("oss sign aseKey:" + aseKey);

                    //ase加密内容
                    String aesEncrypt = AESUtils.encrypt(aseKey, "{\"device_key\":\"ooo\"}");
                    System.out.println("oss sign aesEncrypt:" + aesEncrypt);

                    //构造请求 body
                    RequestBody requestBody1 = FormBody.create(aesEncrypt.getBytes(StandardCharsets.UTF_8));
                    try{
                        System.out.println("oss sign:" + requestBody1.contentType());
                    }
                    catch(Exception e){
                        System.out.println("Wrong!");
                    }

                    //构造 put 请求
                    Request request1 = new Request.Builder()
                            .url(signUrl)
                            .put(requestBody1)
                            .build();

                    //发起同步请求
                    try {

                        com.johnny.sms.HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
                        OkHttpClient.Builder  builder1=new  OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(10, TimeUnit.SECONDS)
                                .writeTimeout(10, TimeUnit.SECONDS)
                                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)//添加信任证书
                                .hostnameVerifier((hostname, session) -> true) //忽略host验证
                                .followRedirects(false);  //禁制OkHttp的重定向操作，我们自己处理重定向
                        OkHttpClient client =builder1.build();
                        Response response1 = client.newCall(request1).execute();
                        if(response == null || response.code() != 200) {
                            System.out.println("oss sign:postOssSign response is Null or code not 200" + response1.code() + " " + response1.message());
                            return;
                        }

                        System.out.println("oss sign:postReportData onFailure" + response1.message());
                        //调用report上报
                        too.postReportData(token, domain, signUrl, aseKey, deviceKey);
                    } catch (Exception e) {
                        System.out.println("oss sign:postOssSign onFailure" + e.getMessage());
                    }

                }
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postReportData(String token, String domain, String dataUrl, String aseKey, String deviceKey) {
        if(dataUrl == null || dataUrl.isEmpty()) {
            return;
        }
        //rsa加密 aseKey
        String rsaEncrypt = "";
        try {
            rsaEncrypt = RSAUtils.encryptRSAToString(aseKey, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4SdE5JJ54SL7wZhOglZNba2vaDC83yLTNQ9ybP6CIO+HDvRCZ/TiFbNNmbfo/xmRx0zU0Y+tZCRrzJhZ9MzzAR7odQnxL/fQlcIuC2MjqvvZ0VpbsbFFqcuqbmzgkbH+p5DdcbrJrDZy5dNr1ccprT3LPGdYgClvRAHodviEcMQIDAQAB");
        } catch (Exception e) {
            System.out.println("postReportData rsaEncrypt exception:"+  e.getMessage());
        }
        System.out.println("postReportData rsaEncrypt: "+  rsaEncrypt);

        OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("device_key", deviceKey);
            json.put("data_type", 3);
            json.put("data_url", dataUrl);
            json.put("aes_pwd", rsaEncrypt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .url(domain + "/v1/device/report-data")
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("postReportData onFailure:"+  e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                System.out.println("postReportData onResponse: "+  str);
            }
        });
    }

    /**
     * 获取随机字符串
     */
    private String generateRandomStr(int length) {
        ArrayList<String> strList = new ArrayList<String>();
        Random random = new Random();

//    int begin = 97;
//    //生成小写字母,并加入集合
//    for (int i = begin; i < begin + 26; i++) {
//      strList.add((char) i + "");
//    }
        //生成大写字母,并加入集合
//    begin = 65;
//    for(int i = begin; i < begin + 26; i++) {
//      strList.add((char)i + "");
//    }
        //将0-9的数字加入集合
        for (int i = 0; i < 10; i++) {
            strList.add(i + "");
        }

        StringBuffer sb = new StringBuffer();
        int size = strList.size();
        for (int i = 0; i < length; i++) {
            String randomStr = strList.get(random.nextInt(size));
            sb.append(randomStr);
        }
        return sb.toString();
    }
}