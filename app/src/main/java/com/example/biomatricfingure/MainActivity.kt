@file:Suppress("DEPRECATION")

package com.example.biomatricfingure
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.biomatricfingure.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricInfo: BiometricPrompt.PromptInfo
    private lateinit var biometricManager: BiometricManager
    private lateinit var executor: Executor

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // ✅ Initialize BiometricManager
        biometricManager = BiometricManager.from(applicationContext)

        // ✅ Check if Face Unlock is available
        val isFaceAvailable = isFaceUnlockAvailable()
        if (isFaceAvailable) {
            Toast.makeText(this, "Face Unlock is supported on this device!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Face Unlock is not available!", Toast.LENGTH_SHORT).show()
        }

        // ✅ Check overall biometric authentication support
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Toast.makeText(this, "Biometric authentication (Face/Fingerprint) is available", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(this, "Biometric hardware is currently unavailable", Toast.LENGTH_SHORT).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(this, "No biometric data enrolled (Enroll Face/Fingerprint in settings)", Toast.LENGTH_SHORT).show()
        }

        // ✅ Initialize Executor
        executor = ContextCompat.getMainExecutor(this)

        // ✅ Initialize BiometricPrompt
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                binding.text.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity, "Authentication Succeeded!", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                binding.text.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Authentication Error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                binding.text.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Authentication Failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        })

        // ✅ Configure BiometricPrompt Info (Supports Face, Fingerprint, and PIN)
        biometricInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use Face, Fingerprint, or Device Lock to unlock")
            .apply {
                // Only set the negative button text if device credential is not allowed
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    setNegativeButtonText("Cancel")
                }
            }
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        // ✅ Start Authentication Automatically
        biometricPrompt.authenticate(biometricInfo)

        // ✅ Trigger Authentication on Button Click
        binding.useFingerId.setOnClickListener {
            biometricPrompt.authenticate(biometricInfo)
        }
    }

    // ✅ Check If Face Unlock Is Available
    private fun isFaceUnlockAvailable(): Boolean {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }
}
