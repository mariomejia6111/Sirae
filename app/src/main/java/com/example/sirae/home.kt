package com.example.sirae

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (supportActionBar != null) supportActionBar?.hide()
    }
}