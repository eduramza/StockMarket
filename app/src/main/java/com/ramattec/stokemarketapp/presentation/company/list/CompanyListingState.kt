package com.ramattec.stokemarketapp.presentation.company.list

import com.ramattec.stokemarketapp.domain.model.CompanyListing

data class CompanyListingState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)
