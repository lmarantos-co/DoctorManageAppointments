package com.example.doctorappointments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.doctorappointments.ViewModels.DoctorViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.doctorappointments.Chat.JavascriptCallbacks
import com.example.doctorappointments.DataModels.Doctor
import com.example.doctorappointments.DataModels.appointmentData
import com.example.doctorappointments.DataModels.appointmentStatus
import com.example.doctorappointments.DataModels.doctorData
import com.example.doctorappointments.DataModels.patientData
import com.example.doctorappointments.ViewModels.ChatViewModel
import com.example.doctorsmanageappointments.Chat.TalkJSUser
import com.example.doctorsmanageappointments.DataModels.postAppointment
import com.example.doctorsmanageappointments.Navigation.BottomNavItem
import com.example.doctorsmanageappointments.R
import com.example.doctorsmanageappointments.Screens.AppointmentsScreen
import com.example.doctorsmanageappointments.Screens.PatientsScreen
import com.example.doctorsmanageappointments.Screens.SectionsPager
import com.example.doctorsmanageappointments.Screens.getUserByName
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import androidx.compose.material3.Text as Text


class MainActivity : ComponentActivity() {
    val doctorViewModel: DoctorViewModel = DoctorViewModel()

    // Declare your ViewModel
    val chatViewModel: ChatViewModel by viewModels()
    var authToken: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true)

//        userViewModel.login("mkaramati", "1234")



        setContent {
//            LoginScreen(userViewModel)
            mainScreen()
        }

        // Trigger login on some event (e.g., button click)
    }




    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun mainScreen() {

        val coroutineScope = rememberCoroutineScope()
        val navController = rememberNavController()
        val bottomNavItems = listOf(
            BottomNavItem.MainScreen,
            BottomNavItem.Appointments,
            BottomNavItem.Patients,
            BottomNavItem.Chat
        )

        // Get the screen configurationb
        val configuration = LocalConfiguration.current

        // Get the screen width in pixels
        val screenWidthPx = configuration.screenWidthDp

        // Convert pixels to dp using LocalDensity
        val screenWidthDp = with(LocalDensity.current) {
            screenWidthPx.dp
        }


        var loginIconClicked = remember {
            mutableStateOf<Boolean>(false)
        }

        var userName = remember {
            mutableStateOf<String>("")
        }

        var userPassword = remember {
            mutableStateOf<String>("")
        }

        var doctorData = remember {
            mutableStateOf<Doctor?>(null)
        }

        var invokeAuthToken = remember {
            mutableStateOf<Boolean>(true)
        }

        var authToken = remember {
            mutableStateOf<String>("")
        }

        var getUserDetails = remember {
            mutableStateOf<Boolean>(false)
        }

        var openUserLoginScreen = remember {
            mutableStateOf<Boolean>(true)
        }

        var userLoginCompleted = remember {
            mutableStateOf<Boolean>(false)
        }

        var logOutIconClicked = remember {
            mutableStateOf<Boolean>(false)
        }

        //initializing the default selected item
        var navigationSelectedItem by remember {
            mutableStateOf(0)
        }

        val infiniteTransition = rememberInfiniteTransition()
        val alpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Create an infinite transition
        val infiniteTextTransition = rememberInfiniteTransition()

        // Define the animated text size
        val animatedTextSize by infiniteTextTransition.animateFloat(
            initialValue = 20f,       // Initial text size in sp
            targetValue = 25f,        // Target text size in sp
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                ), // Animation duration
                repeatMode = RepeatMode.Reverse  // Reverse the animation to loop
            )
        )

        Column(modifier = Modifier.fillMaxSize(1f))
        {
            Scaffold(modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(modifier = Modifier.fillMaxWidth(),
                        title = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Doctor Manage Appointments",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.Black,
                                    fontSize = if (screenWidthDp < 400.dp) {
                                        20.sp
                                    } else {
                                        25.sp
                                    }
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.LightGray),
                        navigationIcon = {
                            Row()
                            {
                                if (!loginIconClicked.value) {
                                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.login_user),
                                        contentDescription = "Doctor Login Icon",
                                        modifier = Modifier
                                            .alpha(alpha)
                                            .padding(5.dp, 0.dp)
                                            .clickable {
                                                loginIconClicked.value = true
                                                if (logOutIconClicked.value == false) {
                                                    logOutIconClicked.value = true
                                                    openUserLoginScreen.value = true
                                                }

                                            }
                                    )
                                } else {
                                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.login_user),
                                        contentDescription = "Doctor Login Icon",
                                        modifier = Modifier
                                            .alpha(0.5f)
                                            .padding(5.dp, 0.dp)
                                            .clickable {
                                                if (logOutIconClicked.value == false) {
                                                    logOutIconClicked.value = true
//                                                openUserLoginScreen.value = true
                                                }

                                            }
                                    )
                                }
                                Spacer(modifier = Modifier.width(if (screenWidthDp < 400.dp) 20.dp else 50.dp))
                                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.logout),
                                    contentDescription = "Logout Icon",
                                    modifier = Modifier
                                        .alpha(if (!userLoginCompleted.value) 0.0f else 1f)
                                        .clickable {
                                            if (userLoginCompleted.value) {
                                                logOutIconClicked.value = true
                                                userLoginCompleted.value = false
                                                getUserDetails.value = false
                                                doctorViewModel.listOfTalkJSUser = emptyList()
                                                userName.value = ""
                                                userPassword.value = ""
                                                openUserLoginScreen.value = true
                                                loginIconClicked.value = true
                                            }
                                        }
                                )
                            }
                        })
                }
                ,
                bottomBar = {
                    NavigationBar {
                        //getting the list of bottom navigation items for our data class
                        bottomNavItems.forEachIndexed {index,navigationItem ->

                            //iterating all items with their respective indexes
                            NavigationBarItem(
                                selected = index == navigationSelectedItem,
                                label = {
                                    Text(navigationItem.title)
                                },
                                icon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(navigationItem.icon),
                                        contentDescription = navigationItem.title
                                    )
                                },
                                onClick = {
                                    navigationSelectedItem = index
                                    navController.navigate(navigationItem.screenRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
                ,
                content = { innerPadding ->


                    Column()
                    {
                        // Call AppNavHost here within the Scaffold content
                        AppNavHost(
                            navController = navController,
                            doctorViewModel,
                            this@MainActivity
                        )
                        if (openUserLoginScreen.value) {
                            LoginPopup(
                                userName = userName,
                                password = userPassword,
                                openUserLoginPopUp = openUserLoginScreen,
                                loginIconClicked,
                                userLoginCompleted,
                                logOutIconClicked,
                                onDismiss = {
                                    openUserLoginScreen.value = false
                                    if (loginIconClicked.value == false) {
                                        loginIconClicked.value = false
                                    }
                                    if (logOutIconClicked.value == true) {
                                        logOutIconClicked.value = false
                                        loginIconClicked.value = false
                                    }
                                })
                        }
                        if ((invokeAuthToken.value) && (!getUserDetails.value)) {
                            LoginScreen(
                                viewModel = doctorViewModel,
                                invokeAuthToken,
                                getUserDetails,
                                openUserLoginScreen,
                                authToken
                            )
                        }
                        if (getUserDetails.value) {
                            DoctorsDetailsScreen(
                                viewModel = doctorViewModel,
                                doctorData,
                                getUserDetails)
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        )
                        {
                            if (openUserLoginScreen.value) {
                                LoginPopup(
                                    userName = userName,
                                    password = userPassword,
                                    openUserLoginPopUp = openUserLoginScreen,
                                    loginIconClicked,
                                    userLoginCompleted,
                                    logOutIconClicked,
                                    onDismiss = {
                                        openUserLoginScreen.value = false
                                        if (loginIconClicked.value == false) {
                                            loginIconClicked.value = false
                                        }
                                        if (logOutIconClicked.value == true) {
                                            logOutIconClicked.value = false
                                            loginIconClicked.value = false
                                        }
                                    })
                            }
                            //get the auth token related with the user
                            if (userLoginCompleted.value) {
                                doctorViewModel.login("${userName.value}", "${userPassword.value}")
                            }
                            if (getUserDetails.value) {
                                doctorViewModel.getDoctorDetails(authToken.value, userName.value)
                            }

                        }

                    }
                }
            )
        }
    }




    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun LoginPopup(
        userName: MutableState<String>,
        password: MutableState<String>,
        openUserLoginPopUp: MutableState<Boolean>,
        loginIconClicked: MutableState<Boolean>,
        userLoginCompleted: MutableState<Boolean>,
        logOutIconClicked: MutableState<Boolean>,
        onDismiss: () -> Unit
    ) {

        // Request focus for text fields
        val userNameFocusRequester = remember { FocusRequester() }
        val passwordFocusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        // Popup to display the login form

        Popup(
            alignment = Alignment.TopCenter,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true),
            offset = IntOffset(0, 100)
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Username Input
                    OutlinedTextField(
                        value = userName.value,
                        onValueChange = { userName.value = it },
                        label = { Text("Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(userNameFocusRequester),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            userNameFocusRequester.requestFocus() // Move focus to password field
                        }
                        ))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            passwordFocusRequester.requestFocus() // Move focus to password field
                        })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    Button(
                        onClick = {
                            // Handle login action here

                            userLoginCompleted.value = checkUserCredentials()
                            if (checkUserCredentials()) {
                                openUserLoginPopUp.value = false
                                loginIconClicked.value = true
                                if (logOutIconClicked.value)
                                    logOutIconClicked.value = false
                            }
                            onDismiss() // Close the popup after login
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }

    private fun checkUserCredentials(): Boolean {
        return true
    }

    @Composable
    fun DoctorsDetailsScreen(
        viewModel: DoctorViewModel,
        doctorData: MutableState<Doctor?>,
        getUserDetails: MutableState<Boolean>,
    ) {
        val doctorResponse = viewModel.doctorDetailsResponse.observeAsState()
        val loginError = viewModel.loginError.observeAsState()

        LaunchedEffect(doctorResponse.value)
        {
            doctorResponse.value?.let {
                doctorData.value = it
                //pass the memberData as the current Talk JS User
                viewModel.currentTalkJSUser = TalkJSUser(
                    doctorData.value!!.data.userId.toString(),
                    doctorData.value!!.data.fullName,
                    "marco@example.com",
                    "https://demo.talkjs.com/img/marco.jpg",
                    "Hey there! How are you? :-)"
                )
                getUserDetails.value = false
            }
        }

        LaunchedEffect(loginError.value) {
            loginError.value?.let {
                // Display the error (e.g., show a Snackbar)
                println("Error: $it")
            }
        }
    }

    @Composable
    fun LoginScreen(
        viewModel: DoctorViewModel,
        invokeAuth: MutableState<Boolean>,
        getUserDetails: MutableState<Boolean>,
        openUserLoginPopUp: MutableState<Boolean>,
        authToken: MutableState<String>
    ) {
        val loginResponse = viewModel.loginResponse.observeAsState()
        val loginError = viewModel.loginError.observeAsState()

        LaunchedEffect(loginResponse.value) {
            loginResponse.value?.let {
                // Do something with the response (e.g., navigate to another screen)
                println("Token: ${it.jwtToken}")
                authToken.value = it.jwtToken
                invokeAuth.value = false
                getUserDetails.value = true
//            viewModel.getDoctors(it.jwtToken , "gioannou"  ,"1234")
            }
        }

        LaunchedEffect(loginError.value) {
            loginError.value?.let {
                // Display the error (e.g., show a Snackbar)
//                Toast.makeText( , "Error logging with username and password, please try again" , Toast.LENGTH_LONG).show()
                openUserLoginPopUp.value = true
                println("Error: $it")
            }
        }
    }

@Composable
fun AppNavHost(navController: NavHostController, doctorViewModel: DoctorViewModel , mainActivity: MainActivity) {
    NavHost(navController = navController, startDestination = "loggin") {


        // Define mainScreen as a composable route
        composable(route = "loggin") {
            mainScreen()
        }

        composable(
            route = "appointmentsScreen/{authToken}/{userId}",
            arguments = listOf(
                navArgument("authToken") { type = NavType.StringType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->


            AppointmentsScreen(
                backStackEntry,
                navController = navController,
                doctorViewModel,
                mainActivity

            )
        }

        // Add more destinations as needed

        composable(
            route = "patients/{authToken}/{userId}",
            arguments = listOf(
                navArgument("authToken") { type = NavType.StringType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            PatientsScreen(
                backStackEntry,
                navController = navController,
                doctorViewModel
            )
        }

        composable(
            route = "chat/{authToken}/{userId}",
            arguments = listOf(
                navArgument("authToken") { type = NavType.StringType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            SectionsPager(
                backStackEntry,
                navController = navController,
                doctorViewModel
            )
        }

    }
}

    fun openChat(doctorViewModel: DoctorViewModel , conversationId: String, userName: String) {
        // Set the active chat user based on the userName
        val activeUser = getUserByName(doctorViewModel , userName) // Implement this method to find the user by name
        doctorViewModel.activeChatUser = activeUser // Update the active chat user
        doctorViewModel.activeConversationId = conversationId // Update the conversation ID

        // Optionally, navigate to the chatbox screen if needed
        // This can be handled in your Composables
    }

}


class LocalContext {

}
