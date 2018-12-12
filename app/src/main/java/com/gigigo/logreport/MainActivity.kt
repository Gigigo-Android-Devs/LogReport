package com.gigigo.logreport

import android.os.Bundle
import com.gigigo.logger.Logger

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger(this)
    }
}
