package com.ramattec.stokemarketapp.data.repository

import com.ramattec.stokemarketapp.data.csv.CSVParser
import com.ramattec.stokemarketapp.data.csv.IntraDayInfoParser
import com.ramattec.stokemarketapp.data.local.StockDatabase
import com.ramattec.stokemarketapp.data.mapper.toCompanyInfo
import com.ramattec.stokemarketapp.data.mapper.toCompanyListing
import com.ramattec.stokemarketapp.data.mapper.toCompanyListingEntity
import com.ramattec.stokemarketapp.data.remote.StockApi
import com.ramattec.stokemarketapp.domain.model.CompanyInfo
import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
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
    private val companyListingParser: CSVParser<CompanyListing>,
    private val intraDayInfoParser: CSVParser<IntraDayInfo>
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

    override suspend fun getIntraDayInfo(symbol: String): Outcome<List<IntraDayInfo>> {
        return try {
            val response = api.getIntraDayInfo(symbol)
            val result = intraDayInfoParser.parse(response.byteStream())
            Outcome.Success(result)
        } catch (e: IOException){
            e.printStackTrace()
            Outcome.Failure("Couldn't load intraday info from remote")
        } catch (e: HttpException){
            e.printStackTrace()
            Outcome.Failure("One Error Occur when get intraDayInfo")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Outcome<CompanyInfo> {
        return try {
            val response = api.getCompanyOverview(symbol)
            Outcome.Success(response.toCompanyInfo())
        } catch (e: IOException){
            e.printStackTrace()
            Outcome.Failure("Couldn't load companyInfo info from remote")
        } catch (e: HttpException){
            e.printStackTrace()
            Outcome.Failure("One Error Occur when get companyInfo")
        }
    }
}