package com.projects.valerian.samplemapapplication.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projects.valerian.samplemapapplication.R
import com.projects.valerian.samplemapapplication.model.InfrastructureType
import com.projects.valerian.samplemapapplication.setVisibility
import kotlinx.android.synthetic.main.item_suggestion_type.view.*

class InfrastructureTypeAdapter(val viewActionListener: ViewActionListener) : RecyclerView.Adapter<InfrastructureTypeVH>() {
    val data = InfrastructureType.values()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): InfrastructureTypeVH =
            InfrastructureTypeVH(LayoutInflater.from(p0.context).inflate(R.layout.item_suggestion_type, p0, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(vh: InfrastructureTypeVH, position: Int) =
            vh.setData(data[position], viewActionListener)
}

class InfrastructureTypeVH(view: View) : RecyclerView.ViewHolder(view) {

    fun setData(infrastructureType: InfrastructureType, viewActionListener: ViewActionListener) =
            itemView.run {
                isSelected = false
                checked_view.setVisibility(isSelected)
                title.text = infrastructureType.readable
                subtitle.text = infrastructureType.description
                setOnLongClickListener {
                    isSelected = true
                    checked_view.setVisibility(isSelected)
                    viewActionListener.onAction(Action.LONG_PRESS, infrastructureType)

                }
                setOnClickListener {
                    isSelected = false
                    checked_view.setVisibility(isSelected)
                    viewActionListener.onAction(Action.CLICK, infrastructureType)
                }
            }
}