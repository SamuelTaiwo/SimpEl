package com.example.simpel.track

import com.example.simpel.tracks.Image
import com.google.gson.annotations.SerializedName

data class Album (
	val artist : String,
	val title : String,
	val mbid : String,
	val url : String,
	val image : List<Image>,
	@SerializedName("@attr")
	val attr : Attr
)
