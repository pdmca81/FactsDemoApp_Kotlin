package com.pradeep.factsdemoapp.api

import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("/s/2iodh4vg0eortkl/facts")
    fun getFacts(
    ): Call<String?>?
}
