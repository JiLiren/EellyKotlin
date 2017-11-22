package com.eelly.bean

/**
 * @author Vurtne on 22-Nov-17.
 * 剧院bean
 *
 *
 * count：返回数量
 * start：分页量
 * total: 数据库总数量
 * title: 名称
 * subjects ：电影
 */
data class TheaterBean(var count: Int, var start :Int, var total:Int,
        var subjects:List<MovieBean>,var title:String)


