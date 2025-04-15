package com.example.doctorsmanageappointments.Navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.doctorsmanageappointments.R

sealed class BottomNavItem(var title: String, var icon: Int, var screenRoute: String) {
    object MainScreen : BottomNavItem("LogUser", R.drawable.login_user, "loggin")
    object Appointments : BottomNavItem("Appointments", R.drawable.appointment, "appointments")
    object Patients : BottomNavItem("Patients", R.drawable.hospitalisation, "patients")
    object Chat : BottomNavItem("Chat", R.drawable.chat_icon, "chat")
}
