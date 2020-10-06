package com.example.simpel.tracks

import com.google.gson.annotations.SerializedName

data class Track (
	@SerializedName("name")
	val track : String,
	val artist : String,
	val url : String,
	val streamable : String,
	val listeners : Int,
	val image : List<Image>,
	val mbid : String
)
