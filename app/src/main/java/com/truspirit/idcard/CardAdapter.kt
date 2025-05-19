package com.truspirit.idcard


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.truspirit.idcard.databinding.ItemHistoryCardBinding
import com.truspirit.idcard.model.EmployeeCard

class CardAdapter(
    private val listener: OnShareClick
) : ListAdapter<EmployeeCard, CardAdapter.VH>(DIFF) {

    interface OnShareClick { fun onShare(card: EmployeeCard) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemHistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))

    inner class VH(val b: ItemHistoryCardBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(card: EmployeeCard) {
            b.tvName.text = card.name
            Glide.with(b.ivThumb).load(card.filePath).into(b.ivThumb)
            b.btnShare.setOnClickListener { listener.onShare(card) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<EmployeeCard>() {
            override fun areItemsTheSame(a: EmployeeCard, b: EmployeeCard) = a.id == b.id
            override fun areContentsTheSame(a: EmployeeCard, b: EmployeeCard) = a == b
        }
    }
}
