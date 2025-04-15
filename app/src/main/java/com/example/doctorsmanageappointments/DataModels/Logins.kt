package com.example.doctorappointments.DataModels

data class LoginRequest(
    val userName: String,
    val password: String
)

data class LoginResponse(
    val userName: String,
    val jwtToken: String,
    val expiresIn: Int,
    val timeStamp: String,
    val serviceOutcome: Int,
    val message: String?
)
data class appointmentResponse(
    val serviceOutcome: Int,
    val timeStamp : Any,
    val message: String?
)