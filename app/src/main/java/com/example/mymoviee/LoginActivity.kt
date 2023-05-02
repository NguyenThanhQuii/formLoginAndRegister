package com.example.mymoviee

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.mymoviee.databinding.ActivityLoginBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // User Name
        val usernameStream = RxTextView.textChanges(binding.edtEmail)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernameStream.subscribe{
            tooSmall(it, "Email/Username")
        }

        // Password
        val passwordStream = RxTextView.textChanges(binding.edtPassword)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        passwordStream.subscribe{
            tooSmall(it, "Password")
        }

        // Button
        val invalidStr = Observable.combineLatest(
            usernameStream,
            passwordStream,
         { usernameInvalid: Boolean, emailInvalid: Boolean ->
            !usernameInvalid && !emailInvalid
        })
        invalidStr.subscribe { isValid ->
            if (isValid) {
                binding.btnLoginForm.isEnabled = true
                binding.btnLoginForm.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_button)
            }
            else {
                binding.btnLoginForm.isEnabled = false
                binding.btnLoginForm.backgroundTintList = ContextCompat.getColorStateList(this, R.color.grey)
            }
        }

        binding.btnLoginForm.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.notHaveAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun tooSmall(isNotValid: Boolean, text: String) {
        if(text == "Email/Username") {
            binding.edtEmail.error = if (isNotValid) "Email hoặc tên tài khoản không hợp lệ!" else null
        }
        else if(text == "Password") {
            binding.edtPassword.error = if (isNotValid) "Mật khẩu không hợp lệ!" else null
        }
    }
}