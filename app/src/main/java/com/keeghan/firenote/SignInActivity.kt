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


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)


        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.sendToSignUp.setOnClickListener {
            val intent = Intent(
                this@SignInActivity,
                SignUpActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        binding.signinButton.setOnClickListener {
            if (validateSignInForm()) {
                auth.signInWithEmailAndPassword(
                    binding.signinEmail.text.toString().trim(),
                    binding.signinPassword.text.toString().trim()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG122", "signInWithEmail:success")
                            // val user = auth.currentUser
                            //   updateUI(user)
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG124", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            //   updateUI(null)
                        }
                    }
            }
        }
    }

    private fun validateSignInForm(): Boolean {
        var isValid = true
        val email: String = binding.signinEmail.text.toString().trim()
        val password: String = binding.signinPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.signinEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signinEmail.error = "Invalid email address"
            isValid = false
        }
        if (TextUtils.isEmpty(password)) {
            binding.signinPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.signinPassword.error = "Password must be at least 6 characters long"
            isValid = false
        }
        return isValid
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}