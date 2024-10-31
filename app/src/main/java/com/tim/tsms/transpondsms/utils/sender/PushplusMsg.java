package com.tim.tsms.transpondsms.utils.sender;

import android.util.Log;


import java.io.IOException;
import java.net.URLEncoder;
import android.util.Base64;

import com.tim.tsms.transpondsms.utils.SettingUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class PushplusMsg {

    static String TAG = "PushplusMsg";

    public static void sendMsg(String msg) throws Exception{

        String pushPlusToken = SettingUtil.get_using_pushplus_token();
        if (pushPlusToken.equals("")) {
            return;
        }

        // 构建 webhook URL
        String webhookUrl = "http://www.pushplus.plus/send?token=" + pushPlusToken;

        final String msgf = msg;

        // 构建消息体
        String textMsg = "{ \"title\": \"title\", \"content\": \"" + msg + "\" }";

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),
                textMsg);

        final Request request = new Request.Builder()
                .url(webhookUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"onFailure：" + e.getMessage());
                SendHistory.addHistory("PushPlus转发:"+msgf+"onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                Log.d(TAG,"Code：" + String.valueOf(response.code())+responseStr);
                SendHistory.addHistory("PushPlus转发:"+msgf+"Code：" + String.valueOf(response.code())+responseStr);
            }
        });
    }
}
