package com.keeghan.firenote

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.keeghan.firenote.databinding.ActivitySignInBinding
import com.keeghan.firenote.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            if (validateSignUpForm()) {
                auth.createUserWithEmailAndPassword(
                    binding.signupEmail.text.toString().trim(),
                    binding.signupPassword.text.toString().trim()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG123", "createUserWithEmail:success")
                        // val user = auth.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG120", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.sendToSignin.setOnClickListener {
            val intent = Intent(this@SignUpActivity,
                SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI() {

    }

    private fun validateSignUpForm(): Boolean {
        var isValid = true
        val email: String = binding.signupEmail.text.toString().trim()
        val password: String = binding.signupPassword.text.toString().trim()
        val confirmPassword: String = binding.confirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.signupEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signupEmail.error = "Invalid email address"
            isValid = false
        }
        if (TextUtils.isEmpty(password)) {
            binding.signupPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.signupPassword.error = "Password must be at least 6 characters long"
            isValid = false
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPassword.error = "Confirm password is required"
            isValid = false
        } else if (confirmPassword != password) {
            binding.confirmPassword.error = "Passwords do not match"
            isValid = false
        }
        return isValid
    }
}