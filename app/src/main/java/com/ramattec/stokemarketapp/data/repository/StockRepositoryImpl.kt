package com.ramattec.stokemarketapp.data.repository

import com.ramattec.stokemarketapp.data.csv.CSVParser
import com.ramattec.stokemarketapp.data.local.StockDatabase
import com.ramattec.stokemarketapp.data.mapper.toCompanyListing
import com.ramattec.stokemarketapp.data.mapper.toCompanyListingEntity
import com.ramattec.stokemarketapp.data.remote.StockApi
import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.domain.repository.StockRepository
import com.ramattec.stokemarketapp.util.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>
) : StockRepository {

    private val dao = db.stockDao

    override suspend fun fetchCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Outcome<List<CompanyListing>>> = flow {
        emit(Outcome.Loading(true))
        val localListings = dao.fetchCompanyListing(query)
        emit(Outcome.Success(data = localListings.map { it.toCompanyListing() }))

        val isDbEmpty = localListings.isEmpty() && query.isBlank()
        if (!isDbEmpty && !fetchFromRemote) {
            emit(Outcome.Loading(false))
            return@flow
        }

        val remoteListing = try {
            val response = api.fetchStockList()
            companyListingParser.parse(response.byteStream())
        } catch (e: IOException) {
            emit(Outcome.Failure("Couldn't load data from remote"))
            null
        } catch (e: HttpException) {
            emit(Outcome.Failure("One Error occur!"))
            null
        }

        remoteListing?.let { list ->
            dao.clearCompanyListing()
            dao.insertCompanyListing(list.map { it.toCompanyListingEntity() })
            emit(Outcome.Success(data = dao.fetchCompanyListing("").map { it.toCompanyListing() }))
            emit(Outcome.Loading(isLoading = false))
        }
    }
}