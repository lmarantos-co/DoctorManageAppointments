package com.example.doctorappointments.Chat

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import com.example.doctorappointments.MainActivity
import com.example.doctorappointments.ViewModels.DoctorViewModel
import com.example.doctorsmanageappointments.Chat.TalkJSUser
import com.example.doctorsmanageappointments.R
import com.google.gson.Gson
import java.util.Timer
import java.util.TimerTask

class JavascriptCallbacks(
    private val options: Options,
    private val context: Context // Use Context instead of Activity for Compose
) {
    private val gson = Gson()
    private val loadTimeout = Timer()

    private var doctorViewModel =  DoctorViewModel()

    init {
        // Handle the load timeout logic
        loadTimeout.schedule(object : TimerTask() {
            override fun run() {
                showLoadError()
            }
        }, 10000)
    }

    /**
     * These settings control how chat.js initializes TalkJS inside the WebView.
     */
    data class Options(
        val appId: String = "tlg6pl69",
        val uiType: String,
        val currentUser: TalkJSUser = TalkJSUser.currentUser,
        val chatWith: TalkJSUser? = null,
        val conversationId: String? = null
    )

    @JavascriptInterface
    fun getOptions(): String {
        return gson.toJson(options)
    }

    @JavascriptInterface
    fun showChatUi() {
        loadTimeout.cancel()

        // Update the ViewModel to hide the loading spinner and show the chat UI
        (context as? Activity)?.runOnUiThread {
            (context as? MainActivity)?.chatViewModel?.showChatUi()
        }
    }

    @JavascriptInterface
    fun openConversation(conversationId: String, name: String) {
        // Use the context to get the MainActivity instance
        (context as MainActivity).openChat(doctorViewModel , conversationId, name)
    }

    private fun showLoadError() {
        // In Compose, you could show an alert dialog using Compose's Dialog composable
        (context as? Activity)?.runOnUiThread {
            AlertDialog.Builder(context)
                .setMessage(R.string.chat_timeout_message)
                .setTitle(R.string.chat_timeout_title)
                .setPositiveButton(R.string.retry) { _, _ ->
                    (context as? Activity)?.recreate()
                }
                .setNegativeButton(R.string.back) { _, _ ->
                    (context as? Activity)?.onBackPressed()
                }
                .create()
                .show()
        }
    }
}
