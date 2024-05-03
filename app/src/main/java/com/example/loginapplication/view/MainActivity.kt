package com.example.loginapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.loginapplication.R
import com.example.loginapplication.databinding.ActivityMainBinding
import com.example.loginapplication.model.UserData
import com.example.loginapplication.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        initView()

        mainBinding.noPan. setOnClickListener {
            finish()
        }
        mainBinding.next.setOnClickListener {
            if (isInputValid()) {
                lifecycleScope.launch {
                    redirect()
                }
            } else {
                Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }

        setEditTextListeners()
    }

    private fun redirect() {
        val userData = viewModel.userData

        val intent = Intent(this, DashBoardActivity::class.java).apply {
            val bundle = Bundle().apply {
                putString("PAN_NUMBER", userData.panNumber)
                putString("BIRTH_DATE", userData.birthDate)
                putString("BIRTH_MONTH", userData.birthMonth)
                putString("BIRTH_YEAR", userData.birthYear)
            }
            putExtras(bundle)
        }
        startActivity(intent)
    }

    private fun initView() {
        val panDetailsTextView = mainBinding.panDetails

        val spannableString = SpannableString("Learn More")

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.unselected_button_color)),
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        panDetailsTextView.append(" ")
        panDetailsTextView.append(spannableString)
    }

    private fun setEditTextListeners() {
        mainBinding.panNumberEditText.addTextChangedListener(createTextWatcher())
        mainBinding.birthdate.addTextChangedListener(createTextWatcher())
        mainBinding.birthMonth.addTextChangedListener(createTextWatcher())
        mainBinding.birthYear.addTextChangedListener(createTextWatcher())
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d("TextWatcher", "Text changed")
                mainBinding.next.isEnabled = isInputValid()

                // Update userData in ViewModel
                viewModel.userData = UserData(
                    mainBinding.panNumberEditText.text.toString(),
                    mainBinding.birthdate.text.toString(),
                    mainBinding.birthMonth.text.toString(),
                    mainBinding.birthYear.text.toString()
                )
            }
        }
    }



    private fun isInputValid(): Boolean {
        val panNumber = mainBinding.panNumberEditText.text.toString()
        val birthDate = mainBinding.birthdate.text.toString()
        val birthMonth = mainBinding.birthMonth.text.toString()
        val birthYear = mainBinding.birthYear.text.toString()

        val isPANValid = validatePAN(panNumber)
        val isBirthdateValid = validateBirthdate(birthDate, birthMonth, birthYear)

        val isValid = panNumber.isNotEmpty() && birthDate.isNotEmpty() && birthMonth.isNotEmpty() && birthYear.isNotEmpty()
                && isPANValid && isBirthdateValid

        Log.d("InputValidation", "Is Input Valid: $isValid")

        return isValid
    }





    private fun validatePAN(panNumber: String): Boolean {
        if (panNumber.length != 10) {
            return false
        }

        val panRegex = "[A-Z]{5}[0-9]{4}[A-Z]{1}"
        return panNumber.matches(panRegex.toRegex())
    }

    private fun validateBirthdate(birthDate: String, birthMonth: String, birthYear: String): Boolean {
        val birthDateString = "$birthDate/$birthMonth/$birthYear"

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false

        try {
            val parsedDate = dateFormat.parse(birthDateString)

            if (parsedDate != null) {
                val currentDate = Date()
                if (parsedDate.after(currentDate)) {
                    return false
                }

                return true
            }
        } catch (e: Exception) {
            return false
        }

        return false
    }
}
