package com.eelly.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eelly.R
import com.eelly.bean.MoviesEntity
import com.eelly.holder.MoviesHolder

/**
 * @author Vurtne on 20-Nov-17.
 */
class MoviesAdapter(var beans: List<MoviesEntity>, var context: Context) : Adapter<MoviesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MoviesHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_staggred, parent, false)
        return MoviesHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
//        return if (beans.isEmpty()) 0 else beans.size
    }

    override fun onBindViewHolder(holder: MoviesHolder?, position: Int) {
//        beans.get(position)
    }



}
