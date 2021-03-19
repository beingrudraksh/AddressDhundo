package com.example.android.addressdhundo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.transition.Explode
import android.view.Window
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeader: TextView
    private lateinit var tvDesc: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window){
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            exitTransition = Explode()
        }

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        tvHeader = findViewById(R.id.tvHeader)
        tvHeader.animate().alpha(1f).duration = 2500

        tvDesc = findViewById(R.id.tvDesc)
        tvDesc.animate().alpha(0.8f).duration = 2500

        Handler().postDelayed(Runnable {
            var intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }, 2500)
    }
}