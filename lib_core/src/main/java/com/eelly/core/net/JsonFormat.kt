package com.eelly.core.net

import android.text.TextUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.ArrayList

/**
 * @author vurtne on 2-Mar-18.
 */
object JsonFormat {

    fun <T> parseJsonElement(je: JsonElement, mClass: Class<T>, key: String): T? {
        try {
            if (je.isJsonArray) {
                val list = ArrayList<T>()
                var objT: T
                val jsonArray = je.asJsonArray
                var jsonObject: JsonObject?
                val length = jsonArray.size()
                for (i in 0 until length) {
                    jsonObject = jsonArray.get(i).asJsonObject
                    if (null != jsonObject) {
                        objT = GsonConvertUtils.getGson().fromJson(jsonObject, mClass)
                        list.add(objT)
                    }
                }
                //服务端那边底层,当数据为空的时候,默认是数组,这边经常有转换异常
                return if (list.isEmpty()) {
                    null
                } else list as T
            } else return if (je.isJsonObject) {
                //如果传进来的是String,就应该是带着一个字段的数据
                if (mClass == String::class.java) {
                    getStringResult(je, key) as T
                } else GsonConvertUtils.getGson().fromJson(je, mClass)
            } else {
                //需要的是一个对象,但是返回的是一个字符串,此类问题也经常出现,导致崩溃
                if (mClass == String::class.java) {
                    je.toString() as T
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getStringResult(je: JsonElement): String? {
        var res = getStringResult(je, "result")
        if (res == null) {
            res = getStringResult(je, "message")
        }
        if (res == null) {
            res = getStringResult(je, "msg")
        }
        if (res == null) {
            res = getStringResult(je, "success")
        }
        return res
    }

    fun getStringResult(je: JsonElement?, fieldName: String): String? {
        if (TextUtils.isEmpty(fieldName)) {
            return getStringResult(je!!)
        }
        if (je == null || !je.isJsonObject) {
            return null
        }
        try {
            val je2 = je.asJsonObject.get(fieldName)
            if (je2 != null) {
                return je2.asString
            }
        } catch (e: Exception) {
        }

        return null
    }

}