package com.ramattec.stokemarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.ramattec.stokemarketapp.data.remote.dto.CompanyInfoDto
import com.ramattec.stokemarketapp.data.remote.dto.IntraDayInfoDto
import com.ramattec.stokemarketapp.domain.model.CompanyInfo
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun IntraDayInfoDto.toIntraDayInfo(): IntraDayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(this.timestamp, formatter)
    return IntraDayInfo(
        date = localDateTime,
        close = close
    )
}

fun CompanyInfoDto.toCompanyInfo() = CompanyInfo(
    symbol ?: "",
    description ?: "",
    name ?: "",
    country ?: "",
    industry ?: ""
)