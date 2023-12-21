package com.pp.coworkingapp.app.new_fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pp.coworkingapp.databinding.ContentBaseBinding

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ContentBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}