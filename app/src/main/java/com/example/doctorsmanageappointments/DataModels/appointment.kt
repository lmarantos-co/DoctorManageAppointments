package com.example.doctorappointments.DataModels

import com.google.gson.annotations.SerializedName
import java.util.Date

class appointment {

    @SerializedName("timeStamp")
    var timeStamp: String = ""

    @SerializedName("serviceOutcome")
    var serviceOutcome: Int = 0

    @SerializedName("message")
    var message: String = ""

    @SerializedName("data")
    var data : List<appointmentData> = arrayListOf<appointmentData>()

}

data class appointmentData(val socialSecurityNumber : Float,
                           val fullName : String,
                           val cost : Int,
                           val email : String,
                           val mobile : String,
                           val avatarUrl : String,
                           val appointmentId : Int,
                           val userId : Int,
                           val appointmentDate : String,
                           val appointmentTime : String,
                           val reasonOfVisit : String,
                           val appointmentStatus : appointmentStatus,
                           )

data class appointmentStatus( val appointmentStatusId : Int,
                              val name : String)