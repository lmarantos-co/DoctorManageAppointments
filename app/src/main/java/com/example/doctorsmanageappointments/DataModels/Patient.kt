package com.example.doctorappointments.DataModels

import com.google.gson.annotations.SerializedName

class patient {

    @SerializedName("timeStamp")
    var timeStamp : String = ""

    @SerializedName("serviceOutcome")
    var serviceOutcome : Int = 0

    @SerializedName("message")
    var message : String = ""

    @SerializedName("data")
    var data : List<patientData> = arrayListOf<patientData>()
}

data class patientData(
    val userId: Int,
    val fullName: String,
    val dateOfBirth: String,
    val socialSecurityNumber : String,
    val gender : String,
    val mobile : String,
    val email : String

)