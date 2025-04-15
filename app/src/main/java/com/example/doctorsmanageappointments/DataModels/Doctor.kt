package com.example.doctorappointments.DataModels

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Gson-related imports
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import java.lang.reflect.Type
import com.google.gson.annotations.SerializedName

class Doctor()
{
    @SerializedName("timeStamp")
    var timeStamp: String = ""

    @SerializedName("serviceOutcome")
    var serviceOutcome: Int = 0

    @SerializedName("message")
    var message: String = ""

    @SerializedName("data")
    lateinit var data : doctorData

}

data class doctorData(
    val userId : Int,
    val fullName : String,
    val specialty: specialty = specialty(0, "", arrayListOf<services>()),  // Initialize with empty list of services
    val rating : Float,
    val totalReviews : Int,
)

data class specialty(val specialtyId : Int ,
                     val name : String ,
                     val services : ArrayList<services>,
                     )

data class services(val serviceId : Int , val name : String)

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        return LocalDateTime.parse(json?.asString, formatter)
    }
}

data class DoctorsResponse(
    val doctors: Doctor
)


