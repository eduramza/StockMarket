package com.ramattec.stokemarketapp.domain.repository

import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun fetchCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}