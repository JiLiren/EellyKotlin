package com.eelly.core.widget.refresh.constant

/**
 * @author Vurtne on 14-Dec-17.
 */

enum class DimensionStatus(val notifyed: Boolean) {

    /**
     * 默认值，但是还没通知确认
     */
    DefaultUnNotify(false),
    /**
     * 默认值
     */
    Default(true),
    /**
     * Xml计算，但是还没通知确认
     */
    XmlWrapUnNotify(false),
    /**
     * Xml计算
     */
    XmlWrap(true),
    /**
     * Xml 的view 指定，但是还没通知确认
     */
    XmlExactUnNotify(false),
    /**
     * Xml 的view 指定
     */
    XmlExact(true),
    /**
     * Xml 的layout 中指定，但是还没通知确认
     */
    XmlLayoutUnNotify(false),
    /**
     * Xml 的layout 中指定
     */
    XmlLayout(true),
    /**
     * 代码指定，但是还没通知确认
     */
    CodeExactUnNotify(false),
    /**
     * 代码指定
     */
    CodeExact(true),
    /**
     * 锁死，但是还没通知确认
     */
    DeadLockUnNotify(false),
    /**
     * 默锁死
     */
    DeadLock(true);

    /**
     * 转换为未通知状态
     */
    fun unNotify(): DimensionStatus {
        if (notifyed) {
            val prev = values()[ordinal - 1]
            return if (!prev.notifyed) {
                prev
            } else DefaultUnNotify
        }
        return this
    }

    /**
     * 转换为通知状态
     */
    fun notifyed(): DimensionStatus {
        return if (!notifyed) {
            values()[ordinal + 1]
        } else this
    }

    /**
     * 小于等于
     */
    fun canReplaceWith(status: DimensionStatus): Boolean {
        return ordinal < status.ordinal || (!notifyed || CodeExact == this) && ordinal == status.ordinal
    }

    /**
     * 大于等于
     */
    fun gteReplaceWith(status: DimensionStatus): Boolean {
        return ordinal >= status.ordinal
    }
}