package com.eelly.bean

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
data class AcatorBean(var avatars:List<ImageBean>, var name_ne:String, var name:String,
                      var alt:String, var id:String)