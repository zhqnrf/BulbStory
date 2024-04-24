package com.example.bulbstory.app.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.example.bulbstory.app.R
import com.example.bulbstory.app.util.PrefsManager
import com.example.bulbstory.app.view.home.HomeActivity
import com.example.bulbstory.app.view.start.WelcomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setView()
        val prefsManager = PrefsManager(this)
        lifecycleScope.launch {
            delay(3000)
            val intent = if(prefsManager.exampleBoolean) {
                Intent(this@SplashscreenActivity, HomeActivity::class.java)
            } else {
                Intent(this@SplashscreenActivity, WelcomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}