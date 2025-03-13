package com.example.movieposter.data

data class MovieSession(
    val movieName: String,
    val time: String,
    val price: String,
    val posterUrl: String? = null
)
