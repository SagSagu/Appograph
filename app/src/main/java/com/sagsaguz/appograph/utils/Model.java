package com.sagsaguz.appograph.utils;

import android.graphics.Bitmap;

public class Model {

    public static final int IMAGE_TYPE1=0;
    public static final int IMAGE_TYPE2=1;
    public static final int IMAGE_TYPE3=2;

    public int type;
    public Bitmap data;
    public Bitmap image2;

    public Model(int type, Bitmap data)
    {
        this.type=type;
        this.data=data;
    }

    public Model(int type, Bitmap data1, Bitmap data2)
    {
        this.type=type;
        this.data=data1;
        this.image2=data2;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getData() {
        return data;
    }

    public void setData(Bitmap data) {
        this.data = data;
    }

    public Bitmap getImage2() {
        return image2;
    }

    public void setImage2(Bitmap image2) {
        this.image2 = image2;
    }
}
