package com.samilozturk.grad_samil.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.samilozturk.grad_samil.ProfileActivity
import com.samilozturk.grad_samil.adapter.GradsAdapter
import com.samilozturk.grad_samil.data.User
import com.samilozturk.grad_samil.databinding.FragmentGradsBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.remote.DbManager
import com.samilozturk.grad_samil.util.snackbar
import com.samilozturk.grad_samil.util.toast
import kotlinx.coroutines.launch

class GradsFragment : Fragment() {

    private lateinit var binding: FragmentGradsBinding
    private val dbManager = DbManager()
    private val authenticator = Authenticator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGradsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUsers()
    }

    private fun fetchUsers() {
        lifecycleScope.launch {
            binding.progressBar.show()
            try {
                val users = dbManager.getAllUsers(authenticator.getCurrentUser()!!.uid)
                listUsers(users)
            } catch (e: Exception) {
                if (isAdded) {
                    requireActivity().snackbar(e.message.toString(), isError = true)
                }
            }
            binding.progressBar.hide()
        }
    }

    private fun listUsers(items: List<User>) {
        val adapter = GradsAdapter(items) { user ->
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
    }

}