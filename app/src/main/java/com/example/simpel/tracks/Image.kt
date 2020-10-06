package com.example.simpel.tracks

import com.google.gson.annotations.SerializedName

data class Image (
	@SerializedName("#text")
	val text : String,
	val size : String
)
