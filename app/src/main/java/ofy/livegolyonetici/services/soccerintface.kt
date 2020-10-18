package ofy.livegolyonetici.services

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface soccerintface {
    @GET("leagues/")
    fun cek(): Call<List<soccerlivemodel>>
}