package com.ehayvan.app.modules.veterinariandashboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ehayvan.app.R
import com.ehayvan.app.databinding.RowListtitleBinding
import com.ehayvan.app.modules.veterinariandashboard.`data`.model.ListtitleRowModel
import kotlin.Int
import kotlin.collections.List

class ListtitleAdapter(
  var list: List<ListtitleRowModel>
) : RecyclerView.Adapter<ListtitleAdapter.RowListtitleVH>() {
  private var clickListener: OnItemClickListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowListtitleVH {
    val view=LayoutInflater.from(parent.context).inflate(R.layout.row_listtitle,parent,false)
    return RowListtitleVH(view)
  }

  override fun onBindViewHolder(holder: RowListtitleVH, position: Int) {
    val listtitleRowModel = ListtitleRowModel()
    // TODO uncomment following line after integration with data source
    // val listtitleRowModel = list[position]
    holder.binding.listtitleRowModel = listtitleRowModel
  }

  override fun getItemCount(): Int = 3
  // TODO uncomment following line after integration with data source
  // return list.size

  public fun updateData(newData: List<ListtitleRowModel>) {
    list = newData
    notifyDataSetChanged()
  }

  fun setOnItemClickListener(clickListener: OnItemClickListener) {
    this.clickListener = clickListener
  }

  interface OnItemClickListener {
    fun onItemClick(
      view: View,
      position: Int,
      item: ListtitleRowModel
    ) {
    }
  }

  inner class RowListtitleVH(
    view: View
  ) : RecyclerView.ViewHolder(view) {
    val binding: RowListtitleBinding = RowListtitleBinding.bind(itemView)
  }
}
