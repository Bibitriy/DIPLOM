package com.example.witte

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.witte.databinding.ActivityMainBinding
import com.example.witte.session.SessionManager
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val editTextUsername: EditText = findViewById(R.id.editTextUsername)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val rememberLoginCheckBox: CheckBox = findViewById(R.id.remeberLogin)

        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", "")
        val savedPassword = sharedPreferences.getString("password", "")
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberMe) {
            editTextUsername.setText(savedUsername)
            editTextPassword.setText(savedPassword)
            rememberLoginCheckBox.isChecked = true
        }

        editTextPassword.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true

            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[2].bounds.width())) {
                        togglePasswordVisibility(editTextPassword)
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val remember = rememberLoginCheckBox.isChecked

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Введите Логин и пароль", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(username, password, remember)
            }
        }
    }

    private fun performLogin(username: String, password: String, remember: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionManager = SessionManager(this@MainActivity, username, password)
                sessionManager.createSession()

                saveLoginDetails(username, password, remember)

                navigateToSecondActivity()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Ошибка входа: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveLoginDetails(username: String, password: String, remember: Boolean) {
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            if (remember) {
                putString("username", username)
                putString("password", password)
                putBoolean("rememberMe", true)
            } else {
                remove("username")
                remove("password")
                putBoolean("rememberMe", false)
            }
            apply()
        }
    }

    private fun navigateToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun togglePasswordVisibility(editTextPassword: EditText) {
        val eye = if (editTextPassword.transformationMethod is PasswordTransformationMethod) {
            editTextPassword.transformationMethod = null
            R.drawable.eye_on
        } else {
            editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            R.drawable.eye_off
        }
        val drawable = ContextCompat.getDrawable(this@MainActivity, eye)
        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        editTextPassword.setSelection(editTextPassword.text.length)
    }
}