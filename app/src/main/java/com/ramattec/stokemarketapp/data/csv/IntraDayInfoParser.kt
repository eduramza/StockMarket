package com.ramattec.stokemarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.opencsv.CSVReader
import com.ramattec.stokemarketapp.data.mapper.toIntraDayInfo
import com.ramattec.stokemarketapp.data.remote.dto.IntraDayInfoDto
import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntraDayInfoParser @Inject constructor(): CSVParser<IntraDayInfo> {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntraDayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO){
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val open = line.getOrNull(1) ?: return@mapNotNull null
                    val high = line.getOrNull(2) ?: return@mapNotNull null
                    val low = line.getOrNull(3) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val volume = line.getOrNull(5) ?: return@mapNotNull null

                    val dto = IntraDayInfoDto(
                        timestamp = timestamp,
                        close = close.toBigDecimal(),
                        open = open.toBigDecimal(),
                        low = low.toBigDecimal(),
                        high = high.toBigDecimal(),
                        volume = volume.toLong()
                    )
                    dto.toIntraDayInfo()
                }
                .filter {
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }
                .sortedBy { it.date.hour }
                .also {
                    csvReader.close()
                }
        }
    }

}