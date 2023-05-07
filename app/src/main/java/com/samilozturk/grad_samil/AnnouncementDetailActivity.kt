package com.samilozturk.grad_samil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.samilozturk.grad_samil.data.Announcement
import com.samilozturk.grad_samil.databinding.ActivityAnnouncementDetailBinding

class AnnouncementDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnnouncementDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnouncementDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val announcement = intent.getParcelableExtra<Announcement>("announcement")!!

        supportActionBar?.title = announcement.title
        binding.textViewContent.text = announcement.content
        binding.textViewAuthor.text = "${announcement.author.firstName} ${announcement.author.lastName}"
        Glide.with(this).load(announcement.imageUrl).into(binding.imageView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}