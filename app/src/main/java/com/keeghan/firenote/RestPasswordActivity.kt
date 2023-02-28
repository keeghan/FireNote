package com.keeghan.firenote

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.keeghan.firenote.databinding.ActivityRestPasswordBinding

class RestPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.resetButton.setOnClickListener {
            if (validateResetEmail()) {
                auth.sendPasswordResetEmail(
                    binding.emailReset.text.toString().trim(),
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Reset Link Send", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }//onCreate


    private fun validateResetEmail(): Boolean {
        var isValid = true
        val email: String = binding.emailReset.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.emailReset.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailReset.error = "Invalid email address"
            isValid = false
        }
        return isValid
    }
}