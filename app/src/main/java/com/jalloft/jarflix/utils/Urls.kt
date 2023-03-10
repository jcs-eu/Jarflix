package com.jalloft.jarflix.utils


const val API_URL_BASE = "https://api.themoviedb.org/3/"
const val IMAGE_URL_W500 = "https://image.tmdb.org/t/p/w500"
const val IMAGE_URL_ORIGINAL = "https://image.tmdb.org/t/p/original"

val String.toImageOrigial get() = IMAGE_URL_ORIGINAL.plus(this)

val String.toImageW500 get() = IMAGE_URL_W500.plus(this)




