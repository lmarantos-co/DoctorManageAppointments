package com.example.doctorappointments.Repository


import android.util.Log
import com.example.doctorappointments.API.RetrofitClient
import com.example.doctorappointments.DataModels.Doctor
import com.example.doctorappointments.DataModels.DoctorsResponse
import com.example.doctorappointments.DataModels.LoginRequest
import com.example.doctorappointments.DataModels.LoginResponse
import com.example.doctorappointments.DataModels.appointment
import com.example.doctorappointments.DataModels.appointmentResponse
import com.example.doctorappointments.DataModels.appointmentStatus
import com.example.doctorappointments.DataModels.member
import com.example.doctorappointments.DataModels.patient
import com.example.doctorsmanageappointments.DataModels.AppointmentResponse
import com.example.doctorsmanageappointments.DataModels.postAppointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository {

    // Function to handle login using Retrofit with Coroutines
    suspend fun loginUser(userName: String, password: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(userName, password)
                // Retrofit makes the network call in the background thread
                val response = RetrofitClient.apiService.login(loginRequest)
                response // Return the response if successful
            } catch (e: Exception) {
                // Handle the exception and return null or throw
                null
            }
        }
    }

    suspend fun getMember(token : String, userName: String) : member
    {
        return withContext(Dispatchers.IO)   {
            try {
                val authHeader = "Bearer $token"
                val response = RetrofitClient.apiService.getUserDetails(authHeader , userName)
                response
            }  catch (e: Exception) {
                // Handle the exception and return null or throw
                println("Exception : ${e.message.toString()}")
                null
            }!!
        }
    }

    suspend fun getDoctorDetails(token : String, userName: String) : Doctor?
    {
        return withContext(Dispatchers.IO) {
            try {
                // Retrofit makes the network call in the background thread
                val authHeader = "Bearer $token"
                val response = RetrofitClient.apiService.getDoctorDetails(authHeader, userName)
                if (response.isSuccessful) {
                    response.body() // Return the successful body
                } else {
                    Log.e("API", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
                    null // Return null if the response is not successful
                }
            } catch (e: Exception) {
                null // Handle the exception and return null
            }
        }
    }

    suspend fun getAllDoctors(token : String ,userName : String, password : String) : Doctor
    {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val loginRequest = LoginRequest(userName, password)
                val response = RetrofitClient.apiService.getDoctors(authHeader)
                if (response.isSuccessful) {
                    response.body() // Return the successful body
                } else {
                    Log.e("API", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
                    null // Return null if the response is not successful
                }
            } catch (e: Exception) {
                null // Handle the exception and return null
            }!!
        }
    }

    suspend fun getAllAppointments(token : String ,userId : String) : appointment
    {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val response = RetrofitClient.apiService.getDoctorAppointments(authHeader , userId)
                if (response.isSuccessful) {
                    response.body() // Return the successful body
                } else {
                    Log.e("API", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
                    null // Return null if the response is not successful
                }
            } catch (e: Exception) {
                null // Handle the exception and return null
            }!!
        }
    }

    suspend fun getAllDoctorPatients(token : String ,userId : String) : patient
    {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val response = RetrofitClient.apiService.getDoctorPatients(authHeader , userId)
                if (response.isSuccessful) {
                    response.body() // Return the successful body
                } else {
                    Log.e("API", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
                    null // Return null if the response is not successful
                }
            } catch (e: Exception) {
                null // Handle the exception and return null
            }!!
        }
    }

    suspend fun updateAppointmentStatus(operation : Int ,token : String , appointmentId: Int): AppointmentResponse? {
        var appointmentStatus = appointmentStatus(appointmentStatusId = 2, name = "")
        if (operation == 1)
        {
            appointmentStatus = appointmentStatus(appointmentStatusId = 2, name = "")
        }
        if (operation == 2)
        {
            appointmentStatus = appointmentStatus(appointmentStatusId = 1, name = "")
        }
        val authHeader = "Bearer $token"

        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.updateAppointmentStatus(authHeader , appointmentId, appointmentStatus).execute()
                if (response.isSuccessful) {
                    response.body()  // Return the AppointmentResponse object
                } else {
                    // Handle error case
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                null
            }
        }
    }

    //function to handle the posting of appointment
    suspend fun postAppointment(token : String, appointment: postAppointment): appointmentResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // Retrofit makes the network call in the background thread
                val response = RetrofitClient.apiService.postAppointment(token, appointment)
                if (response.isSuccessful) {
                    response.body() // Return the successful body
                } else {
                    Log.e("API", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
                    null // Return null if the response is not successful
                }
            } catch (e: Exception) {
                null // Handle the exception and return null
            }
        }
    }


    suspend fun getDoctors(token : String ,userName : String, password : String) : Doctor
    {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val loginRequest = LoginRequest(userName, password)
                val response = RetrofitClient.apiService.getDoctors(authHeader)
                if (response.isSuccessful) {
                    response.body() // Return the successful body
                } else {
                    Log.e("API", "Response failed: ${response.code()} - ${response.errorBody()?.string()}")
                    null // Return null if the response is not successful
                }
            } catch (e: Exception) {
                null // Handle the exception and return null
            }!!
        }
    }
}
