package com.ramattec.stokemarketapp.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class IntraDayInfo(
    val date: LocalDateTime,
    val close: BigDecimal
)