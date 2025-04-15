package com.example.doctorsmanageappointments.Screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.doctorappointments.DataModels.Doctor
import com.example.doctorappointments.DataModels.appointmentData
import com.example.doctorappointments.DataModels.patientData
import com.example.doctorappointments.MainActivity
import com.example.doctorappointments.ViewModels.DoctorViewModel
import com.example.doctorsmanageappointments.DataModels.postAppointment
import com.example.doctorsmanageappointments.R
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale

@Composable
fun AppointmentsScreen(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    doctorViewModel: DoctorViewModel,
    mainActivity: MainActivity
) {

    // Observe the message LiveData
    doctorViewModel.appointmentMessage.observe(mainActivity) { message ->
        Toast.makeText(mainActivity.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    // Observe the message LiveData
    doctorViewModel.appointmentUpdateResponse.observe(mainActivity) { message ->
        Toast.makeText(mainActivity.applicationContext, "Appointment Update: ${message}", Toast.LENGTH_SHORT).show()
    }

    // Retrieve arguments
    val authToken = backStackEntry.arguments?.getString("authToken")
    val userId = backStackEntry.arguments?.getInt("userId")



    // Fetch appointments on first launch
    LaunchedEffect(Unit) {
        doctorViewModel.fetchAppointments(1 , authToken!!, userId.toString())
    }

    var appointmentData = remember {
        mutableStateOf<postAppointment?>(null)
    }

    var appointmentId = remember {
        mutableStateOf<Int>(0)
    }

    var sendAppointmentData = remember {
        mutableStateOf<Boolean>(false)
    }

    var selectDate = remember {
        mutableStateOf<String>("")
    }

    var selectedTime = remember {
        mutableStateOf<String>("")
    }

    var deleteAppointmentData = remember {
        mutableStateOf<Boolean> (false)
    }

    var approveAppointmentData = remember {
        mutableStateOf<Boolean> (false)
    }

    var doctorDetailsData = remember {
        mutableStateOf<Doctor?>(null)
    }

    //observe the patients list in order to use it in the appointments screen
    val patientsResponse by doctorViewModel.allPatients.observeAsState()

    // Observe appointments and errors
    val appointmentsResponse by doctorViewModel.allAppointments.observeAsState(emptyList())
    val loginError by doctorViewModel.loginError.observeAsState()
//        val appointmentsForPage by viewModel.appointmentsForPage.observeAsState(emptyList())
    // Keep track of confirmed and unconfirmed appointments


    //tab selected
    var selectedTabIndex by remember { mutableStateOf(1) }

    // Display error message if there's any
    LaunchedEffect(loginError) {
        loginError?.let {
            println("Error: $it") // Handle error (e.g., show Snackbar or Toast)
        }
    }

    LaunchedEffect(selectedTabIndex) {
        when (selectedTabIndex) {
            1 -> doctorViewModel.fetchAppointments(1, authToken!!, userId.toString()) // Fetch all appointments
            2 -> doctorViewModel.fetchAppointments(2, authToken!!, userId.toString()) // Fetch unconfirmed appointments
        }
    }

    val appointmentsForPage by doctorViewModel.appointmentsForPage.observeAsState(emptyList())
    val unCorfirmedAppointmentsForPage by doctorViewModel.appointmentsForPage.observeAsState(emptyList())
    val appointmentList by doctorViewModel.allAppointments.observeAsState()

    var allAppointmentsList = remember {
        mutableStateOf<List<appointmentData>>(emptyList())
    }


    val appointmenTabTitles = listOf(
        stringResource(R.string.app_tab_text_1),
        stringResource(R.string.app_tab_text_2),
        stringResource(R.string.app_tab_text_3),
    )
    // Update the appointment list state
    if (!appointmentList.isNullOrEmpty()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row(
                modifier = Modifier
                    .padding(10.dp, 10.dp)
                    .border(1.dp, Color.White, shape = RoundedCornerShape(5.dp)),
                horizontalArrangement = Arrangement.End
            ) {

                // TabRow for displaying the tabs
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.padding(10.dp, 10.dp)
                )
                {
                    appointmenTabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                            },
                            modifier = Modifier.fillMaxHeight(),
                            text = { Text(title) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            if (selectedTabIndex == 0) //show the calendar view
            {
                showCalenderView(allAppointmentsList)
            }
            if (selectedTabIndex == 1) // show appointments list
            {

                // Pagination controls
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { doctorViewModel.previousAppointmentPage() },
                        enabled = doctorViewModel.currentAppointmentPage > 1
                    ) {
                        Text("Previous")
                    }

                    Text(text = "Page ${doctorViewModel.currentAppointmentPage} of ${doctorViewModel.totalAppointmentPages}")

                    Button(
                        onClick = { doctorViewModel.nextAppointmentPage() },
                        enabled = doctorViewModel.currentAppointmentPage < doctorViewModel.totalAppointmentPages
                    ) {
                        Text("Next")
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))

                // Display the paginated appointments in LazyColumn
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appointmentsForPage) { appointment ->
                        AppointmentCard(
                            doctorViewModel,
                            appointment = appointment,
                            deleteAppointmentData = deleteAppointmentData,
                            approveAppointmentData = approveAppointmentData,
                            appointmentData = appointmentData,
                            authToken = authToken!!,
                            selectedDate = selectDate,
                            selectedTime = selectedTime,
                            sendAppointmentData = sendAppointmentData,
                            appointmentId = appointmentId,
                            patientsList = patientsResponse!!,
                            doctorDetailsData = doctorDetailsData
                        )
                    }
                }
            }
            if (selectedTabIndex == 2) // show ekremei appointments list
            {
                if (unCorfirmedAppointmentsForPage.size >0)
                {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { doctorViewModel.previousUnAppointmentPage() },
                            enabled = doctorViewModel.currentUnAppointmentPage > 1
                        ) {
                            Text("Previous")
                        }

                        Text(text = "Page ${doctorViewModel.currentUnAppointmentPage} of ${doctorViewModel.totalUnAppointmentPages}")

                        Button(
                            onClick = { doctorViewModel.nextUnAppointmentPage() },
                            enabled = doctorViewModel.currentUnAppointmentPage < doctorViewModel.totalUnAppointmentPages
                        ) {
                            Text("Next")
                        }
                    }


                    Spacer(modifier = Modifier.height(10.dp))

// Display the paginated appointments in LazyColumn
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(unCorfirmedAppointmentsForPage.filter { appointment ->
                            // Replace "confirmed" with the specific status you want to filter by
                            appointment.appointmentStatus.name == "Εκκρεμεί"
                        }) { appointment ->
                            AppointmentCard(
                                doctorViewModel,
                                appointment = appointment,
                                deleteAppointmentData = deleteAppointmentData,
                                approveAppointmentData = approveAppointmentData,
                                appointmentData = appointmentData,
                                authToken = authToken!!,
                                selectedDate = selectDate,
                                selectedTime = selectedTime,
                                sendAppointmentData = sendAppointmentData,
                                appointmentId = appointmentId,
                                patientsList = patientsResponse!!,
                                doctorDetailsData = doctorDetailsData
                            )
                        }
                    }
                }
                else
                {
                    Text(text = "Loading unconfirmed appointments")
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    doctorViewModel : DoctorViewModel,
    appointment: appointmentData?,
    deleteAppointmentData: MutableState<Boolean>,
    approveAppointmentData: MutableState<Boolean>,
    appointmentData: MutableState<postAppointment?>,
    authToken: String,
    selectedDate: MutableState<String>,
    selectedTime: MutableState<String>,
    sendAppointmentData: MutableState<Boolean>,
    appointmentId: MutableState<Int>,
    patientsList: List<patientData?>,
    doctorDetailsData: MutableState<Doctor?>
) {
    // Get the screen configuration
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp
    val screenWidthDp = with(LocalDensity.current) {
        screenWidthPx.dp
    }

    var showAppointmentDialog = remember {
        mutableStateOf<Boolean>(false)
    }

    var appointmentApprove = remember {
        mutableStateOf<Boolean>(false)
    }

    var showDateDialog = remember {
        mutableStateOf<Boolean>(false)
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showAppointmentDialog.value = true }
            .padding(
                if (screenWidthPx < 400) {
                    4.dp
                } else {
                    8.dp
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        shape = RoundedCornerShape(8.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            // Patient's image
            // Image from URL using Coil
            Image(
                painter = rememberImagePainter(
                    data = appointment!!.avatarUrl,
                    builder = {
                        crossfade(true)
                        scale(Scale.FILL)
                    }
                ),
                contentDescription = "Patient Image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.fullName,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row()
                {
                    Text(
                        text = appointment.appointmentDate,
                        style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.appointmentTime,
                        style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = appointment.email,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = appointment.mobile,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = appointment.reasonOfVisit,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = appointment.appointmentStatus.name,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    if (showAppointmentDialog.value) {
        ApproveRejectDialog(
            onApprove = {
                appointmentApprove.value = true
                showAppointmentDialog.value = false
                appointmentId.value = appointment!!.appointmentId
                approveAppointmentData.value = true
                deleteAppointmentData.value = false
            },
            onReject = {
                appointmentApprove.value = false
                showAppointmentDialog.value = false
                deleteAppointmentData.value = true
                //pass the proper attributes to the selected appointmentData in order to post it
                appointmentData.value!!.reasonOfVisit = appointment!!.reasonOfVisit
                appointmentData.value!!.responseUserId = doctorDetailsData.value!!.data.userId
                //get the requestUserId
                var requestId =
                    patientsList.filter { it!!.fullName == appointment.fullName }
                        .get(0)?.userId
                appointmentData.value!!.requestUserId = requestId!!
                appointmentData.value!!.userId = requestId
                appointmentData.value!!.type = "In-Person"
                appointmentData.value!!.reasonOfVisit = "Επίσκεψη"
                appointmentId.value = appointment.appointmentId
                //post a new appointment with date and time
                showDateDialog.value = true
            },
            onDismiss = { showAppointmentDialog.value = false }
        )
    }
    if (deleteAppointmentData.value) {
        // Perform the update
        doctorViewModel.updateAppointment(1, authToken, appointmentId.value)
        // Fetch the updated appointments
        doctorViewModel.fetchAppointments(1 , authToken, doctorDetailsData.value!!.data.userId.toString())
        // Reset the state
        deleteAppointmentData.value = false
    }

    if (approveAppointmentData.value) {
        // Perform the update
        doctorViewModel.updateAppointment(2, authToken, appointmentId.value)
        // Fetch the updated appointments
        doctorViewModel.fetchAppointments(1 , authToken, doctorDetailsData.value!!.data.userId.toString())
        // Reset the state
        approveAppointmentData.value = false
    }

    if (showDateDialog.value) {
        DatePickerWithTimePicker(
            onDateTimeSelected = { dateTime ->
                // Update the selected date and appointment data
                selectedDate.value = dateTime
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(Date())

                appointmentData.value?.appointmentDate = selectedDate.value
                appointmentData.value!!.appointmentTime = selectedTime.value
                sendAppointmentData.value = true

                // Close the date picker after selection
                showDateDialog.value = false
            },
            selectedTime,
            onDismissRequest = {
                // Close the date picker when canceled
                showDateDialog.value = false
            }
        )
    }
}

@Composable
fun ApproveRejectDialog(
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Approve or Reject Appointment") },
        text = { Text(text = "Do you want to approve or reject the appointment?") },
        confirmButton = {
            TextButton(onClick = { onApprove() }) {
                Text("Approve")
            }
        },
        dismissButton = {
            TextButton(onClick = { onReject() }) {
                Text("Reject")
            }
        }
    )
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithTimePicker(
    onDateTimeSelected: (String) -> Unit, // Passes the selected date and time
    selectedTime: MutableState<String>,
    onDismissRequest: () -> Unit
) {
    // Initialize DatePickerState with current date
    val currentDate = System.currentTimeMillis()
    // Initialize DatePickerState
    val datePickerState = remember {
        DatePickerState(
            initialSelectedDateMillis = currentDate,
            initialDisplayedMonthMillis = currentDate,
            yearRange = IntRange(1900, 2100), // Adjust range as needed
            // Removed displayMode here
            DisplayMode.Picker
        )
    }

    var showHourPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    // DatePickerDialog from Material3
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                // Proceed with hour selection if a date is selected
                if (selectedDate.isNotEmpty()) {
                    showHourPicker = true
                }
            }) {
                Text("Next")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 8.dp,
        colors = DatePickerDefaults.colors()
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // Set the size based on orientation
        val datePickerSize = if (isLandscape) {
            300.dp  // Larger size in landscape
        } else {
            400.dp  // Default size in portrait
        }
        // Content of the DatePicker
        DatePicker(
            state = datePickerState,
            modifier = Modifier.size(datePickerSize),
            dateFormatter = DatePickerFormatter(), // Use appropriate formatter
            // Disallow past dates by validating them
            dateValidator = { selectedDateMillis ->
                selectedDateMillis >= currentDate // Ensure the selected date is today or later
            },
            title = { Text("Select Date") },
            headline = { Text("Please select a date") },
            showModeToggle = false,
            colors = DatePickerDefaults.colors()
        )
    }

    // Update selected date when date is selected in DatePicker
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
            selectedDate = date.toString() // Convert to the desired date format
        }
    }

    // Show Hour Picker Dialog after the date is selected
    if (showHourPicker) {
        HourPickerDialog(onHourSelected = { hour ->
            selectedTime.value = "$hour:00:00"
            onDateTimeSelected(selectedDate)
            showHourPicker = false // Close the hour picker after selection
        })
    }
}

@Composable
fun HourPickerDialog(onHourSelected: (Int) -> Unit) {
    val hours = (9..21).toList() // Hours from 9 AM to 9 PM
    AlertDialog(
        onDismissRequest = { /* Do nothing to keep the dialog open */ },
        title = { Text(text = "Select Hour") },
        text = {
            Column {
                hours.forEach { hour ->
                    Text(
                        text = "$hour:00",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onHourSelected(hour)
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

private fun getCurrentHour(i: Int): String
{
    var returnedHour : String = ""
    when (i)
    {
        0 ->
        {
            returnedHour = "00:00"
        }
        1 ->
        {
            returnedHour = "01:00"
        }
        2 ->
        {
            returnedHour = "02:00"
        }
        3 ->
        {
            returnedHour = "03:00"
        }
        4 ->
        {
            returnedHour = "04:00"
        }
        5 ->
        {
            returnedHour = "05:00"
        }
        6 ->
        {
            returnedHour = "06:00"
        }
        7 ->
        {
            returnedHour = "07:00"
        }
        8 ->
        {
            returnedHour = "08:00"
        }
        9 ->
        {
            returnedHour = "09:00"
        }
        10 ->
        {
            returnedHour = "10:00"
        }
        11 ->
        {
            returnedHour = "11:00"
        }
        12 ->
        {
            returnedHour = "12:00"
        }
        13 ->
        {
            returnedHour = "13:00"
        }
        14 ->
        {
            returnedHour = "14:00"
        }
        15 ->
        {
            returnedHour = "15:00"
        }
        16 ->
        {
            returnedHour = "16:00"
        }
        17 ->
        {
            returnedHour = "17:00"
        }
        18 ->
        {
            returnedHour = "18:00"
        }
        19 ->
        {
            returnedHour = "19:00"
        }
        20 ->
        {
            returnedHour = "200:00"
        }
        21 ->
        {
            returnedHour = "21:00"
        }
        22 ->
        {
            returnedHour = "22:00"
        }
        23 ->
        {
            returnedHour = "23:00"
        }
    }
    return returnedHour
}

@Composable
private fun showCalenderView(appointmentList: MutableState<List<appointmentData>>) {
    var currentMonth = getCurrentMonthName()
    var currentYear = getCurrentYear()

    // Get the configuration
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp // Width in dp
    val screenHeightDp = configuration.screenHeightDp // Height in dp

    var dateSelectionButton = remember {
        mutableStateOf<Int>(1)
    }

    var selectedMonth = remember {
        mutableStateOf<Month>(LocalDate.now().month)
    }

    var selectedWeek = remember {
        mutableStateOf<Int>(getCurrentWeekNumber())
    }

    var selectedDay = remember {
        mutableStateOf<DayOfWeek>(LocalDate.now().dayOfWeek)
    }

    var selectedDate = remember {
        mutableStateOf<LocalDate>(LocalDate.now())
    }

    var selectedDayOfYear = remember {
        mutableStateOf<Int>(LocalDate.now().dayOfYear)
    }

    var selectedYear = LocalDate.now().year

    val selectedAppointmentWeek = remember {
        mutableStateOf(Pair(LocalDate.now(), LocalDate.now())) // Initial value with current date
    }

    Card(modifier = Modifier
        .border(1.dp, color = Color.Red, shape = RoundedCornerShape(5.dp))
        .fillMaxWidth(1f)
        .padding(10.dp, 10.dp))
    {
        Column(verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally)
        {
            Row(horizontalArrangement = Arrangement.Center)
            {
                Box(
                    modifier = Modifier
                        .size(if (screenWidthDp < 460) 8.dp else 16.dp) // Set the size of the dot
                        .clip(CircleShape) // Make it circular
                        .background(Color.Blue) // Set the color to red
                )
                Text("Επιβεβαιωμένο", fontSize = if (screenWidthDp < 460) 8.sp else 16.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .size(if (screenWidthDp < 460) 8.dp else 16.dp) // Set the size of the dot
                        .clip(CircleShape) // Make it circular
                        .background(Color.Red) // Set the color to red
                )
                Text("Ακυρωμένο", fontSize = if (screenWidthDp < 460) 8.sp else 16.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .size(if (screenWidthDp < 460) 8.dp else 16.dp) // Set the size of the dot
                        .clip(CircleShape) // Make it circular
                        .background(Color.Yellow) // Set the color to red
                )
                Text("Επόμενα ραντεβού", fontSize = if (screenWidthDp < 460) 8.sp else 16.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .size(if (screenWidthDp < 460) 8.dp else 16.dp) // Set the size of the dot
                        .clip(CircleShape) // Make it circular
                        .background(Color.Green) // Set the color to red
                )
                Text("Διεκπαιρωμένο", fontSize = if (screenWidthDp < 460) 8.sp else 16.sp)
            }
            //row to hold buttons  / dateperiod / dateSelection
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly)
            {
                //previous next buttons
                Row(
                    modifier = Modifier.border(
                        1.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(5.dp)
                    ).weight(1f),
                    horizontalArrangement = Arrangement.SpaceAround
                )
                {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // Default Material Icon
                        contentDescription = "Back Arrow",
                        modifier = Modifier.size(30.dp).clickable {
                            // Handle click event
                            when (dateSelectionButton.value) {
                                1 -> selectedMonth.value = selectedMonth.value.minus(1)
                                2 -> selectedWeek.value = selectedWeek.value.minus(1)
                                3 -> selectedDate.value = selectedDate.value.minusDays(1)
                            }
                        },
                        tint = Color.Black // Set icon color
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward, // Default Material Icon
                        contentDescription = "Forward Arrow",
                        modifier = Modifier.size(30.dp).clickable {
                            // Handle click event
                            when (dateSelectionButton.value) {
                                1 -> selectedMonth.value = selectedMonth.value.plus(1)
                                2 -> selectedWeek.value = selectedWeek.value.plus(1)
                                3 -> selectedDate.value = selectedDate.value.plusDays(1)
                            }
                        },
                        tint = Color.Black // Set icon color
                    )
                }
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center)
                {
                    selectedAppointmentWeek.value = getDatesForWeek(selectedWeek.value)
                    if (dateSelectionButton.value == 1)
                        Text(
                            text = "${selectedMonth.value.toString()} ${selectedYear}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    if (dateSelectionButton.value == 2)
                        Text(text = "${selectedAppointmentWeek.value.first} ${selectedAppointmentWeek.value.second}")
                    if (dateSelectionButton.value == 3)
                        Text(text = "${selectedDate.value.toString()}")
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth() // Ensure it fills the available width
                        .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                        .border(1.dp, Color.Gray) // Border around the entire row
                        .padding(4.dp) // Padding inside the row
                        .weight(1f)
                ) {
                    // Month
                    Box(
                        modifier = Modifier
                            .weight(1f) // Equal width for each item
                            .padding(4.dp) // Space between text and border
                            .background(
                                color = if (dateSelectionButton.value == 1) Color.Red else Color.Transparent,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable {
                                dateSelectionButton.value = 1
                            }
                    ) {
                        Text(
                            text = "Month",
                            fontSize = if (screenWidthDp < 400) 10.sp else 12.sp,
                            color = if (dateSelectionButton.value == 1) Color.White else Color.Black,
                            modifier = Modifier.align(Alignment.Center) // Center the text
                        )
                    }

                    // Week
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .background(
                                color = if (dateSelectionButton.value == 2) Color.Red else Color.Transparent,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable {
                                dateSelectionButton.value = 2
                            }
                    ) {
                        Text(
                            text = "Week",
                            fontSize = if (screenWidthDp < 400) 10.sp else 12.sp,
                            color = if (dateSelectionButton.value == 2) Color.White else Color.Black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Day
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .background(
                                color = if (dateSelectionButton.value == 3) Color.Red else Color.Transparent,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable {
                                dateSelectionButton.value = 3
                            }
                    ) {
                        Text(
                            text = "Day",
                            fontSize = if (screenWidthDp < 400) 10.sp else 12.sp,
                            color = if (dateSelectionButton.value == 3) Color.White else Color.Black,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

            }
            if (dateSelectionButton.value == 1) // if we selected month period
            {
                var appointmentCardDate: LocalDate =
                    LocalDate.of(selectedYear, selectedMonth.value, 1)
                Spacer(modifier = Modifier.height(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    Row(horizontalArrangement = Arrangement.Center)
                    {
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Sun",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Mon",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Tue",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Wed",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Thu",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Fri",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Sat",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )

                    }
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    //create 6 different columns
                    for (i in 0..5) {
                        Column()
                        {
                            Row()
                            {
                                for (j in 0..6) {
                                    if ((i == 0) && (j == 0)) {
                                        appointmentCardDate = getFirstSundayBeforeMonth(
                                            selectedYear,
                                            selectedMonth.value
                                        )
                                    }
//                                        else
//                                        {
//                                            appointmentCardDate = appointmentCardDate.plusWeeks(i.toLong()).plusDays(j.toLong())
//                                        }
                                    appointmentDateCard(
                                        selectedMonth.value,
                                        appointmentCardDate.plusWeeks(i.toLong())
                                            .plusDays(j.toLong()),
                                        appointmentList
                                    )
                                }
                            }
                        }
                    }

                }
            }
            if (dateSelectionButton.value == 2) {
                var appointmentCardDate: LocalDate =
                    LocalDate.of(selectedYear, selectedMonth.value, 1)
                Spacer(modifier = Modifier.height(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    Row(horizontalArrangement = Arrangement.Center)
                    {
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Sun ${
                                selectedAppointmentWeek.value.first.toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Mon ${
                                selectedAppointmentWeek.value.first.plusDays(1L).toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Tue ${
                                selectedAppointmentWeek.value.first.plusDays(2L).toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Wed ${
                                selectedAppointmentWeek.value.first.plusDays(3L).toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Thu ${
                                selectedAppointmentWeek.value.first.plusDays(4L).toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Fri ${
                                selectedAppointmentWeek.value.first.plusDays(5L).toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )
                        Text(
                            text = "Sat ${
                                selectedAppointmentWeek.value.first.plusDays(6L).toString()
                                    .subSequence(5, 10)
                            }",
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .background(color = Color.LightGray)
                        )
                        Divider(
                            modifier = Modifier
                                .height((screenHeightDp / 50).dp)
                                .width(1.dp)
                                .border(1.dp, color = Color.DarkGray)
                        )

                    }
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    //place the hour grid
                    for (i in 0..24)
                    {
                        Row(modifier = Modifier.fillMaxWidth())
                        {
                            Divider(
                                modifier = Modifier
                                    .height((screenHeightDp / 50).dp)
                                    .width(1.dp)
                                    .border(1.dp, color = Color.DarkGray)
                            )
                            Text(
                                text = "${getCurrentHour(i)}",
                                fontSize = 5.sp,
                                modifier = Modifier
                                    .height((screenHeightDp / 40).dp)
                                    .width((screenWidthDp / 60).dp))
                            Spacer(modifier = Modifier.width((screenWidthDp / 10).dp))
                            for (j in 0..6)
                            {
                                var currentHour = getCurrentHour(i)
                                appointmentWeekdateCard(
                                    curentHour = currentHour,
                                    date = selectedAppointmentWeek.value.first.plusDays(j.toLong()),
                                    appointmentList = appointmentList
                                )
                            }
                        }
                        Spacer(modifier = Modifier.fillMaxWidth(1f).height(1.dp).border(1.dp , color = Color.Gray))
                    }
                }

            }
            if (dateSelectionButton.value == 3) {
                var appointmentCardDate: LocalDate =
                    LocalDate.of(selectedYear, selectedMonth.value, 1)
                Spacer(modifier = Modifier.height(10.dp))
                Column(horizontalAlignment = Alignment.Start)
                {
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    Row(horizontalArrangement = Arrangement.Center)
                    {
                        var currentDay = selectedDate.value.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        Text(text = "${currentDay.toString()}")
                    }
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    //place the hour grid
                    for (i in 0..24)
                    {
                        Row()
                        {
                            Divider(
                                modifier = Modifier
                                    .height((screenHeightDp / 50).dp)
                                    .width(1.dp)
                                    .border(1.dp, color = Color.DarkGray)
                            )
                            Text(
                                text = "${getCurrentHour(i)}",
                                fontSize = 5.sp,
                                modifier = Modifier
                                    .height((screenHeightDp / 40).dp)
                                    .width((screenWidthDp / 60).dp))

                            var currentHour = getCurrentHour(i)
                            appointmentDaydateCard(
                                curentHour = currentHour,
                                date = selectedDate.value,
                                appointmentList = appointmentList
                            )
                        }
                        Spacer(modifier = Modifier.fillMaxWidth(1f).height(1.dp).border(1.dp , color = Color.Gray))
                    }
                }

            }
        }
    }
}

fun getDatesForWeek(weekNumber: Int): Pair<LocalDate, LocalDate> {
    // Get the first day of the year
    val firstDayOfYear = LocalDate.of(LocalDate.now().year, 1, 1)

    // Find the first Sunday of the year
    val firstSunday = firstDayOfYear.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7L)

    // Calculate the start of the selected week
    val startOfWeek = firstSunday.plusWeeks((weekNumber - 1).toLong())

    // The end of the week (Saturday is 6 days after Sunday)
    val endOfWeek = startOfWeek.plusDays(6)

    return Pair(startOfWeek, endOfWeek)
}

@Composable
private fun appointmentDateCard(
    curentMonth : Month,
    date : LocalDate,
    appointmentList: MutableState<List<appointmentData>>
) {
    // Get the configuration
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp // Width in dp
    val screenHeightDp = configuration.screenHeightDp // Height in dp

    //check if there are appointments with the selected date
    var appointments = getAppointmentsForSelectedDate(date, appointmentList)

    var showPopUpWindow = remember {
        mutableStateOf(false)
    }

    var clickedAppointment = remember {
        mutableStateOf<appointmentData?>(null)
    }

    Card(modifier = Modifier
        .width((screenWidthDp / 9).dp)
        .height((screenHeightDp / 9).dp))
    {
        Column(verticalArrangement = Arrangement.Center)
        {
            Row(horizontalArrangement = Arrangement.End)
            {
                if (curentMonth.value == date.monthValue) {
                    Text(text = date.dayOfMonth.toString())
                } else {
                    Text(text = date.dayOfMonth.toString(), modifier = Modifier.alpha(0.5f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (curentMonth.value == date.monthValue) {
                for (appointment in appointments) {
                    Row(modifier = Modifier.clickable {
                        showPopUpWindow.value = true
                        clickedAppointment.value = appointment
                    })
                    {
                        Box(
                            modifier = Modifier
                                .size(if (screenWidthDp < 460) 8.dp else 12.dp) // Set the size of the dot
                                .clip(CircleShape) // Make it circular
                                .background(
                                    color = if (appointment!!.appointmentStatus.name == "Επιβεβαιωμένο") {
                                        Color.Blue
                                    } else {
                                        if (appointment!!.appointmentStatus.name == "Απορρίφθηκε") {
                                            Color.Red
                                        } else {
                                            if (appointment!!.appointmentStatus.name == "Εκκρεμεί") {
                                                Color.Yellow
                                            } else {
                                                Color.Green
                                            }
                                        }
                                    }
                                )
                        )
                        Text(
                            text = appointment.appointmentTime.toString().substring(0, 2),
                            fontSize = if (screenWidthDp < 460) 8.sp else 12.sp
                        )
                        Text(text = appointment.fullName, fontSize = if (screenWidthDp < 460) 6.sp else 10.sp)
                    }

                }
            }

        }

    }
    if (showPopUpWindow.value) {
        Popup(
            alignment = Alignment.TopCenter,
            onDismissRequest = { showPopUpWindow.value = false },
            properties = PopupProperties(focusable = true),
            offset = IntOffset(0, 100)
        )
        {
            Card(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        showPopUpWindow.value = false
                        clickedAppointment.value = null
                    })
            {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(text = clickedAppointment.value!!.fullName)
                    Text(text = "${clickedAppointment.value!!.appointmentDate} ${clickedAppointment.value!!.appointmentTime}")
                    Text(text = "${clickedAppointment.value!!.mobile}")
                    Text(text = "${clickedAppointment.value!!.email}")
                    Text(text = "${clickedAppointment.value!!.reasonOfVisit}")
                }

            }
        }

    }
}

@Composable
private fun appointmentDaydateCard(curentHour : String, date : LocalDate, appointmentList: MutableState<List<appointmentData>>)
{
    // Get the configuration
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp // Width in dp
    val screenHeightDp = configuration.screenHeightDp // Height in dp
    //check if there are appointments with the selected date
    var appointments = getAppointmentsForSelectedDate(date, appointmentList)

    var showPopUpWindow = remember {
        mutableStateOf(false)
    }

    var clickedAppointment = remember {
        mutableStateOf<appointmentData?>(null)
    }

    Card(modifier = Modifier
        .fillMaxWidth(0.9f)
        .height((screenHeightDp / 40).dp))
    {
        Column(verticalArrangement = Arrangement.Center)
        {
            for (appointment in appointments) {
                if (curentHour == appointment!!.appointmentTime.substring(0 , 5))
                {
                    Row(modifier = Modifier.clickable {
                        showPopUpWindow.value = true
                        clickedAppointment.value = appointment
                    })
                    {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height((screenHeightDp / 40).dp)
                                .background(
                                    color = if (appointment!!.appointmentStatus.name == "Επιβεβαιωμένο") {
                                        Color.Blue
                                    } else {
                                        if (appointment!!.appointmentStatus.name == "Απορρίφθηκε") {
                                            Color.Red
                                        } else {
                                            if (appointment!!.appointmentStatus.name == "Εκκρεμεί") {
                                                Color.Yellow
                                            } else {
                                                Color.Green
                                            }
                                        }
                                    }
                                ), contentAlignment = Alignment.Center
                        )
                        {
                            Row(horizontalArrangement = Arrangement.Center)
                            {
                                Text(text = "${appointment!!.appointmentTime}      " , fontSize = if (screenWidthDp < 460) 8.sp else 12.sp)
                                Text(text = "${appointment!!.fullName}" , fontSize = if (screenWidthDp < 460) 8.sp else 12.sp)
                            }
                        }
                    }
                }
            }

        }

    }
    if (showPopUpWindow.value) {
        Popup(
            alignment = Alignment.TopCenter,
            onDismissRequest = { showPopUpWindow.value = false },
            properties = PopupProperties(focusable = true),
            offset = IntOffset(0, 100)
        )
        {
            Card(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        showPopUpWindow.value = false
                        clickedAppointment.value = null
                    })
            {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(text = clickedAppointment.value!!.fullName)
                    Text(text = "${clickedAppointment.value!!.appointmentDate} ${clickedAppointment.value!!.appointmentTime}")
                    Text(text = "${clickedAppointment.value!!.mobile}")
                    Text(text = "${clickedAppointment.value!!.email}")
                    Text(text = "${clickedAppointment.value!!.reasonOfVisit}")
                }

            }
        }

    }
}

@Composable
private fun appointmentWeekdateCard(curentHour : String, date : LocalDate, appointmentList: MutableState<List<appointmentData>>)
{
    // Get the configuration
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp // Width in dp
    val screenHeightDp = configuration.screenHeightDp // Height in dp
    //check if there are appointments with the selected date
    var appointments = getAppointmentsForSelectedDate(date, appointmentList)

    var showPopUpWindow = remember {
        mutableStateOf(false)
    }

    var clickedAppointment = remember {
        mutableStateOf<appointmentData?>(null)
    }

    Card(modifier = Modifier
        .width((screenWidthDp / 9).dp)
        .height((screenHeightDp / 40).dp))
    {
        Column(verticalArrangement = Arrangement.Center)
        {
            for (appointment in appointments) {
                if (curentHour == appointment!!.appointmentTime.substring(0 , 5))
                {
                    Row(modifier = Modifier.clickable {
                        showPopUpWindow.value = true
                        clickedAppointment.value = appointment
                    })
                    {
                        Box(
                            modifier = Modifier
                                .width((screenWidthDp / 9).dp)
                                .height((screenHeightDp / 40).dp)
                                .background(
                                    color = if (appointment!!.appointmentStatus.name == "Επιβεβαιωμένο") {
                                        Color.Blue
                                    } else {
                                        if (appointment!!.appointmentStatus.name == "Απορρίφθηκε") {
                                            Color.Red
                                        } else {
                                            if (appointment!!.appointmentStatus.name == "Εκκρεμεί") {
                                                Color.Yellow
                                            } else {
                                                Color.Green
                                            }
                                        }
                                    }
                                ), contentAlignment = Alignment.Center
                        )
                        {
                            Column(horizontalAlignment = Alignment.CenterHorizontally)
                            {
                                Text(text = "${appointment!!.appointmentTime}" , fontSize = if (screenWidthDp < 460) 5.sp else 6.sp)
                                Text(text = "${appointment!!.fullName}" , fontSize = if (screenWidthDp < 460) 5.sp else 6.sp)
                            }
                        }
                    }
                }
            }

        }

    }
    if (showPopUpWindow.value) {
        Popup(
            alignment = Alignment.TopCenter,
            onDismissRequest = { showPopUpWindow.value = false },
            properties = PopupProperties(focusable = true),
            offset = IntOffset(0, 100)
        )
        {
            Card(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        showPopUpWindow.value = false
                        clickedAppointment.value = null
                    })
            {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(text = clickedAppointment.value!!.fullName)
                    Text(text = "${clickedAppointment.value!!.appointmentDate} ${clickedAppointment.value!!.appointmentTime}")
                    Text(text = "${clickedAppointment.value!!.mobile}")
                    Text(text = "${clickedAppointment.value!!.email}")
                    Text(text = "${clickedAppointment.value!!.reasonOfVisit}")
                }

            }
        }

    }
}

private fun getAppointmentsForSelectedDate(date: LocalDate, appointmentList: MutableState<List<appointmentData>>) : ArrayList<appointmentData?>
{
    var appointmentsOnDate : ArrayList<appointmentData?> = ArrayList()
    for (appointment in appointmentList.value)
    {
        //transform the appointment date to a localdate
        var appointmentDateString = appointment.appointmentDate
        val appointmentDate = LocalDate.parse(appointmentDateString, DateTimeFormatter.ISO_LOCAL_DATE)
        if (appointmentDate.isEqual(date))
        {
            appointmentsOnDate.add(appointment)
        }
    }
    return appointmentsOnDate
}

fun getFirstSundayBeforeMonth(year: Int, month: Month): LocalDate {
    // Get the first day of the month
    val firstDayOfMonth = LocalDate.of(year, month, 1)

    // Get the day of the week for the first day of the month
    val dayOfWeek = firstDayOfMonth.dayOfWeek

    // Calculate how many days to go back to reach the previous Sunday
    val daysToGoBack = if (dayOfWeek == DayOfWeek.SUNDAY) {
        0 // If the first day is Sunday, return that date
    } else {
        dayOfWeek.value // Move back to the last Sunday
    }

    // Calculate the date of the last Sunday before the first day of the month
    return firstDayOfMonth.minusDays(daysToGoBack.toLong())
}


fun getCurrentMonthName(): String {
    val currentMonth = LocalDate.now().month
    // Get the month name in full style (e.g., "October")
    return currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault())
}

fun getCurrentYear(): String {
    val currentYear = LocalDate.now().year
    // Get the month name in full style (e.g., "October")
    return currentYear.toString()
}

fun getCurrentWeekNumber(): Int {
    val today = LocalDate.now()
    val weekFields = WeekFields.of(DayOfWeek.SUNDAY, 1) // Start week on Monday
    return today.get(weekFields.weekOfWeekBasedYear())
}