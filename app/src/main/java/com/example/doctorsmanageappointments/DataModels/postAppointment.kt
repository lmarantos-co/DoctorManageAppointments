package com.example.doctorsmanageappointments.DataModels

import com.google.gson.annotations.SerializedName
import java.util.Date

class postAppointment {

    @SerializedName("userId")
    var userId : Int = 0

    @SerializedName("requestUserId")
    var requestUserId : Int = 0

    @SerializedName("responseUserId")
    var responseUserId : Int = 0

    @SerializedName("appointmentDate")
    var appointmentDate : String = ""

    @SerializedName("appointmentTime")
    var appointmentTime : String = ""

    @SerializedName("reasonOfVisit")
    var reasonOfVisit : String = ""

    @SerializedName("type")
    var type : String = ""
}