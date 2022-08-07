package com.ramattec.stokemarketapp.presentation.company.list

sealed class CompanyListingEvent{
    object Refresh: CompanyListingEvent()
    data class OnSearchQueryCahnge(val query: String): CompanyListingEvent()
}
