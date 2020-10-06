package com.example.simpel.tracks

import com.google.gson.annotations.SerializedName

data class Results (
	@SerializedName("opensearch:Query")
	val opensearch_Query : OpenSearchQuery,
	@SerializedName("opensearch:totalResults")
	val opensearch_totalResults : Int,
	@SerializedName("opensearch:startIndex")
	val opensearch_startIndex : Int,
	@SerializedName("opensearch:itemsPerPage")
	val opensearch_itemsPerPage : Int,
	@SerializedName("trackmatches")
	val trackMatches : TrackMatches,
	@SerializedName("attr")
	val attr : Attr
)
