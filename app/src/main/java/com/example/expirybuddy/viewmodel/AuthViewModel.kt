package com.example.expirybuddy.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _userName = MutableStateFlow("Ael")
    val userName = _userName.asStateFlow()

    fun logout() {
        _userName.value = ""
    }
}
