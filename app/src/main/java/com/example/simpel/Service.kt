package com.example.simpel

import com.example.simpel.track.IndividualTrack
import com.example.simpel.tracks.TrackSearch
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmNetworkService {
    @GET("2.0/")
    suspend fun fetchTracksAsync(
        @Query("track") track: String,
        @Query("method") method: String = "track.search",
        @Query("api_key") apiKey: String = "98ceff85f274d0607f90af8a2755cd5b" ,
        @Query("format") format: String = "json"
    ): TrackSearch

    @GET("2.0/")
    suspend fun fetchTrackAsync(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("method") method: String = "track.getInfo",
        @Query("api_key") apiKey: String = "98ceff85f274d0607f90af8a2755cd5b" ,
        @Query("format") format: String = "json"
    ): IndividualTrack
}
