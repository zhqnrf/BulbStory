package com.example.bulbstory.app.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.bulbstory.app.R
import com.example.bulbstory.app.data.DataRepository
import com.example.bulbstory.app.data.remote.api.ApiClient
import com.example.bulbstory.app.databinding.ActivityRegisterBinding
import com.example.bulbstory.app.util.Message
import com.example.bulbstory.app.util.NetworkResult
import com.example.bulbstory.app.util.ViewModelFactory
import com.example.bulbstory.app.view.login.LoginActivity
import com.example.bulbstory.app.view.login.LoginAndRegisterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: LoginAndRegisterViewModel
    private var registerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))[LoginAndRegisterViewModel::class.java]
        setData()

        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        playAnimation()
    }

    @Suppress("DEPRECATION")
    private fun setData() {
        binding.apply {
            signupButton.setOnClickListener {
                val name = binding.nameEditText.text.toString().trim()
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                    Message.setMessage(this@RegisterActivity, getString(R.string.warning_input))
                } else {
                    showLoading(true)
                    lifecycle.coroutineScope.launchWhenResumed {
                        if(registerJob.isActive) registerJob.cancel()
                        registerJob = launch {
                            viewModel.register(name, email, password).collect { result ->
                                when(result) {
                                    is NetworkResult.Success -> {
                                        showLoading(false)
                                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                        Message.setMessage(this@RegisterActivity, getString(R.string.success_register))
                                        finish()
                                    }
                                    is NetworkResult.Loading -> {
                                        showLoading(true)
                                    }
                                    is NetworkResult.Error -> {
                                        Message.setMessage(this@RegisterActivity, resources.getString(R.string.error_register))
                                        showLoading(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val haveAcc = ObjectAnimator.ofFloat(binding.tvIsHaveAccount, View.ALPHA, 1f).setDuration(100)
        val loginButton = ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(haveAcc, loginButton)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                nameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                signup,
                together
            )
            startDelay = 100
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}