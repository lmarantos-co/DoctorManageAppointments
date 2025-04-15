package com.example.doctorsmanageappointments.Screens

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.doctorappointments.Chat.JavascriptCallbacks
import com.example.doctorappointments.DataModels.patientData
import com.example.doctorappointments.ViewModels.ChatViewModel
import com.example.doctorappointments.ViewModels.DoctorViewModel

import com.example.doctorsmanageappointments.Chat.TalkJSUser
import com.example.doctorsmanageappointments.R


@Composable
fun SectionsPager(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    doctorViewModel: DoctorViewModel) {
    val tabTitles = listOf(
        stringResource(R.string.tab_text_1),  // Inbox tab title
        stringResource(R.string.tab_text_2)   // Users tab title
    )

    var listOfPatientData = doctorViewModel.allPatients.value
    var currentUser = doctorViewModel.currentTalkJSUser
    var selectedTabIndex by remember { mutableStateOf(1) }

    Column(modifier = Modifier.padding(0.dp, 100.dp)) {
        // TabRow for displaying the tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.padding(10.dp, 10.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        doctorViewModel.activeChatUser = null  // Reset the chat when switching tabs
                    },
                    modifier = Modifier.fillMaxHeight(),
                    text = { Text(title) }
                )
            }
        }

        // If `activeChatUser` is not null, show the ChatboxScreen
        if (doctorViewModel.activeChatUser != null) {
            ChatboxScreen(
                doctorViewModel,
                chatWith = doctorViewModel.activeChatUser!!, // Pass the selected doctor
            )
        } else {
            // Content of the selected tab
            when (selectedTabIndex) {
                0 -> InboxTab(
                    currentUser = doctorViewModel.currentTalkJSUser!!,
                    chatWith = doctorViewModel.activeChatUser,
                    conversationId = doctorViewModel.activeConversationId
                )

                1 -> UsersTab(
                    listOfPatientJSUser = doctorViewModel.listOfTalkJSUser,
                    listOfPatientData!!,
                    currentUser = currentUser!!
                ) { selectedUser ->
                    doctorViewModel.activeChatUser = selectedUser // Set the selected user for chat
                }
            }
        }
    }
}

@Composable
fun ChatboxScreen(
    doctorViewModel: DoctorViewModel,
    chatWith: TalkJSUser, // Pass the TalkJSUser object
) {
    // Observe the state from the ViewModel
    // Directly access the isChatUiLoaded state
//        val isChatUiLoaded = chatViewModel.isChatUiLoaded
//
//        if (isChatUiLoaded) {
    // Setting up the WebView inside AndroidView
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                loadUrl("file:///android_asset/index.html")

                // Set up the interface for JavaScript
                addJavascriptInterface(
                    JavascriptCallbacks(
                        JavascriptCallbacks.Options(
                            appId = "tlg6pl69", "chatbox",
                            doctorViewModel.currentTalkJSUser!!, chatWith, null
                        ),
                        context
                    ),
                    "app"
                )
            }
        },
        update = { webView ->
            // Optionally update WebView on recomposition
        },
        modifier = Modifier.fillMaxSize()
    )
//        }
//    else {
//            // Show loading spinner while waiting for the chat UI to load
//            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
//        }
}

fun openChat(doctorViewModel: DoctorViewModel , conversationId: String, userName: String) {
    // Set the active chat user based on the userName
    val activeUser = getUserByName(doctorViewModel , userName) // Implement this method to find the user by name
    doctorViewModel.activeChatUser = activeUser // Update the active chat user
    doctorViewModel.activeConversationId = conversationId // Update the conversation ID

    // Optionally, navigate to the chatbox screen if needed
    // This can be handled in your Composables
}

fun getUserByName(doctorViewModel: DoctorViewModel , name: String): TalkJSUser? {
    // Assuming you have a list of users to search from
    return doctorViewModel.listOfTalkJSUser.find { it.name == name }
}


@Composable
fun InboxTab(
    currentUser: TalkJSUser, // Pass the current user
    chatWith: TalkJSUser?,   // Optionally pass the user to chat with
    conversationId: String?  // Optionally pass the conversation ID
) {
    val context = LocalContext.current

    // Create an instance of Options with the passed values
    val options = JavascriptCallbacks.Options(
        uiType = "inbox",
        currentUser = currentUser,
        chatWith = chatWith,
        conversationId = conversationId
    )

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                // Pass the created options to JavascriptCallbacks
                addJavascriptInterface(JavascriptCallbacks(options, context), "app")
                loadUrl("file:///android_asset/index.html")
            }
        },
        update = { webView ->
            // Optionally update the WebView if needed
        }
    )
}

@Composable
fun UsersTab(
    listOfPatientJSUser: List<TalkJSUser>,
    listOfPatientData: List<patientData>,
    currentUser: TalkJSUser,           // The current user (if needed)
    onChatWithUser: (TalkJSUser) -> Unit  // Callback for starting a chat with a selected user
) {
    // A list of user names (assuming `TalkJSUser.allUsers` is accessible)
//        val users = TalkJSUser.allUsers
    val patients = listOfPatientJSUser

    // LazyColumn to display the list of user names
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(patients) { user ->
            var patient = getPatientFromUser(user, listOfPatientData)
            UserRow(user, patient, onChatWithUser)
            Divider(thickness = 1.dp)
        }
    }
}

private fun getPatientFromUser(
    user: TalkJSUser,
    listOfPatientData: List<patientData>
): patientData {
    var doctorData = listOfPatientData.filter { it.fullName == user.name }
    return doctorData.get(0)
}

@Composable
fun UserRow(user: TalkJSUser, patient: patientData, onChatWithUser: (TalkJSUser) -> Unit) {

    // Get the screen configuration
    val configuration = LocalConfiguration.current

    // Get the screen width in pixels
    val screenWidthPx = configuration.screenWidthDp

    // Convert pixels to dp using LocalDensity
    val screenWidthDp = with(LocalDensity.current) {
        screenWidthPx.dp
    }


    // Display a row with the user's name
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = patient.fullName,
            style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    onChatWithUser(user)
                }
        )

        Text(
            text = patient.dateOfBirth,
            style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    onChatWithUser(user)
                }
        )

        Text(
            text = patient.mobile,
            style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    onChatWithUser(user)
                }
        )

        Text(
            text = patient.email,
            style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    onChatWithUser(user)
                }
        )
    }
}