package com.eelly.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eelly.R
import com.eelly.bean.StaggerBean
import com.eelly.holder.StaggredHolder

/**
 * @author Vurtne on 20-Nov-17.
 */
class StaggerdAdapter(var beans: List<StaggerBean>, var context: Context) : Adapter<StaggredHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StaggredHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_staggred, parent, false)
        return StaggredHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
//        return if (beans.isEmpty()) 0 else beans.size
    }

    override fun onBindViewHolder(holder: StaggredHolder?, position: Int) {
//        beans.get(position)
    }



}
