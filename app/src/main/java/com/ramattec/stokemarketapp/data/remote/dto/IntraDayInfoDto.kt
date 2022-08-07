package com.ramattec.stokemarketapp.data.remote.dto

import java.math.BigDecimal

data class IntraDayInfoDto(
    val timestamp: String,
    val close: BigDecimal,
    val open: BigDecimal,
    val low: BigDecimal,
    val high: BigDecimal,
    val volume: Long
)
