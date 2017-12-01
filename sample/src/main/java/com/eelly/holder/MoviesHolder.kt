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

    var mBannerIv : ImageView = rootView.findViewById(R.id.mPosterIv) as ImageView
    var mNameTv : TextView = rootView.findViewById(R.id.mNameTv) as TextView
    var mRateRb : XRatingBar = rootView.findViewById(R.id.mRateRb) as XRatingBar
    var mActorTv : TextView = rootView.findViewById(R.id.mActorTv) as TextView
    var mDirectorTv : TextView = rootView.findViewById(R.id.mDirectorTv) as TextView
    var mClickLayout : LinearLayout = rootView.findViewById(R.id.mClickLayout) as LinearLayout

}