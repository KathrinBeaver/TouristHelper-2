package com.hse.touristhelper.qr.storage;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Alex on 10.05.2016.
 */
public class QRCodeObject extends RealmObject {
    private long time;
    private int type;
    @Required
    private String content;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
