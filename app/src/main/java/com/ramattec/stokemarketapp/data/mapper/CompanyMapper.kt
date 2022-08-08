package com.ramattec.stokemarketapp.data.mapper

import com.ramattec.stokemarketapp.data.local.CompanyListingEntity
import com.ramattec.stokemarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing() =
    CompanyListing(name, symbol, exchange)

fun CompanyListing.toCompanyListingEntity() =
    CompanyListingEntity(name, symbol, exchange)