package com.ramattec.stokemarketapp.data.remote.dto

data class IntraDayInfoDto(
    val timestamp: String,
    val close: Double,
    val open: Double,
    val low: Double,
    val high: Double,
    val volume: Long
)
