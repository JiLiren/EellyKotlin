package com.eelly.core.widget.banner

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.eelly.core.R
import kotlinx.android.synthetic.main.item_banner.view.*

/**
 * @author Vurtne on 5-Dec-17.
 */
class BannerLayout(context:Context,attrs: AttributeSet?): FrameLayout(context, attrs){

    init{
        View.inflate(getContext(), R.layout.item_banner, this)
    }

    fun setEntity(entity:BannerEntity) {
        Glide.with(context).load(entity.imageUrl).into(mBannerIv)
        if (!TextUtils.isEmpty(entity.title)) {
            mTitleTv.text = entity.title
        } else {
            mTitleTv.visibility = GONE
        }
    }
}