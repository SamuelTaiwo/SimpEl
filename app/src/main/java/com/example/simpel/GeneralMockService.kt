package com.example.simpel

import com.example.annotations.FieldAnno
import com.example.annotations.GeneralServiceGenerator
import com.example.simpel.track.Album

@GeneralServiceGenerator
class GeneralMockService(e: Album) {
    private val easy = "123"
    private val hard = "456"

    /** A generated property declaration.  */
    @FieldAnno
    val path = "${easy}/bbb/${hard}"

    /** A generated property declaration.  */
    var path2
        get() = "${easy}/bbb/${hard}"
        set(value) { "${easy}/bbb/${hard}" }

    val path3 by lazy { "${easy}/bbb/${hard}" }

    var counter = 1

    fun poo(){
        val cheese = "1"
        fun poo2(){
            val cheese22 = "1"
            counter = 2
            counter = 3
        }
    }
}