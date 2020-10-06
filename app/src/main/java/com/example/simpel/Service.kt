package com.example.simpel

import com.example.simpel.track.IndividualTrack
import com.example.simpel.tracks.TrackSearch
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "http://ws.audioscrobbler.com/"
const val VERSION = "2.0/"
const val METHOD = "method"
const val API_KEY = "api_key"
const val FORMAT = "format"
const val KEY = "98ceff85f274d0607f90af8a2755cd5b" //Dangerous, usually never expose api keys without obfuscating/encrypting or proxying request with backend
const val JSON = "json"

const val TRACK_QUERY = "track"
const val TRACK_SEARCH_METHOD = "track.search"

const val ARTIST_QUERY = "artist"
const val TRACK_INFO_METHOD = "track.getInfo"


interface LastFmNetworkService {
    @GET(VERSION)
    suspend fun fetchTracksAsync(
        @Query(TRACK_QUERY) track: String,
        @Query(METHOD) method: String = TRACK_SEARCH_METHOD,
        @Query(API_KEY) apiKey: String = KEY,
        @Query(FORMAT) format: String = JSON
    ): TrackSearch

    @GET(VERSION)
    suspend fun fetchTrackAsync(
        @Query(TRACK_QUERY) track: String,
        @Query(ARTIST_QUERY) artist: String,
        @Query(METHOD) method: String = TRACK_INFO_METHOD,
        @Query(API_KEY) apiKey: String = KEY,
        @Query(FORMAT) format: String = JSON
    ): IndividualTrack
}