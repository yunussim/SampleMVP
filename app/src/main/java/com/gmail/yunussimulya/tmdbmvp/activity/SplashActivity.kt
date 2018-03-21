package com.gmail.yunussimulya.tmdbmvp.activity

import android.os.Bundle
import com.gmail.yunussimulya.tmdbmvp.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun stringFromJNI(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Example of a call to a native method
        sample_text.text = stringFromJNI()
    }

}
