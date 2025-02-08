package com.example.nafis.nf2024.smallsteps.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.databinding.ActivitySetLockBinding

import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.example.nafis.nf2024.smallsteps.R


class SetLockActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetLockBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetLockBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE)
        val savedPIN = sharedPreferences.getString("LOCK_PIN", null)

        val isVerify = intent.getBooleanExtra("verify", false)

        Log.d("verify","${isVerify}")

        if (savedPIN != null && savedPIN.isNotEmpty() && !isVerify) {
            val intent = Intent(this, LockScreenActivity::class.java)
            intent.putExtra("SetLock", true)
            startActivity(intent)
            finish()
            return
        }

        val pinFields = listOf(binding.etPin1, binding.etPin2, binding.etPin3, binding.etPin4)

        setupAutoMove(pinFields)

        binding.btnOk.setOnClickListener {
            val enteredPIN = pinFields.joinToString("") { it.text.toString() }

            if (enteredPIN.length == 4) {
                savePIN(enteredPIN)
                Toast.makeText(this, "PIN Set Successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish() // Close the activity or navigate
            } else {
                Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show()
            }
        }





        //========show password==================
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show password (plain text) for all PIN fields
                pinFields.forEach { it.inputType = InputType.TYPE_CLASS_TEXT } // Show password as plain text
            } else {
                // Hide password (numberPassword) for all PIN fields
                pinFields.forEach {
                    it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                } // Hide password as dots (•)
            }
        }
    }



    // ================= Pin Move next back ==================
    private fun setupAutoMove(pinFields: List<EditText>) {
        for (i in pinFields.indices) {
            pinFields[i].addTextChangedListener(object : TextWatcher {
                     override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            if (count == 1 && i < pinFields.size - 1) {
                                pinFields[i + 1].requestFocus() // Move to next field
                    } else if (count == 0 && i > 0) {
                        pinFields[i - 1].requestFocus() // Move back on delete
                    }


                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }


    private fun savePIN(pin: String) {
        val editor = sharedPreferences.edit()
        editor.putString("LOCK_PIN", pin)
        editor.apply()
    }




}
