package com.ramattec.stokemarketapp.di

import com.ramattec.stokemarketapp.data.csv.CSVParser
import com.ramattec.stokemarketapp.data.csv.CompanyListingParser
import com.ramattec.stokemarketapp.data.csv.IntraDayInfoParser
import com.ramattec.stokemarketapp.data.repository.StockRepositoryImpl
import com.ramattec.stokemarketapp.domain.model.CompanyListing
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
import com.ramattec.stokemarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingParser: CompanyListingParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindIntraDayParser(
        intraDayInfoParser: IntraDayInfoParser
    ): CSVParser<IntraDayInfo>
}