package com.ramattec.stokemarketapp.presentation.company.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramattec.stokemarketapp.domain.model.CompanyInfo
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo
import com.ramattec.stokemarketapp.domain.repository.StockRepository
import com.ramattec.stokemarketapp.util.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
) : ViewModel() {

    var uiState by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            uiState = uiState.copy(isLoading = true)
            val companyInfo = async { repository.getCompanyInfo(symbol) }
            val intraDay = async { repository.getIntraDayInfo(symbol) }
            setupCompanyInfo(companyInfo)
            setupIntraDayInfo(intraDay)
        }
    }

    private suspend fun setupIntraDayInfo(intraDay: Deferred<Outcome<List<IntraDayInfo>>>) {
        when (val result = intraDay.await()) {
            is Outcome.Success -> {
                uiState = uiState.copy(
                    stockInfos = result.data ?: emptyList(),
                    isLoading = false,
                    error = null
                )
            }
            is Outcome.Failure -> {
                uiState = uiState.copy(
                    stockInfos = emptyList(),
                    isLoading = false,
                    error = result.message
                )
            }
            else -> Unit
        }
    }

    private suspend fun setupCompanyInfo(companyInfo: Deferred<Outcome<CompanyInfo>>) {
        when (val result = companyInfo.await()) {
            is Outcome.Success -> {
                uiState = uiState.copy(
                    company = result.data,
                    isLoading = false,
                    error = null
                )
            }
            is Outcome.Failure -> {
                uiState = uiState.copy(
                    company = null,
                    isLoading = false,
                    error = result.message
                )
            }
            else -> Unit
        }
    }
}