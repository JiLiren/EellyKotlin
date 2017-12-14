package com.eelly.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.eelly.R
import com.eelly.widget.XRatingBar

/**
 * @author Vurtne on 20-Nov-17.
 *
 */
class MoviesHolder(rootView :View) : RecyclerView.ViewHolder(rootView){
    var mBannerIv : ImageView = rootView.findViewById<ImageView>(R.id.mPosterIv)
    var mNameTv : TextView = rootView.findViewById<TextView>(R.id.mNameTv)
    var mRateRb : XRatingBar = rootView.findViewById<XRatingBar>(R.id.mRateRb)
    var mActorTv : TextView = rootView.findViewById<TextView>(R.id.mActorTv)
    var mDirectorTv : TextView = rootView.findViewById<TextView>(R.id.mDirectorTv)
    var mClickLayout : LinearLayout = rootView.findViewById<LinearLayout>(R.id.mClickLayout)
}