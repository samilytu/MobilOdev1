package com.samilozturk.grad_samil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.samilozturk.grad_samil.data.SendVerificationEmailCredentials
import com.samilozturk.grad_samil.databinding.ActivityResetPasswordBinding
import com.samilozturk.grad_samil.remote.Authenticator
import com.samilozturk.grad_samil.util.snackbar
import com.samilozturk.grad_samil.validator.SendVerificationEmailValidator
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val authenticator = Authenticator()
    private val sendVerificationEmailValidator = SendVerificationEmailValidator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonResetPassword.setOnClickListener {
            try {
                val email = binding.editTextEmail.text.toString()
                val credentials = SendVerificationEmailCredentials(email)
                sendVerificationEmailValidator.validate(credentials)
                sendVerificationCode(credentials)
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            }
        }

    }

    private fun sendVerificationCode(credentials: SendVerificationEmailCredentials) {
        lifecycleScope.launch {
            binding.apply {
                buttonResetPassword.isEnabled = false
                buttonResetPassword.text = ""
                progressBar.show()
            }
            try {
                authenticator.sendResetPasswordEmail(credentials.email)
                binding.buttonResetPassword.text = getString(R.string.send_verification_email)
                snackbar("Doğrulama kodu gönderildi. Lütfen e-postanızı kontrol edin.")
            } catch (e: Exception) {
                e.printStackTrace()
                binding.buttonResetPassword.text = getString(R.string.send_verification_email)
                snackbar(e.message.toString(), isError = true)
            }
            binding.apply {
                buttonResetPassword.isEnabled = true
                progressBar.hide()
            }
        }
    }


}