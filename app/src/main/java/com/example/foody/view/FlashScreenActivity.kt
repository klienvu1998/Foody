package com.example.foody.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.example.foody.R

class FlashScreenActivity : AppCompatActivity() {

    lateinit var txtVersion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_flashscreen)
        mapping()
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        var packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        txtVersion.text = getString(R.string.version)+": "+packageInfo.versionName
    }

    private fun mapping() {
        txtVersion = findViewById(R.id.txt_version_flashscreen)
    }

    override fun onResume() {
        super.onResume()
        val r = Runnable {
            val intent = Intent(this@FlashScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        Handler().postDelayed(r, 2000)
    }
}
