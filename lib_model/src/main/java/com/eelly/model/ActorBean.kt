package com.eelly.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Vurtne on 22-Nov-17.
 * 演员
 *
 * avatars 头像
 * name_ne 英文名
 * name    中文名
 * alt     网页链接
 * id      id
 */
data class ActorBean(var avatars:ImageBean, var name_ne:String, var name:String,
                     var alt:String, var id:String):Parcelable{

    constructor(source: Parcel) : this(
            source.readParcelable<ImageBean>(ImageBean::class.java.classLoader),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(source: Parcel , flags: Int) = with(source) {
        writeParcelable(this@ActorBean.avatars,flags)
        writeString(this@ActorBean.name_ne)
        writeString(this@ActorBean.name)
        writeString(this@ActorBean.alt)
        writeString(this@ActorBean.id)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ActorBean> = object : Parcelable.Creator<ActorBean> {
            override fun createFromParcel(source: Parcel): ActorBean = ActorBean(source)
            override fun newArray(size: Int): Array<ActorBean?> = arrayOfNulls(size)
        }
    }
}