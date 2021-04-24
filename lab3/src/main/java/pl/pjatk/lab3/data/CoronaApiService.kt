package pl.pjatk.lab3.data

import pl.pjatk.lab3.model.Country
import pl.pjatk.lab3.model.ResultData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoronaApiService {

    @GET("/countries")
    fun getCountries(): Call<List<Country>>

    @GET("/country/{country}")
    fun getChartData(
        @Path("country") country: String,
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<List<ResultData>>
}
