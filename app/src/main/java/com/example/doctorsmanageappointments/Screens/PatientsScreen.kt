package com.example.doctorsmanageappointments.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.doctorappointments.DataModels.patientData
import com.example.doctorappointments.ViewModels.DoctorViewModel
import com.example.doctorsmanageappointments.Chat.TalkJSUser
import com.example.doctorsmanageappointments.R

@Composable
fun PatientsScreen(
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    viewModel: DoctorViewModel) {

    // Retrieve arguments
    val authToken = backStackEntry.arguments?.getString("authToken")
    val userId = backStackEntry.arguments?.getInt("userId")

    // Fetch doctors on the first launch
    LaunchedEffect(Unit) {
        viewModel.fetchPatients(authToken!!, userId.toString())
    }

    // Observe patients and errors
    val patientsResponse by viewModel.allPatients.observeAsState()
    val loginError by viewModel.loginError.observeAsState()

    // Display error message if there's an issue
    LaunchedEffect(loginError) {
        loginError?.let {
            // You can replace `println` with showing a Snackbar or Toast in Compose
            println("Error: $it")
        }
    }

    // Check if patients list is available and not empty before rendering
    if (!patientsResponse!!.isNullOrEmpty()) {
        //create a list of TalkJsUsers based on the patient data
        CreateListOfTalkJsUsers(doctorViewModel = viewModel , patientsResponse!!)
        DoctorsPaginatedList(
            viewModel = viewModel,
        )
    } else {
        // Display loading indicator or placeholder if doctors haven't been fetched yet
        Text("Loading patients...") // Replace with a proper loading indicator
    }

}

private fun CreateListOfTalkJsUsers(
    doctorViewModel: DoctorViewModel,
    patientResponse: List<patientData>,
) {
// To clear the list:
    doctorViewModel.listOfTalkJSUser = emptyList()
    for (data in patientResponse) {
        var newTalkJSUser = TalkJSUser(
            data.userId.toString(),
            data.fullName,
            data.email,
            "https://demo.talkjs.com/img/marco.jpg",
            "Hey there! How are you? :-)"
        )
        // Update the list by adding the new user
        doctorViewModel.listOfTalkJSUser = doctorViewModel.listOfTalkJSUser + newTalkJSUser
    }
}

@Composable
fun DoctorsPaginatedList(
    viewModel: DoctorViewModel,
) {
    // Observe patients for the current page
    // Observing the patient data and pagination
    val patientsForPage by remember(viewModel.currentPatientPage) {
        derivedStateOf { viewModel.getPatientsForCurrentPage() }
    }



    Column {
        // LazyColumn to display doctors for the current page
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(patientsForPage) { patient ->
                PatientCard(
                    patient
                )
            }
        }

        // Pagination Controls (Next/Previous Buttons)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { viewModel.previousPatientPage() },
                enabled = viewModel.currentPatientPage > 1 // Disable if on the first page
            ) {
                Text("Previous")
            }

            Text(text = "Page ${viewModel.currentPatientPage} of ${viewModel.totalPatientPages}")

            Button(
                onClick = { viewModel.nextPatientPage() },
                enabled = viewModel.currentPatientPage < viewModel.totalPatientPages // Disable if on the last page
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun PatientCard(
    patient: patientData,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Get the screen configuration
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp
    val screenWidthDp = with(LocalDensity.current) {
        screenWidthPx.dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Doctor's image
            Image(
                painter = painterResource(
                    id = if (patient.gender == "male") {
                        R.drawable.male_patient
                    } else {
                        R.drawable.female_patient
                    }
                ),
                contentDescription = "Patient's Image",
                modifier = Modifier
                    .size(if (screenWidthPx < 400) 50.dp else 64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.fullName,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = patient.dateOfBirth,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = patient.mobile,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = patient.email,
                    style = if (screenWidthDp < 460.dp) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}



