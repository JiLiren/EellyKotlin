package com.eelly.model

import android.os.Parcel
import android.os.Parcelable


/**
 * @author Vurtne on 22-Nov-17.
 * 主题
 *
 * rating 排名信息
 * genres 电影分类
 * title  电影名
 * casts  整容
 * durations 时长
 * collect_count 播放次数
 * mainland_pubdate 大陆上映时间
 * has_video 是否有资源
 * original_title 电影原名
 * subtype：固定值 movie
 * directors：导演信息
 * pubdates：各地上映日期
 * year：上映年
 * images：剧照
 * alt：网页链接
 * id：电影 id，用于电影介绍
 *
 */
data class MovieBean(var rating: RateBean, var genres: List<String>, var title: String,
                     var casts: List<ActorBean>, var durations: List<String>, var collect_count: String,
                     var mainland_pubdate: String, var has_video: Boolean, var original_title: String,
                     var subtype: String, var directors: List<ActorBean>, var pubdates: List<String>,
                     var year: String, var images: ImageBean, var alt: String, var id: String) : Parcelable {

    constructor(source: Parcel) : this(
            source.readParcelable<RateBean>(RateBean::class.java.classLoader),
            ArrayList<String>().apply { source.readStringList(this) },
            source.readString(),

            ArrayList<ActorBean>().apply { source.readTypedList(this,ActorBean.CREATOR) },
            ArrayList<String>().apply { source.readStringList(this) },
            source.readString(),

            source.readString(),
            1 == source.readInt(),
            source.readString(),

            source.readString(),
            ArrayList<ActorBean>().apply { source.readTypedList(this,ActorBean.CREATOR) },
            ArrayList<String>().apply { source.readStringList(this) },

            source.readString(),
            source.readParcelable<ImageBean>(ImageBean::class.java.classLoader),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(this@MovieBean.rating, flags)
        writeStringList(this@MovieBean.genres)
        writeString(this@MovieBean.title)
        writeList(this@MovieBean.casts)
        writeList(this@MovieBean.durations)
        writeString(this@MovieBean.collect_count)
        writeString(this@MovieBean.mainland_pubdate)
        writeInt(if(this@MovieBean.has_video) 1 else 0)
        writeString(this@MovieBean.original_title)
        writeString(this@MovieBean.subtype)
        writeList(this@MovieBean.directors)
        writeStringList(this@MovieBean.pubdates)
        writeString(this@MovieBean.year)
        writeParcelable(this@MovieBean.images,flags)
        writeString(this@MovieBean.alt)
        writeString(this@MovieBean.id)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<MovieBean> = object : Parcelable.Creator<MovieBean> {
            override fun createFromParcel(source: Parcel): MovieBean = MovieBean(source)
            override fun newArray(size: Int): Array<MovieBean?> = arrayOfNulls(size)
        }
    }
}

