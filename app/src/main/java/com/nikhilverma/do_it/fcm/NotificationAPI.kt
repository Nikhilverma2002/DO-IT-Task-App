package com.nikhilverma.do_it.fcm

import com.google.android.gms.common.api.Response
import com.nikhilverma.do_it.fcm.Constants.Companion.CONTENT_TYPE
import com.nikhilverma.do_it.fcm.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/*
interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY","Content_Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification:PushNotification
    ): Response<ResponseBody>
}*/
