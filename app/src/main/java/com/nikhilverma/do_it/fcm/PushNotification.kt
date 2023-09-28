package com.nikhilverma.do_it.fcm

data class PushNotification (
    val data:NotificationData,
    val to:String
)