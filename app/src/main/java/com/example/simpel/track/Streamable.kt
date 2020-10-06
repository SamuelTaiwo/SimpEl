package com.example.simpel.track

import com.google.gson.annotations.SerializedName

data class Streamable (
	@SerializedName("#text")
	val text : Int,
	val fulltrack : Int
)
