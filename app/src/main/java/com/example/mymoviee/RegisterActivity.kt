package com.example.mymoviee

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.mymoviee.databinding.ActivityRegisterBinding
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Full Name
        val nameStream = RxTextView.textChanges(binding.edtRegisterFullName)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe{
            nameAlert(it)
        }

        // Email
        val emailStream = RxTextView.textChanges(binding.edtRegisterEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe{
            emailAlert(it)
        }

        // User Name
        val userNameStream = RxTextView.textChanges(binding.edtRegisterAccount)
            .skipInitialValue()
            .map { username ->
                username.length < 6
            }
        userNameStream.subscribe{
            tooSmall(it, "Username")
        }

        // Password
        val passwordStream = RxTextView.textChanges(binding.edtRegisterPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        passwordStream.subscribe{
            tooSmall(it, "Password")
        }

        // Confirm Password
        val confirmPasswordStream = io.reactivex.Observable.merge(
            RxTextView.textChanges(binding.edtRegisterPassword)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.edtRegisterConfirmPassword.toString()
                },
            RxTextView.textChanges(binding.edtRegisterConfirmPassword)
                .skipInitialValue()
                .map { confirmPassword ->
                    confirmPassword.toString() != binding.edtRegisterPassword.toString()
                })
        confirmPasswordStream.subscribe{
            confirmPasswordAlert(it)
        }

        // Button
        val invalidStream = io.reactivex.Observable.combineLatest(
            nameStream,
            emailStream,
            userNameStream,
            passwordStream,
            confirmPasswordStream,
        { nameInvalid: Boolean, emailInvalid: Boolean, usernameInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ->
            !nameInvalid && !emailInvalid && !usernameInvalid && !passwordInvalid && !passwordConfirmInvalid
        })
        invalidStream.subscribe { isValid ->
            if (isValid) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_button)
            }
            else {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.grey)
            }
        }


        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.haveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun nameAlert(isNotValid: Boolean) {
        binding.edtRegisterFullName.error = if (isNotValid) "Vui lòng nhập Họ và tên!" else null
    }

    private fun tooSmall(isNotValid: Boolean, text: String) {
        if(text == "Username") {
            binding.edtRegisterAccount.error = if (isNotValid) "Tên tài khoản không hợp lệ!" else null
        }
        else if(text == "Password") {
            binding.edtRegisterPassword.error = if (isNotValid) "Mật khẩu không hợp lệ!" else null
        }
    }

    private fun emailAlert(isNotValid: Boolean) {
        binding.edtRegisterEmail.error = if (isNotValid) "Email không hợp lệ" else null
    }

    private fun confirmPasswordAlert(isNotValid: Boolean) {
        binding.edtRegisterEmail.error = if (isNotValid) "Lỗi xác nhận mật khẩu" else null
    }
}