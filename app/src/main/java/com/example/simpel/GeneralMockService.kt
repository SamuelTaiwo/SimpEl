package com.example.simpel

import com.example.annotations.GeneralServiceGenerator
import com.example.simpel.track.Album

@GeneralServiceGenerator
class GeneralMockService(e: Album) {
    private val easy = "123"
    private val hard = "456"

    val path = "${easy}/bbb/${hard}"
}