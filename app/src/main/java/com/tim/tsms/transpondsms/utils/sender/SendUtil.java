package com.tim.tsms.transpondsms.utils.sender;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tim.tsms.transpondsms.model.LogModel;
import com.tim.tsms.transpondsms.model.RuleModel;
import com.tim.tsms.transpondsms.model.SenderModel;
import com.tim.tsms.transpondsms.model.vo.DingDingSettingVo;
import com.tim.tsms.transpondsms.model.vo.PushPlusSettingVo;
import com.tim.tsms.transpondsms.model.vo.EmailSettingVo;
import com.tim.tsms.transpondsms.model.vo.QYWXGroupRobotSettingVo;
import com.tim.tsms.transpondsms.model.vo.SmsVo;
import com.tim.tsms.transpondsms.model.vo.WebNotifySettingVo;
import com.tim.tsms.transpondsms.model.vo.SocketSettingVo;
import com.tim.tsms.transpondsms.utils.LogUtil;
import com.tim.tsms.transpondsms.utils.RuleUtil;
import com.tim.tsms.transpondsms.utils.SettingUtil;

import java.util.List;


import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_EMAIL;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_PUSHPLUS;
import static com.tim.tsms.transpondsms.model.SenderModel.TYPE_SOCKET;

public class SendUtil {
    private static String TAG = "SendUtil";

    public static void send_msg(String msg){
        if(SettingUtil.using_dingding()){
            try {
                SenderDingdingMsg.sendMsg(msg);
            }catch (Exception e){
                Log.d(TAG,"发送出错："+e.getMessage());
            }

        }
        if(SettingUtil.using_pushplus()){
            try {
                SenderPushplusMsg.sendMsg(msg);
            }catch (Exception e){
                Log.d(TAG,"发送出错："+e.getMessage());
            }
        }
        if(SettingUtil.using_socket()){
            try {
                SenderSocketMsg.sendMsg(msg);
            }catch (Exception e){
                Log.d(TAG,"发送出错："+e.getMessage());
            }
        }
        if(SettingUtil.using_email()){
//            SenderMailMsg.send(SettingUtil.get_send_util_email(Define.SP_MSG_SEND_UTIL_EMAIL_TOADD_KEY),"转发",msg);
        }

    }
    public static void send_msg_list(Context context,List<SmsVo> smsVoList){
        Log.i(TAG, "send_msg_list size: "+smsVoList.size());
        for (SmsVo smsVo:smsVoList){
            SendUtil.send_msg(context,smsVo);
        }
    }
    public static void send_msg(Context context,SmsVo smsVo){
        Log.i(TAG, "send_msg smsVo:"+smsVo);
        RuleUtil.init(context);
        LogUtil.init(context);

        List<RuleModel> rulelist = RuleUtil.getRule(null,null);
        if(!rulelist.isEmpty()){
            SenderUtil.init(context);
            for (RuleModel ruleModel:rulelist
            ) {
                //规则匹配发现需要发送
                try{
                    if(ruleModel.checkMsg(smsVo)){
                        List<SenderModel> senderModels = SenderUtil.getSender(ruleModel.getSenderId(),null);
                        for (SenderModel senderModel:senderModels
                        ) {
                            LogUtil.addLog(new LogModel(smsVo.getMobile(),smsVo.getContent(),senderModel.getId(),JSON.toJSONString(smsVo.getSmsExtraVo())));
                            SendUtil.senderSendMsgNoHandError(smsVo,senderModel);
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, "send_msg: fail checkMsg:",e);
                }


            }

        }
    }
    public static void sendMsgByRuleModelSenderId(final Handler handError, RuleModel ruleModel,SmsVo smsVo, Long senderId) throws Exception {
        if(senderId==null){
            throw new Exception("先新建并选择发送方");
        }

        if(!ruleModel.checkMsg(smsVo)){
            throw new Exception("短信未匹配中规则");
        }

        List<SenderModel> senderModels = SenderUtil.getSender(senderId,null);
        if(senderModels.isEmpty()){
            throw new Exception("未找到发送方");
        }

        for (SenderModel senderModel:senderModels
        ) {
             //test
            //LogUtil.addLog(new LogModel(smsVo.getMobile(),smsVo.getContent(),senderModel.getId(),JSON.toJSONString(smsVo.getSmsExtraVo())));
            SendUtil.senderSendMsg(handError,smsVo,senderModel);
        }
    }
    public static void senderSendMsgNoHandError(SmsVo smsVo,SenderModel senderModel) {
        SendUtil.senderSendMsg(null,smsVo,senderModel);
    }
    public static void senderSendMsg(Handler handError,SmsVo smsVo, SenderModel senderModel) {

        Log.i(TAG, "senderSendMsg smsVo:"+smsVo+"senderModel:"+senderModel);
        switch (senderModel.getType()){
            case TYPE_EMAIL:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    EmailSettingVo emailSettingVo = JSON.parseObject(senderModel.getJsonSetting(), EmailSettingVo.class);
                    if(emailSettingVo!=null){
                        try {
                            SenderMailMsg.sendEmail(handError, emailSettingVo.getHost(),emailSettingVo.getPort(),emailSettingVo.getSsl(),emailSettingVo.getFromEmail(),
                                    emailSettingVo.getPwd(),emailSettingVo.getToEmail(),smsVo.getMobile(),smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: SenderMailMsg error "+e.getMessage() );
                        }

                    }
                }

                break;


            case TYPE_PUSHPLUS:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    PushPlusSettingVo pushPlusSettingVo = JSON.parseObject(senderModel.getJsonSetting(), PushPlusSettingVo.class);
                    if(pushPlusSettingVo!=null){
                        try {
                            // 此处的getMobile的作用再看
                            // SenderPushplusMsg.sendMsg(handError, pushPlusSettingVo.getToken(), smsVo.getMobile(), smsVo.getSmsVoForSend());
                            SenderPushplusMsg.sendMsg(handError, pushPlusSettingVo.getToken(), smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: pushplus error "+e.getMessage() );
                        }
                    }
                }
            case TYPE_SOCKET:
                //try phrase json setting
                if (senderModel.getJsonSetting() != null) {
                    SocketSettingVo socketSettingVo = JSON.parseObject(senderModel.getJsonSetting(), SocketSettingVo.class);
                    if(socketSettingVo!=null){
                        try {
                            SenderSocketMsg.sendMsg(handError, socketSettingVo.getIpAddress(), socketSettingVo.getPort(), smsVo.getSmsVoForSend());
                        }catch (Exception e){
                            Log.e(TAG, "senderSendMsg: socket error "+e.getMessage() );
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

}
