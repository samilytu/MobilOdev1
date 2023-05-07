package com.samilozturk.grad_samil.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.samilozturk.grad_samil.data.Announcement
import com.samilozturk.grad_samil.databinding.ItemAnnouncementBinding

class AnnouncementAdapter(
    private val announcements: List<Announcement>,
    private val onAnnouncementClick: (Announcement) -> Unit,
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    inner class AnnouncementViewHolder(val binding: ItemAnnouncementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(announcement: Announcement) {
            binding.apply {
                textViewTitle.text = announcement.title
                textViewAuthor.text =
                    "${announcement.author.firstName} ${announcement.author.lastName}"
                Glide.with(root).load(announcement.imageUrl).into(imageView)
                root.setOnClickListener {
                    onAnnouncementClick(announcement)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = ItemAnnouncementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.bindTo(announcement)
    }

    override fun getItemCount() = announcements.size


}