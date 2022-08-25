package com.tencent.wxcloudrun.model;

public class ImageReplyBean {
    public String mediaId;
    public String text;

    public ImageReplyBean(String mediaId,String text) {
        this.mediaId = mediaId;
        this.text = text;
    }
}
