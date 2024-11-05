package com.tim.tsms.transpondsms.model.vo;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class SocketSettingVo implements Serializable {
    private String ipAddress;
    private int port;

    // 无参构造方法
    public SocketSettingVo() {
    }

    // 全参构造方法
    public SocketSettingVo(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    // Getter 和 Setter 方法

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
