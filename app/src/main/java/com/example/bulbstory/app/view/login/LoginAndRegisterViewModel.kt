package com.example.bulbstory.app.view.login

import androidx.lifecycle.ViewModel
import com.example.bulbstory.app.data.DataRepository

class LoginAndRegisterViewModel constructor(private val dataRepository: DataRepository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) = dataRepository.register(name, email, password)

    suspend fun login(email: String, password: String) = dataRepository.login(email, password)

}