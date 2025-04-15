package com.example.doctorappointments.API

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
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("baseauthentication/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("doctors")
    suspend fun getDoctors(
        @Header("Authorization") authHeader: String,
    ): Response<Doctor>

    @GET("member/me/{username}")
    suspend fun getUserDetails(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String // This will replace the placeholder in the URL
    ) : member

    @GET("doctors/me/{username}")
    suspend fun getDoctorDetails(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String // This will replace the placeholder in the URL
    ) : Response<Doctor>


    @GET("appointments/{userId}/userappointments")
    suspend fun getDoctorAppointments(
        @Header("Authorization") authHeader : String,
        @Path("userId") userId : String // This will replace the placeholder in the URL
    ) : Response<appointment>

    @GET("doctors/{userId}/patients")
    suspend fun getDoctorPatients(
        @Header("Authorization") authHeader : String,
        @Path("userId") userId : String // This will replace the placeholder in the URL
    ) : Response<patient>

    @POST("appointments")
    suspend fun postAppointment(
        @Header("Authorization") authHeader: String,
        @Body appointment : postAppointment
    ): Response<appointmentResponse> // Return Retrofit's Response object

    //delete a specific appointment
    @PUT("appointments/{appointmentId}/status")
    fun updateAppointmentStatus(
        @Header("Authorization") authHeader : String,
        @Path("appointmentId") appointmentId: Int, // Dynamic appointment ID in the path
        @Body appointmentStatus: appointmentStatus    // Request body with status update
    ): Call<AppointmentResponse>  // Response could be Void or any other type depending on API response

}
