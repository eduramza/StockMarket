package com.ramattec.stokemarketapp.domain.repository

import com.ramattec.stokemarketapp.domain.model.CompanyInfo
import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
import com.ramattec.stokemarketapp.util.Outcome
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun fetchCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Outcome<List<CompanyListing>>>

    suspend fun getIntraDayInfo(
        symbol: String
    ): Outcome<List<IntraDayInfo>>

    suspend fun getCompanyInfo(
        symbol: String
    ): Outcome<CompanyInfo>
}