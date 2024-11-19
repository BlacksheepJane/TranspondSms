package com.tim.tsms.transpondsms.utils.sender;

import static com.tim.tsms.transpondsms.SenderActivity.NOTIFY;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tim.tsms.transpondsms.utils.SettingUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SenderPushplusMsg {

    static String TAG = "SenderPushPlusMsg";

    public static void sendMsg(final Handler handError, String token, String msg) throws Exception {
        String[] parts = msg.split("\n", 2);
        String title = parts[0];
        String content = parts.length > 1 ? parts[1] : "";
        Log.i(TAG, "sendMsg token:" + token + " from:" + title + " msg:" + content);
        if (token == null || token.isEmpty()) {
            return;
        }

        Map textMsgMap =new HashMap();
        textMsgMap.put("title", title);
        textMsgMap.put("content", content);
        String textMsg = JSON.toJSONString(textMsgMap);
        Log.i(TAG, "textMsg: " + textMsg);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),
                textMsg);

        String webhookUrl = "http://www.pushplus.plus/send?token=" + token;

        final Request request = new Request.Builder()
                .url(webhookUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Log.d(TAG, "onFailure：" + e.getMessage());

                if(handError != null){
                    android.os.Message msg = new android.os.Message();
                    msg.what = NOTIFY;
                    Bundle bundle = new Bundle();
                    bundle.putString("DATA","发送失败：" + e.getMessage());
                    msg.setData(bundle);
                    handError.sendMessage(msg);
                }


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                Log.d(TAG, "Code：" + String.valueOf(response.code()) + responseStr);

                if(handError != null){
                    android.os.Message msg = new android.os.Message();
                    msg.what = NOTIFY;
                    Bundle bundle = new Bundle();
                    bundle.putString("DATA","发送状态：" + responseStr);
                    msg.setData(bundle);
                    handError.sendMessage(msg);
                    Log.d(TAG, "Coxxyyde：" + String.valueOf(response.code()) + responseStr);
                }

            }
        });
    }



}
