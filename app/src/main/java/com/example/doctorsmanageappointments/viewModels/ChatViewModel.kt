package com.example.doctorappointments.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    var isChatUiLoaded by mutableStateOf(false)
        private set


    fun showChatUi() {
        isChatUiLoaded = true
    }

    fun resetChatUi() {
        isChatUiLoaded = false  // Show spinner, reset to initial state
    }
}
