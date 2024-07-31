package com.example.daytastic


import android.util.Log
import com.example.daytastic.weather.forecast.ForecastData
import com.example.daytastic.weather.Weather
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

val API_KEY = BuildConfig.API_KEY

class TemperatureEngine {
    fun getWeather(location: String): CompletableFuture<Weather> {
        val future = CompletableFuture<Weather>()
        val url = getURL(location)
        Log.d("OKHTTP3", "Sending a request to url: \"$url\" ")
        val okHttpClient = OkHttpClient()
        val requestBody = "".toRequestBody()
        val request = Request.Builder()
            .post(requestBody)
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OKHTTP3", "Exception: $e")
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.i("OKHTTP3", "Request successful code: $response.code")
                    val body = response.body!!.string()
                    val weather = Weather(Gson().fromJson(body, ForecastData::class.java))
                    future.complete(weather)
                    Log.i("OKHTTP3","completed future")
                } else {
                    Log.e("OKHTTP3", "Error: ${response.code}")
                    future.completeExceptionally(Exception("Error: $response.code"))
                }
            }

        })
        return future
    }

    fun getURL(location:String):HttpUrl{
        val urlBuilder = "https://api.weatherapi.com/v1/forecast.json".toHttpUrl()
        return urlBuilder.newBuilder()
            .addQueryParameter("key", API_KEY)
            .addQueryParameter("q", location)
            .addQueryParameter("days", "10")
            .addQueryParameter("aqi", "no")
            .addQueryParameter("alerts", "no").build()

    }
}