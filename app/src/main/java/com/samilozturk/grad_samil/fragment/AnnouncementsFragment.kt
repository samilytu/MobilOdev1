package com.samilozturk.grad_samil.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.samilozturk.grad_samil.AddAnnouncementActivity
import com.samilozturk.grad_samil.AnnouncementDetailActivity
import com.samilozturk.grad_samil.adapter.AnnouncementAdapter
import com.samilozturk.grad_samil.data.Announcement
import com.samilozturk.grad_samil.databinding.FragmentAnnouncementsBinding
import com.samilozturk.grad_samil.remote.DbManager
import com.samilozturk.grad_samil.util.snackbar
import kotlinx.coroutines.launch

class AnnouncementsFragment : Fragment() {

    private lateinit var binding: FragmentAnnouncementsBinding
    private val dbManager = DbManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAnnouncements()

        binding.fabAddAnnouncement.setOnClickListener {
            val intent = Intent(requireContext(), AddAnnouncementActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_ANNOUNCEMENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_ADD_ANNOUNCEMENT && resultCode == RESULT_OK) {
            fetchAnnouncements()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fetchAnnouncements() {
        lifecycleScope.launch {
            binding.progressBar.show()
            binding.recyclerView.isVisible = false
            try {
                val announcements = dbManager.getAllAnnouncements()
                listAnnouncements(announcements)
            } catch (e: Exception) {
                if (isAdded) {
                    requireActivity().snackbar(e.message.toString(), isError = true)
                }
            }
            binding.progressBar.hide()
            binding.recyclerView.isVisible = true
        }
    }

    private fun listAnnouncements(announcements: List<Announcement>) {
        if (announcements.isEmpty()) {
            binding.textViewEmptyWarning.isVisible = true
            return
        }
        val adapter = AnnouncementAdapter(announcements) {
            val intent = Intent(requireContext(), AnnouncementDetailActivity::class.java)
            intent.putExtra("announcement", it)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
    }

    companion object {
        private const val REQUEST_CODE_ADD_ANNOUNCEMENT = 1
    }

}