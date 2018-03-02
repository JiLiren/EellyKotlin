package com.eelly.core.util

import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * @author Vurtne on 8-Jun-17.
 * 判断手机是否是小米 或者魅族的工具类
 */
class ModelUtil {
    private val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"


    /** 是否是小米  */
    fun isMIUI(): Boolean {
        try {
            val prop = BuildProperties.Companion.newInstance()
            return (prop.getProperty(KEY_MIUI_VERSION_CODE, "") != null ||
                    prop.getProperty(KEY_MIUI_VERSION_NAME, "") != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, "") != null)
        } catch (e: IOException) {
            return false
        }

    }

    fun isMIUI6(): Boolean {
        try {
            val prop = BuildProperties.Companion.newInstance()
            val verStr = prop.getProperty(KEY_MIUI_VERSION_NAME, "")
            val version = Integer.parseInt(verStr.substring(1, 2))
            return version > 6 || version == 6
        } catch (e: IOException) {
            return false
        }

    }

    fun isMIUI9(): Boolean {
        try {
            val prop = BuildProperties.Companion.newInstance()
            val verStr = prop.getProperty(KEY_MIUI_VERSION_NAME, "")
            val version = Integer.parseInt(verStr.substring(1, 2))
            return version > 9 || version == 9
        } catch (e: IOException) {
            return false
        }

    }

    /** 是否是魅族  */
    fun isFlyme(): Boolean {
        try {
            val method = Build::class.java.getMethod("hasSmartBar")
            return method != null
        } catch (e: Exception) {
            return false
        }

    }



    private class BuildProperties(){

        private val properties: Properties

        init {
            properties = Properties()
            properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
        }

        fun containsKey(key: Any): Boolean {
            return properties.containsKey(key)
        }

        fun containsValue(value: Any): Boolean {
            return properties.containsValue(value)
        }

        fun entrySet(): MutableSet<MutableMap.MutableEntry<Any, Any>> {
            return properties.entries
        }

        fun getProperty(name: String): String {
            return properties.getProperty(name)
        }

        fun getProperty(name: String, defaultValue: String): String {
            return properties.getProperty(name, defaultValue)
        }

        fun isEmpty(): Boolean {
            return properties.isEmpty
        }

        fun keys(): Enumeration<Any> {
            return properties.keys()
        }

        fun keySet(): Set<Any> {
            return properties.keys
        }

        fun size(): Int {
            return properties.size
        }

        fun values(): Collection<Any> {
            return properties.values
        }

        companion object{
            @Throws(IOException::class)
            fun newInstance(): BuildProperties {
                return BuildProperties()
            }
        }

    }
}


