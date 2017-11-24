package com.eelly.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.eelly.R
import com.eelly.bean.MovieBean
import com.eelly.holder.MoviesHolder

/**
 * @author Vurtne on 20-Nov-17.
 */
class MoviesAdapter(var mMovies:List<MovieBean>,var context: Context) : Adapter<MoviesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MoviesHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movies, parent, false)
        return MoviesHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mMovies.isEmpty()) 0 else mMovies.size
    }

    override fun onBindViewHolder(holder: MoviesHolder, position: Int) {
        val bean : MovieBean = mMovies[position]
        holder.mNameTv.text = bean.title
        holder.mRateRb.setStar(bean.rating.stars)

        var strName = ""
        bean.casts.forEach { actor ->
            strName += actor.name + " / "
        }
        if (strName.endsWith(" / ")){
            strName = strName.substring(0,strName.length-2)
        }
        holder.mActorTv.text = context.getString(R.string.text_actor,strName)

        var strdiractor = ""
        bean.directors.forEach { actor ->
            strdiractor += actor.name + " / "
        }
        if (strdiractor.endsWith(" / ")){
            strdiractor = strdiractor.substring(0,strdiractor.length-2)
        }
        holder.mDirectorTv.text = context.getString(R.string.text_director,strdiractor)

        Glide.with(context).load(bean.images.large).
                into(holder.mBannerIv)


    }



}
