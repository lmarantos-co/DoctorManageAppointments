package com.example.doctorappointments.DataModels

import com.google.gson.annotations.SerializedName

class member {

    @SerializedName("timeStamp")
    var timeStamp : String = ""

    @SerializedName("serviceOutcome")
    var serviceOutcome : Int = 0

    @SerializedName("message")
    var message : String = ""

    @SerializedName("data")
    var data : List<data> = arrayListOf<data>()
}

data class data(
    val userId: Int,
    val fullName: String,
    val dateOfBirth: String,
    val identityCardMember: String,
    val socialSecurityNumber : String,
    val mobile : String,
    val telephone : String,
    val email : String

)