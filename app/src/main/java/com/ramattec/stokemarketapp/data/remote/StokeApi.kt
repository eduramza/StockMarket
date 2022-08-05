package com.ramattec.stokemarketapp.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StokeApi {

    @GET("query?function=$FUNCTION_LISTING")
    suspend fun fetchStockList(
        @Query("apikey") apikey: String
    ): ResponseBody

    companion object{
        const val API_KEY = "HHRX2P53JLSF8L2P"
        const val BASE_URL = "https://alphavantage.co"
        const val FUNCTION_LISTING = "LISTING_STATUS"
    }
}