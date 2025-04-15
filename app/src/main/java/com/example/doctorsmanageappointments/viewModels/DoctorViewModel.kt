package com.example.doctorappointments.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctorappointments.DataModels.Doctor
import com.example.doctorappointments.DataModels.LoginResponse
import com.example.doctorappointments.DataModels.appointment
import com.example.doctorappointments.DataModels.appointmentData
import com.example.doctorappointments.DataModels.appointmentResponse
import com.example.doctorappointments.DataModels.member
import com.example.doctorappointments.DataModels.patientData
import com.example.doctorappointments.Repository.UserRepository
import com.example.doctorsmanageappointments.Chat.TalkJSUser
import com.example.doctorsmanageappointments.DataModels.AppointmentResponse
import com.example.doctorsmanageappointments.DataModels.postAppointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {

    private val repository = UserRepository()

    // LiveData for login response
    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse


    // LiveData for error handling
    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    private val _memberResponse = MutableLiveData<member>()
    val memberResponse : LiveData<member> get() = _memberResponse

    private val _doctorDetailsResponse = MutableLiveData<Doctor>()
    val doctorDetailsResponse : LiveData<Doctor> get() = _doctorDetailsResponse

    private val _appointmentResponse = MutableLiveData<appointmentResponse>()
    val appointmentResponse : LiveData<appointmentResponse> get() = _appointmentResponse

    private val _appointmentUpdateResponse = MutableLiveData<String>()
    val appointmentUpdateResponse : LiveData<String> get() = _appointmentUpdateResponse

    // LiveData for appointment result message (success or error)
    private val _appointmentMessage = MutableLiveData<String>()
    val appointmentMessage: LiveData<String> = _appointmentMessage

    private val _appointmentsForPage = MutableLiveData<List<appointmentData>>()
    val appointmentsForPage: LiveData<List<appointmentData>> get() = _appointmentsForPage

    private val _unCorfimedappointmentsForPage = MutableLiveData<List<appointmentData>>()
    val unCorfimedappointmentsForPage: LiveData<List<appointmentData>> get() = _unCorfimedappointmentsForPage

    // Function to trigger login using Coroutines
    fun login(userName: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(userName, password)
                if (response != null) {
                    _loginResponse.value = response
                } else {
                    _loginError.value = "Failed to login. Please try again."
                }
            } catch (e: Exception) {
                _loginError.value = "Error: ${e.message}"
            }
        }
    }


    fun getMember(authToken: String , userName: String)
    {
        viewModelScope.launch {
            try {
                val response = repository.getMember(authToken , userName)
                if (response != null) {
                    _memberResponse.value = response!!
                } else {
                    _loginError.value = "Failed to login. Please try again."
                }
            } catch (e: Exception) {
                _loginError.value = "Error: ${e.message}"
            }
        }
    }

    fun getDoctorDetails(authToken: String , userName : String)
    {
        viewModelScope.launch {
            try {
                val response = repository.getDoctorDetails(authToken , userName)
                if (response != null) {
                    _doctorDetailsResponse.value = response!!
                } else {
                    _loginError.value = "Failed to get doctor details."
                }
            }
            catch (e: Exception) {
                _loginError.value = "Error: ${e.message}"
            }
        }
    }

    //talkJSUsers

    //inbox chat variables
    // Declare state variables for active chat user and conversation ID
    var activeChatUser by mutableStateOf<TalkJSUser?>(null)
    var currentTalkJSUser by mutableStateOf<TalkJSUser?>(null)
    var activeConversationId by mutableStateOf("") // Initialize as an empty string or null
    var listOfTalkJSUser by
    mutableStateOf<List<TalkJSUser>>(arrayListOf())



    // MutableStateFlow for storing the entire list of patients
    private val _allPatients = MutableLiveData<List<patientData>>(emptyList()) // Start with an empty list
    val allPatients: LiveData<List<patientData>> = _allPatients

    // State to keep track of the current page
    var currentPatientPage by mutableStateOf(1)
        private set

    // Constants for pagination
    private val patientsPerPage = 5 // Number of doctors to display per page

    val totalPatientPages: Int
        get() = (_allPatients.value!!.size + currentPatientPage - 1) / currentPatientPage

    // In your ViewModel when fetching all appointments
    fun fetchPatients(authToken: String, userId : String) {
        viewModelScope.launch {
            try {
                val patientsList = repository.getAllDoctorPatients(authToken, userId)
                _allPatients.value = patientsList.data  // Update the StateFlow with the new list
            } catch (e: Exception) {
                _loginError.value = "Error fetching patients: ${e.message}"
            }
        }
    }

    // Function to get appointments for the current page
    fun getPatientsForCurrentPage(): List<patientData> {
        val fromIndex = (currentPatientPage - 1) * patientsPerPage
        val toIndex = minOf(currentPatientPage * patientsPerPage, _allPatients.value!!.size)
        return _allPatients.value!!.subList(fromIndex, toIndex)

    }

    // Function to navigate to the next page
    fun nextPatientPage() {
        if (currentPatientPage < totalAppointmentPages) {
            currentPatientPage++
        }
    }

    // Function to navigate to the previous page
    fun previousPatientPage() {
        if (currentPatientPage > 1) {
            currentPatientPage--
        }
    }

    // Fetch appointments for the current page
    fun getAppointmentsForCurrentPage() {
        // Log the current page and the total number of appointments
        Log.d("ViewModel", "Current Appointment Page: $currentAppointmentPage")
        Log.d("ViewModel", "Total Appointments: ${_allAppointments.value?.size}")
        totalAppointmentPages = (appointmentList.size + appointmentsPerPage - 1) / appointmentsPerPage
        // Logic to filter _allAppointments based on currentAppointmentPage
        val currentPageAppointments = appointmentList
            ?.chunked(appointmentsPerPage)
            ?.get(currentAppointmentPage - 1)
            ?: emptyList()

        // Log the appointments for the current page
        Log.d("ViewModel", "Appointments for Page $currentAppointmentPage: $currentPageAppointments")

        // Post the filtered appointments to LiveData
        _appointmentsForPage.postValue(currentPageAppointments)
//        _unCorfimedappointmentsForPage.postValue(currentPageAppointments.filter { it.appointmentStatus.name == "Εκκρεμεί" })
    }

    // Fetch appointments for the current page
    fun getUncorfimedAppointmentsForCurrentPage() {
        // Log the current page and the total number of appointments
        Log.d("ViewModel", "Current Appointment Page: $currentUnAppointmentPage")
        Log.d("ViewModel", "Total Appointments: ${_allAppointments.value?.filter { it.appointmentStatus.name == "Εκκρεμεί" }?.size}")
        totalUnAppointmentPages = (appointmentList.filter { it.appointmentStatus.name == "Εκκρεμεί" }.size + appointmentsPerPage - 1) / appointmentsPerPage
        // Logic to filter _allAppointments based on currentAppointmentPage
        val currentPageAppointments = appointmentList.filter { it.appointmentStatus.name == "Εκκρεμεί" }
            ?.chunked(appointmentsPerPage)
            ?.get(currentUnAppointmentPage - 1)
            ?: emptyList()

        // Log the appointments for the current page
        Log.d("ViewModel", "Appointments for Page $currentUnAppointmentPage: $currentPageAppointments")

        // Post the filtered appointments to LiveData
        _unCorfimedappointmentsForPage.postValue(currentPageAppointments)
    }



    // Fetch all appointments

    // Handle pagination
    fun nextAppointmentPage() {
        if (currentAppointmentPage < totalAppointmentPages) {
            currentAppointmentPage++
            getAppointmentsForCurrentPage() // Update appointments for the new page
        }
    }

    fun previousAppointmentPage() {
        if (currentAppointmentPage > 1) {
            currentAppointmentPage--
            getAppointmentsForCurrentPage() // Update appointments for the new page
        }
    }

    // Fetch all uncofirmed appointments

    // Handle pagination
    fun nextUnAppointmentPage() {
        if (currentUnAppointmentPage < totalUnAppointmentPages) {
            currentUnAppointmentPage++
            getUncorfimedAppointmentsForCurrentPage() // Update appointments for the new page
        }
    }

    fun previousUnAppointmentPage() {
        if (currentUnAppointmentPage > 1) {
            currentUnAppointmentPage--
            getUncorfimedAppointmentsForCurrentPage() // Update appointments for the new page
        }
    }

    // MutableStateFlow for storing the entire list of appointments
//    var _allAppointments = MutableStateFlow<List<appointmentData>>(emptyList()) // Start with an empty list
//    var allAppointments: StateFlow<List<appointmentData>> = _allAppointments

    // State to keep track of the current page
    var currentAppointmentPage by mutableStateOf(1)
        private set

    // State to keep track of the current page
    var currentUnAppointmentPage by mutableStateOf(1)
        private set



    // Constants for pagination
    private val appointmentsPerPage = 5 // Number of doctors to display per page

    var totalAppointmentPages: Int =0

    var totalUnAppointmentPages: Int =0


    // MutableLiveData for appointments
    private val _allAppointments = MutableLiveData<List<appointmentData>>() // Ensure it is initialized
    val allAppointments: LiveData<List<appointmentData>> = _allAppointments


    // LiveData for error handling
//    private val _loginError = MutableLiveData<String>()
//    val loginError: LiveData<String> get() = _loginError

    var appointmentList : List<appointmentData> = emptyList()
    // Fetch appointments method
    fun fetchAppointments(type : Int, authToken: String, userId: String) {
        viewModelScope.launch {
            try {
                // Fetching data from the repository
                Log.d("ViewModel", "Fetching appointments for user: $userId")
                appointmentList = repository.getAllAppointments(authToken, userId).data

                // Log the received data
                Log.d("ViewModel", "Fetched appointments: ${appointmentList}")

                // Check if data is valid before updating LiveData
                if (appointmentList != null && appointmentList.isNotEmpty()) {
                    // Update the LiveData with a new list instance
                    _allAppointments.postValue(appointmentList.toList())
                    Log.d("ViewModel", "Updated _allAppointments with size: ${appointmentList.size}")
                } else {
                    Log.w("ViewModel", "No appointments fetched or data is null")
                }

                // Update the current page appointments
                if (type ==1)
                    getAppointmentsForCurrentPage() // Update appointments for the current page after fetching
                else
                    getUncorfimedAppointmentsForCurrentPage()
            } catch (e: Exception) {
                _loginError.postValue("Error fetching appointments: ${e.message}")
                Log.e("ViewModel", "Error fetching appointments: ${e.message}")
            }
        }
    }


    fun updateAppointment(operation : Int , authToken: String ,appointmentId: Int) {
        viewModelScope.launch {
            try {
                val appointmentResponse = repository.updateAppointmentStatus(operation , authToken ,appointmentId)
                if (appointmentResponse != null) {
                    _appointmentUpdateResponse.value = appointmentResponse.message
                } else {
                    _loginError.value = "Error updating appointment: No response from server"
                }
            } catch (e: Exception) {
                _loginError.value = "Error updating appointment: ${e.message}"
            }
        }
    }

    fun postAppointment(token : String , appointment: postAppointment)
    {
        viewModelScope.launch {try
        {
            val response = repository.postAppointment(token , appointment)
            if (response != null) {
                _appointmentResponse.value = response!!
                _appointmentMessage.value = "Appointment posted successfully!"

            } else {
                _loginError.value = "Failed to login. Please try again."
                _appointmentMessage.value = "Failed to post appointment."
            }
        } catch (e: Exception) {
            _loginError.value = "Error: ${e.message}"
            _appointmentMessage.value = "Error: ${e.message}"
        }
        }
    }


    // Function to get appointments for the current page
//    fun getAppointmentsForCurrentPage(): List<appointmentData> {
//        val appointments = _allAppointments.value
//        val fromIndex = (currentAppointmentPage - 1) * appointmentsPerPage
//        val toIndex = minOf(currentAppointmentPage * appointmentsPerPage, appointments!!.size)
//        return if (appointments!!.isNotEmpty()) {
//            appointments.subList(fromIndex, toIndex)
//        } else {
//            emptyList() // Return an empty list when there are no appointments
//        }
//    }

    // Function to navigate to the next page
//    fun nextAppointmentPage() {
//        if (currentAppointmentPage < totalAppointmentPages) {
//            currentAppointmentPage++
//        }
//    }

    // Function to navigate to the previous page
//    fun previousAppointentPage() {
//        if (currentAppointmentPage > 1) {
//            currentAppointmentPage--
//        }
//    }

}