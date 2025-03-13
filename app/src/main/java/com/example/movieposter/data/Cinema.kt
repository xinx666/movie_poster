package com.example.movieposter.data

data class Cinema(
    val name: String,
    val metroStation: String,
    val scheduleUrl: String,
    val fullAddress: String? = null,
    val description: String? = null,
    val schedule: List<MovieSession>
)
