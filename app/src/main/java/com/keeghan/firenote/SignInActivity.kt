package com.keeghan.firenote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
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
            closeKeyboard(baseContext, it)
            if (validateSignInForm()) {
                auth.signInWithEmailAndPassword(
                    binding.signinEmail.text.toString().trim(),
                    binding.signinPassword.text.toString().trim()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG122", "signInWithEmail:success")
                        // val user = auth.currentUser
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG124", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            task.exception?.localizedMessage ?: "Authentication Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.recoveryBtn.setOnClickListener {
            val intent = Intent(this@SignInActivity, RestPasswordActivity::class.java)
            startActivity(intent)
        }

        //Clear password error status when typing password
        binding.signinPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                    binding.signinPasswordLayout.error = null
                    binding.signinPasswordLayout.isErrorEnabled = false
            }
        })
    }

    //Validate from before calling Firebase Auth
    //Enable error message on TextFields and disable if there isn't any (empty space)
    private fun validateSignInForm(): Boolean {
        val email: String = binding.signinEmail.text.toString().trim()
        val password: String = binding.signinPassword.text.toString().trim()
        binding.signinPasswordLayout.isErrorEnabled = false

        if (TextUtils.isEmpty(email)) {
            binding.signinEmail.error = "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signinEmail.error = "Invalid email address"
        } else if (TextUtils.isEmpty(password)) {
            binding.signinPasswordLayout.error = "Password is required"
        } else if (password.length < 6) {
            binding.signinPasswordLayout.error = "Password must be at least 6 characters long"
        }

        return binding.signinEmail.error == null && binding.signinPasswordLayout.error == null
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

    private fun closeKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}