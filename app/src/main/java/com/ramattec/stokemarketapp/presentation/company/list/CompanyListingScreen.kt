package com.ramattec.stokemarketapp.presentation.company.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun CompanyListingScreen(
    navigator: DestinationsNavigator,
    companyListingViewModel: CompanyListingViewModel = hiltViewModel()
) {

    val swipeRefreshState =  rememberSwipeRefreshState(isRefreshing = companyListingViewModel.uiState.isRefreshing)
    val state = companyListingViewModel.uiState

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = {
                companyListingViewModel.onEvent(
                    CompanyListingEvent.OnSearchQueryCahnge(it)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = {
                Text(text = "Search...")
            },
            maxLines = 1,
            singleLine = true
        )
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                companyListingViewModel.onEvent(CompanyListingEvent.Refresh)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ){
                items(state.companies.size){ items ->

                    val company = state.companies[items]
                    CompanyItem(
                        company = company,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                //TODO: Navigate to detail screen
                            }
                            .padding(16.dp)
                    )

                    if (items < state.companies.size){
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}