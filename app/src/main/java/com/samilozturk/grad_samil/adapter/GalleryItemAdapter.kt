package com.samilozturk.grad_samil.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.samilozturk.grad_samil.data.GalleryItem
import com.samilozturk.grad_samil.databinding.ItemGalleryItemBinding

class GalleryItemAdapter(
    private val galleryItems: List<GalleryItem>,
    private val onGalleryItemClick: (GalleryItem) -> Unit,
) : RecyclerView.Adapter<GalleryItemAdapter.GalleryItemViewHolder>() {

    inner class GalleryItemViewHolder(val binding: ItemGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(galleryItem: GalleryItem) {
            binding.apply {
                textViewTitle.text = galleryItem.title
                textViewAuthor.text =
                    "${galleryItem.author.firstName} ${galleryItem.author.lastName}"
                Glide.with(root).load(galleryItem.thumbnailUrl).into(imageView)
                imageViewPlay.isVisible = galleryItem.isVideo
                root.setOnClickListener {
                    onGalleryItemClick(galleryItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        val binding = ItemGalleryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GalleryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        val galleryItem = galleryItems[position]
        holder.bindTo(galleryItem)
    }

    override fun getItemCount() = galleryItems.size


}