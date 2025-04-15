package com.example.doctorsmanageappointments.DataModels

import com.google.gson.annotations.SerializedName

class AppointmentResponse {

    @SerializedName("timeStamp")
    var timeStamp: String = ""

    @SerializedName("serviceOutcome")
    var serviceOutcome: Int = 0

    @SerializedName("message")
    var message: String = ""
}