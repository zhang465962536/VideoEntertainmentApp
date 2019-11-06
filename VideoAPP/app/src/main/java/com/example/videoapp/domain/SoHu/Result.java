package com.example.videoapp.domain.SoHu;

import com.google.gson.annotations.Expose;

/**
 * 搜狐数据频道数据返回集
 */
public class Result {

    @Expose
    private long status;

    @Expose
    private String statusText;

    //for 列表页
    @Expose
    private Data data;

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}

