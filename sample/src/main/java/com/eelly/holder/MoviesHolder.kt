package com.eelly.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.eelly.R

/**
 * @author Vurtne on 20-Nov-17.
 *
 */
class MoviesHolder(rootView :View) : RecyclerView.ViewHolder(rootView){

    var mBannerIv : ImageView = rootView.findViewById(R.id.mPosterIv) as ImageView
    var mNameTv : TextView = rootView.findViewById(R.id.mNameTv) as TextView
    var mRateRb : RatingBar = rootView.findViewById(R.id.mRateRb) as RatingBar

}