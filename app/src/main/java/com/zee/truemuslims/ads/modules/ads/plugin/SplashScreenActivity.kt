package com.zee.truemuslims.ads.modules.ads.plugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zee.truemuslims.ads.modules.TrueZAppOpenAd
import com.zee.truemuslims.ads.modules.database.TrueZSPRepository

class SplashScreenActivity : AppCompatActivity() {
    var TAG = "SplashScreenActivityy"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "onCreate: ${!TrueZSPRepository.getOpenAdValue(this)}")
            if (!TrueZSPRepository.getOpenAdValue(this)) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }, 4500)
    }
}