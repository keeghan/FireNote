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
                binding.progressIndicator.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(
                    binding.signupEmail.text.toString().trim(),
                    binding.signupPassword.text.toString().trim()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        binding.progressIndicator.visibility = View.INVISIBLE
                        // val user = auth.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.progressIndicator.visibility = View.INVISIBLE
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            task.exception?.localizedMessage ?: "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.sendToSignin.setOnClickListener {
            closeKeyboard(baseContext, it)
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Clear password error status when typing password
        binding.signupPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.signupPasswordLayout.error = null
                binding.signupPasswordLayout.isErrorEnabled = false
            }
        })

        binding.confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.confirmPasswordLayout.error = null
                binding.confirmPasswordLayout.isErrorEnabled = false
            }
        })


    }

    //Todo: Code Cleanup
    //Validate SignUp Form before calling Firebase
    //Enable error message on TextFields and disable if there isn't any (empty space)
    private fun validateSignUpForm(): Boolean {
        val email: String = binding.signupEmail.text.toString().trim()
        val password: String = binding.signupPassword.text.toString().trim()
        val confirmPassword: String = binding.confirmPassword.text.toString().trim()
        binding.signupPasswordLayout.isErrorEnabled = true
        binding.confirmPasswordLayout.isErrorEnabled = true


        if (TextUtils.isEmpty(email)) {
            binding.signupEmail.error = "Email is required"
            binding.signupPasswordLayout.isErrorEnabled = false
            binding.confirmPasswordLayout.isErrorEnabled = false
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signupEmail.error = "Invalid email address"
            binding.signupPasswordLayout.isErrorEnabled = false
            binding.confirmPasswordLayout.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(password)) {
            binding.signupPasswordLayout.error = "Password is required"
            binding.confirmPasswordLayout.isErrorEnabled = false
            return false
        } else if (password.length < 6) {
            binding.signupPasswordLayout.error = "Password must be at least 6 characters long"
            binding.confirmPasswordLayout.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordLayout.error = "Confirm password is required"
            binding.signupPasswordLayout.isErrorEnabled = false
            return false
        } else if (confirmPassword != password) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            binding.signupPasswordLayout.isErrorEnabled = false
            return false
        }
        binding.signupPasswordLayout.isErrorEnabled = false
        binding.confirmPasswordLayout.isErrorEnabled = false
        return true
    }


    private fun closeKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}