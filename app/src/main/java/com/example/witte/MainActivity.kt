package com.example.witte

import android.annotation.SuppressLint
import android.content.Intent
import kotlinx.coroutines.*
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.witte.ParsePersonalAccountData.AccountData
import com.example.witte.databinding.ActivityMainBinding
import com.example.witte.utils.Utils
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var redirect = false;

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidThreeTen.init(this)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val editTextUsername: EditText = findViewById(R.id.editTextUsername)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val rememberLoginCheckBox: CheckBox = findViewById(R.id.remeberLogin)

        var username: String? = Utils.getUsername(this)
        var password: String?

        if (username != null) {
            editTextUsername.setText(username)
            rememberLoginCheckBox.isChecked = true
        }
//        editTextPassword.setText()

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
            username = editTextUsername.text.toString()
            password = editTextPassword.text.toString()
            if (password.isNullOrEmpty() || username.isNullOrEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Введите Логин и пароль",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                if (rememberLoginCheckBox.isChecked) {
                    Utils.saveLogin(username!!, this)
                }
                GlobalScope.launch(Dispatchers.IO) {
                    val accData = AccountData(username, password)
                    try {
                        Utils.saveLessons(this@MainActivity, accData.getSchedule())
                    } catch (err: Exception) {
                        println(err)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                err.javaClass.simpleName,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
                if (Utils.lessonsExists(this@MainActivity)) {
                    print(Utils.loadLessonsFromJSON(this))
                    print(Utils.loadLessonsFromJSON(this))
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

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