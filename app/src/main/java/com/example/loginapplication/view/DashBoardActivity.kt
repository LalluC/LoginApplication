package com.example.loginapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.loginapplication.databinding.ActivityDashBoardBinding
import com.example.loginapplication.databinding.ActivityMainBinding
import com.example.loginapplication.model.UserData
import com.example.loginapplication.viewModel.LoginViewModel

class DashBoardActivity : AppCompatActivity() {
    private lateinit var dashBoardBinding:ActivityDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashBoardBinding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(dashBoardBinding.root)
        retrieve()
    }

    private fun retrieve() {
        val userData = intent.extras?.let {
            UserData(
                it.getString("PAN_NUMBER", ""),
                it.getString("BIRTH_DATE", ""),
                it.getString("BIRTH_MONTH", ""),
                it.getString("BIRTH_YEAR", "")
            )
        }
        Log.d("DashBoardActivity", "UserData: $userData")

        userData?.let { data ->
            dashBoardBinding.pan.text = "${data.panNumber}"
            dashBoardBinding.date.text = "${data.birthDate}/${data.birthMonth}/${data.birthYear}"
        }
    }

}