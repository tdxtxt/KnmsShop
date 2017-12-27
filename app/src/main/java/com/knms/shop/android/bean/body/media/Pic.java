package com.knms.shop.android.bean.body.media;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tdx on 2016/9/6.
 */
public class Pic implements Serializable{
    @SerializedName("imageId")
    public String id;
    @SerializedName(value = "imageUrl",alternate = {"imgUrl","imageName"})
    public String url;
    @SerializedName(value = "imageSeq",alternate = {"imageSorting","imageseq"})
    public String seq;
    public boolean isSelect;//是否被选中
    public String order;//选中的顺序

    @Override
    public String toString() {
        return "id:" + this.id + ";url:" + this.url;
    }
    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof Pic){
            if(this.toString().equals(arg0.toString())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
