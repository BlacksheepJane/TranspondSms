package com.tim.tsms.transpondsms.model.vo;

import java.io.Serializable;

public class PushPlusSettingVo implements Serializable {
    private String token;

    public PushPlusSettingVo() {
    }

    public PushPlusSettingVo(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
