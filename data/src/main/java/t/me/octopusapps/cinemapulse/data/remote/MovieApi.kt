package t.me.octopusapps.cinemapulse.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import t.me.octopusapps.cinemapulse.data.models.MovieDetails
import t.me.octopusapps.cinemapulse.data.models.MovieResponse

internal interface MovieApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
    ): MovieDetails

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String
    ): MovieResponse
}