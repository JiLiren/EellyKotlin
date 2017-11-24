package com.eelly.bean

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
class MovieBean(var rating: RateBean, var genres:List<String>, var title:String,
                var casts:List<ActorBean>, var durations: List<String>, var collect_count: String,
                var mainland_pubdate:String, var has_video:Boolean, var original_title:String,
                var subtype:String, var directors:List<ActorBean>, var pubdates:List<String>,
                var year:String, var images:ImageBean, var alt:String, var id:String){
}