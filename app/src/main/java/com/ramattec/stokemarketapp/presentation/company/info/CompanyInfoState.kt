package com.ramattec.stokemarketapp.presentation.company.info

import com.ramattec.stokemarketapp.domain.model.CompanyInfo
import com.ramattec.stokemarketapp.domain.model.IntraDayInfo

data class CompanyInfoState(
    val stockInfos: List<IntraDayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
