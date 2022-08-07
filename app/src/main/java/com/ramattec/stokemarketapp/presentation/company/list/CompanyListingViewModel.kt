package com.ramattec.stokemarketapp.presentation.company.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramattec.stokemarketapp.domain.repository.StockRepository
import com.ramattec.stokemarketapp.util.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    var state by mutableStateOf(CompanyListingState())

    private var searchJob: Job? = null

    fun onEvent(event: CompanyListingEvent) {
        when (event) {
            is CompanyListingEvent.Refresh -> {
                getCompanyListing(fetchFromRemote = true)
            }
            is CompanyListingEvent.OnSearchQueryCahnge -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500)
                    getCompanyListing()
                }
            }
        }
    }

    fun getCompanyListing(
        query: String = state.searchQuery.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            repository.fetchCompanyListing(fetchFromRemote, query)
                .collect { result ->
                    when (result) {
                        is Outcome.Success -> {
                            result.data?.let {
                                state = state.copy(companies = it)
                            }
                        }
                        is Outcome.Failure -> Unit
                        is Outcome.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}