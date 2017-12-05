package com.eelly.core.widget.banner

/**
 * @author Vurtne on 5-Dec-17.
 */
data class BannerEntity(var imageUrl:String,var title:String,var average :Float){


    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null || javaClass != obj.javaClass) return false

        val that = obj as BannerEntity?


        if (if (imageUrl != null) !imageUrl.equals(that!!.imageUrl) else that!!.imageUrl != null) return false
        return if (title != null) title.equals(that.title) else that.title == null

    }


    override fun hashCode(): Int {
        var result = if (imageUrl != null) imageUrl.hashCode() else 0
        result = 31 * result + if (title != null) title.hashCode() else 0
        return result
    }
}