package com.example.simpel.track

data class Track (
	val name : String,
	val mbid : String,
	val url : String,
	val duration : Int,
	val streamable : Streamable,
	val listeners : Int,
	val playcount : Int,
	val artist : Artist,
	val album : Album?,
	val toptags : Toptags,
	val wiki : Wiki
)
