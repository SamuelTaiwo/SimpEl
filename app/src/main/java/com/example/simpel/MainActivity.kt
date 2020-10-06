package com.example.simpel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.simpel.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Default, CoroutineStart.DEFAULT, null, {getTracksGenerated()})
    }

    private suspend fun getTracksGenerated(){
        val tracks = GeneratedMockNetwork().trackService.fetchTracksAsync("Reviver")
        Log.i("tracks", tracks.results.trackMatches.track[0].artist)
    }

    private suspend fun getTracks(){
        val tracks = Network().trackService.fetchTracksAsync("hello")
        Log.i("tracks", tracks.results.trackMatches.track[0].artist)
        EncapsulatedModel()
    }
}

