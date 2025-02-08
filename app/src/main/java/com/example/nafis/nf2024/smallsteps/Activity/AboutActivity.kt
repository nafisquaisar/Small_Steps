package com.example.nafis.nf2024.smallsteps.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.databinding.ActivityAboutBinding
import com.example.nafis.nf2024.smallsteps.databinding.ActivityHelpBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAboutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}