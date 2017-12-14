package com.eelly.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Vurtne on 22-Nov-17.
 * 评分
 *
 * max 最大分
 * average 该电影得分
 * stars 星数
 * min  最低分
 */
data class RateBean(var max :String,var average :Float ,var stars:Float,var min:String): Parcelable{

    constructor(source : Parcel):this(
            source.readString(),
            source.readFloat(),
            source.readFloat(),
            source.readString()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(source: Parcel, flags: Int) = with(source){
        writeString(this@RateBean.max)
        writeFloat(this@RateBean.average)
        writeFloat(this@RateBean.stars)
        writeString(this@RateBean.min)
    }

    companion object{
        @JvmField val CREATOR : Parcelable.Creator<RateBean> = object : Parcelable.Creator<RateBean>{
            override fun createFromParcel(source: Parcel): RateBean  = RateBean(source)
            override fun newArray(size: Int): Array<RateBean?>  = arrayOfNulls<RateBean>(size)
        }
    }


}