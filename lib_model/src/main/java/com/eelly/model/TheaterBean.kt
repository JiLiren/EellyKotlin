package com.eelly.model

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
data class TheaterBean(var count: Int, var start :Double, var total:Double,
        var subjects:ArrayList<MovieBean>,var title:String)


