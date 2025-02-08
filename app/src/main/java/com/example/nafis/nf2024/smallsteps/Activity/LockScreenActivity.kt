package com.example.nafis.nf2024.smallsteps.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.databinding.ActivityLockScreenBinding

class LockScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLockScreenBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isSettingLock: Boolean = false
    private var isDisable: Boolean = false
    private lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        isSettingLock = intent.getBooleanExtra("SetLock", false)
        isDisable = intent.getBooleanExtra("disable", false)
        sharedPreferences = getSharedPreferences("AppLockPrefs", Context.MODE_PRIVATE)

        if(isSettingLock || isDisable){
            binding.tvTitle.text="Enter Old PIN"
        }else{
            binding.tvTitle.text="Enter PIN"
        }

        // ========== find and set next pin block=================
        val pinFields = listOf(binding.etPin1, binding.etPin2, binding.etPin3, binding.etPin4)
        setupAutoMove(pinFields)


        // =================Set up the biometric authentication==================
        setupBiometricAuthentication()
//        =============== click biometric btn====================
        binding.tvFingerprint.setOnClickListener {
            if (canAuthenticate()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                Toast.makeText(this, "Fingerprint authentication is not available", Toast.LENGTH_SHORT).show()
            }
        }

        // =================== Handle OK button click =================================
        binding.btnOk.setOnClickListener {
            val password = pinFields.joinToString("") { it.text.toString() }

            val savedPIN = sharedPreferences.getString("LOCK_PIN", "")

            if (password.length == 4) {
                if (password == savedPIN) {
                    Toast.makeText(this, "PIN Verified!", Toast.LENGTH_SHORT).show()

                    val intent = when {
                        isSettingLock -> {
                            // If setting the lock, go to SetLockActivity with the verify flag
                            Intent(this, SetLockActivity::class.java).apply {
                                putExtra("verify", true)
                            }
                        }
                        isDisable -> {
                            // If disabling the lock, remove PIN from SharedPreferences
                            val editor = sharedPreferences.edit()
                            editor.remove("LOCK_PIN")  // Remove the stored PIN
                            editor.apply()

                            // Show Toast and redirect to MainActivity
                            Toast.makeText(this, "Disabled Pin Successfully", Toast.LENGTH_SHORT).show()
                            Intent(this, MainActivity::class.java)
                        }
                        else -> {
                            // Default case: Go to MainActivity
                            Intent(this, MainActivity::class.java)
                        }
                    }

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect PIN. Try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show()
            }

        }



        //========Check box password show hide ==================
        binding.lockShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
              showPassword(pinFields)
            } else {
                hidePassword(pinFields)
            }
        }
    }


    //=====================  Hide password as dots (•)==================================
    private fun hidePassword(pinFields: List<EditText>) {
        pinFields.forEach {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            it.transformationMethod = PasswordTransformationMethod.getInstance() // Ensure hiding
            it.setSelection(it.text.length) // Keep cursor at the end
        }
    }

    // ================= Show password as plain text =========================
    private fun showPassword(pinFields: List<EditText>) {
        pinFields.forEach {
            it.inputType = InputType.TYPE_CLASS_TEXT // Show plain text
            it.transformationMethod = null // Remove password transformation
            it.setSelection(it.text.length) // Keep cursor at the end
        }
    }





    private fun setupAutoMove(pinFields: List<EditText>) {
        for (i in pinFields.indices) {
            pinFields[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Move to the next field when a digit is entered
                    if (count == 1 && i < pinFields.size - 1) {
                        pinFields[i + 1].requestFocus()
                    } else if (count == 0 && i > 0) {
                        pinFields[i - 1].requestFocus()
                    }
                    Log.d("PinInput", "Text changed in field ${i+1}: ${s.toString()}")

                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }



    // ================Function to check biometric availability in the Phone========================
    private fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    //================== Initialize biometric prompt and prompt info==========================
    private fun setupBiometricAuthentication() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                val intent = when {
                    isSettingLock -> {
                        // If setting the lock, go to SetLockActivity with the verify flag
                        Intent(this@LockScreenActivity, SetLockActivity::class.java).apply {
                            putExtra("verify", true)
                        }
                    }
                    isDisable -> {
                        // If disabling the lock, remove PIN from SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.remove("LOCK_PIN")  // Remove the stored PIN
                        editor.apply()

                        // Show Toast and redirect to MainActivity
                        Toast.makeText(this@LockScreenActivity, "Disabled Pin Successfully", Toast.LENGTH_SHORT).show()
                        Intent(this@LockScreenActivity, MainActivity::class.java)
                    }
                    else -> {
                        // Default case: Go to MainActivity
                        Intent(this@LockScreenActivity, MainActivity::class.java)
                    }
                }

                startActivity(intent)
                finish()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })

        // Configure the prompt info
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your fingerprint to authenticate")
            .setNegativeButtonText("Cancel")
            .build()
    }
}
