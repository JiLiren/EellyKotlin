package com.eelly.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Vurtne on 22-Nov-17.
 * 图片
 *
 * small 小
 * medium 中
 * large 大
 */
data class ImageBean(var small:String, var large :String, var medium:String):Parcelable{


    constructor(source: Parcel):this(
         source.readString(),
         source.readString(),
         source.readString()
    )

    override fun describeContents(): Int = 0


    override fun writeToParcel(source: Parcel, flags : Int) = with(source){
        writeString(this@ImageBean.small)
        writeString(this@ImageBean.large)
        writeString(this@ImageBean.medium)
    }

    companion object {
        @JvmField val CRAETOR: Parcelable.Creator<ImageBean> = object : Parcelable.Creator<ImageBean> {
            override fun createFromParcel(source: Parcel): ImageBean = ImageBean(source)
            override fun newArray(size: Int): Array<ImageBean?> = arrayOfNulls<ImageBean>(size)
        }
    }

}