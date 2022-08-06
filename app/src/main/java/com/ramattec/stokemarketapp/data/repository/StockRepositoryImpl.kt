package com.ramattec.stokemarketapp.data.repository

import com.opencsv.CSVReader
import com.ramattec.stokemarketapp.data.local.StockDao
import com.ramattec.stokemarketapp.data.local.StockDatabase
import com.ramattec.stokemarketapp.data.mapper.toCompanyListing
import com.ramattec.stokemarketapp.data.remote.StockApi
import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.domain.repository.StockRepository
import com.ramattec.stokemarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    db: StockDatabase
): StockRepository{

    private val dao = db.stockDao

    override suspend fun fetchCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> = flow {
        emit(Resource.Loading(true))
        val localListings = dao.fetchCompanyListing(query)
        emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))

        val isDbEmpty = localListings.isEmpty() && query.isBlank()
        if (!isDbEmpty && !fetchFromRemote){
            emit(Resource.Loading(false))
            return@flow
        }

        val remoteListing = try {
            val response = api.fetchStockList()
            val csvReader = CSVReader(InputStreamReader(response.byteStream()))
        } catch (e: IOException){
            emit(Resource.Failure("Couldn't load data from remote"))
        } catch (e: HttpException){
            emit(Resource.Failure("One Error occur!"))
        }
    }
}