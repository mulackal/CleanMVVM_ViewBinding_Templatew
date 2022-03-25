package com.vip.cleantemplate.presentation.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vip.cleantemplate.R
import com.vip.cleantemplate.databinding.ItemLayoutBinding
import com.vip.cleantemplate.databinding.UserItemBinding
import com.vip.cleantemplate.domain.model.Player

class PagingAdapter : PagingDataAdapter<Player, PagingAdapter.MainHolder>(DataComparator) {

    class MainHolder( val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.binding.tvName.text = getItem(position)?.first_name
        holder.binding.tvEmail.text = getItem(position)?.last_name

        val requestOptions = RequestOptions()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
            .fitCenter()
            .circleCrop()

        Glide.with(holder.itemView.context)
            .load("https://bellard.org/bpg/lena30.jpg")
            .apply(requestOptions)
            .into(holder.binding.ivUser)

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context,"- ${getItem(position)?.first_name} -",Toast.LENGTH_SHORT).show()
        }
    }

    object DataComparator : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

    }
}