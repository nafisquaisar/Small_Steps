package com.example.nafis.nf2024.smallsteps.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.R

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE)
        val savedPIN = sharedPreferences.getString("LOCK_PIN", null)

        // Delay to show splash screen for a few seconds
        Handler().postDelayed({
            if (savedPIN.isNullOrEmpty()) {
                // No password set, go straight to MainActivity or a setup screen
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Launch lock screen
                startActivity(Intent(this, LockScreenActivity::class.java))
            }
            finish()
        }, 2000) // Delay for 2 seconds (2000 milliseconds)
    }
}
