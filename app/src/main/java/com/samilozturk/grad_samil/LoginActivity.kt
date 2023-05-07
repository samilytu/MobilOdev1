package com.samilozturk.grad_samil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.samilozturk.grad_samil.data.LoginCredentials
import com.samilozturk.grad_samil.databinding.ActivityLoginBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.remote.DbManager
import com.samilozturk.grad_samil.remote.LocalStorageManager
import com.samilozturk.grad_samil.util.snackbar
import com.samilozturk.grad_samil.util.toast
import com.samilozturk.grad_samil.validator.LoginValidator
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var validator: LoginValidator
    private lateinit var authenticator: Authenticator
    private lateinit var dbManager: DbManager
    private lateinit var localStorageManager: LocalStorageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validator = LoginValidator()
        authenticator = Authenticator()
        dbManager = DbManager()
        localStorageManager = LocalStorageManager(this)

        binding.buttonLogin.setOnClickListener {
            onClickButtonLogin()
        }

        binding.textViewSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.textViewResetPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onClickButtonLogin() {
        try {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val credentials = LoginCredentials(email, password)
            validator.validate(credentials)
            // login to firebase
            onLoginValidationSuccess(credentials)
        } catch (e: Exception) {
            // show error
            snackbar(e.message.toString(), isError = true)
        }
    }

    private fun onLoginValidationSuccess(credentials: LoginCredentials) {
        lifecycleScope.launch {
            binding.apply {
                buttonLogin.isEnabled = false
                buttonLogin.text = ""
                progressBar.show()
            }
            try {
                login(credentials)
            } catch (e: Exception) {
                e.printStackTrace()
                snackbar(e.message.toString(), isError = true)
            }
            binding.apply {
                buttonLogin.isEnabled = true
                buttonLogin.text = getString(R.string.login)
                progressBar.hide()
            }
        }
    }

    private suspend fun login(credentials: LoginCredentials) {
        val firebaseUser = authenticator.login(credentials.email, credentials.password)
        val user = dbManager.getUser(firebaseUser.uid)
        // save user to shared preferences
        localStorageManager.saveUser(user)
        toast("Başarıyla giriş yapıldı.")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}