package ofy.livegolyonetici.services

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface fcminterface {
    @POST("send")
    fun bildirimgonder(@HeaderMap headers:Map<String,String>,
                       @Body bildirim:fcmmodel): Call<Response<fcmmodel>>
}