package com.samilozturk.grad_samil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.samilozturk.grad_samil.databinding.ActivityHomeBinding
import com.samilozturk.grad_samil.fragment.AnnouncementsFragment
import com.samilozturk.grad_samil.fragment.GalleryFragment
import com.samilozturk.grad_samil.fragment.GradsFragment
import com.samilozturk.grad_samil.remote.LocalStorageManager
import com.samilozturk.grad_samil.util.toast

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var localStorageManager: LocalStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localStorageManager = LocalStorageManager(this)

        // get user from intent
        val user = localStorageManager.getUser()!!


        // change actionbar title
        // supportActionBar?.title = "> ${user.firstName} ${user.lastName}"

        // configure bottom navigation
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_grads -> {
                    replaceFragment(GradsFragment())
                    true
                }
                R.id.nav_announcements -> {
                    replaceFragment(AnnouncementsFragment())
                    true
                }
                R.id.nav_gallery -> {
                    replaceFragment(GalleryFragment())
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("isEditable", true)
                    intent.putExtra("user", user)
                    startActivity(intent)
                    false
                }
                else -> error("Unknown navigation item")
            }
        }

        // set default fragment
        binding.navView.selectedItemId = R.id.nav_grads
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
        }
    }
}