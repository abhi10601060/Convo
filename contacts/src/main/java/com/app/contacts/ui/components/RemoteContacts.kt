package com.app.contacts.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.app.contacts.ui.screen.contacts.RemoteContactsUiState

@Composable
fun RemoteContacts(
    modifier: Modifier = Modifier,
    uiState: RemoteContactsUiState,
    listState: LazyListState = rememberLazyListState()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (uiState) {
            is RemoteContactsUiState.Success -> {
                val pagingItems = uiState.pagingData.collectAsLazyPagingItems()
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(pagingItems.itemCount) { index ->
                            pagingItems[index]?.let { contact ->
                                ContactItem(contact = contact)
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 80.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }

                        pagingItems.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                loadState.append is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                loadState.refresh is LoadState.Error -> {
                                    val e = pagingItems.loadState.refresh as LoadState.Error
                                    item {
                                        Box(
                                            modifier = Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = e.error.localizedMessage ?: "Unknown Error",
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(16.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                loadState.append is LoadState.Error -> {
                                    val e = pagingItems.loadState.append as LoadState.Error
                                    item {
                                        Text(
                                            text = e.error.localizedMessage ?: "Unknown Error",
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0) {
                        EmptyContactsView()
                    }

                }

            }
            is RemoteContactsUiState.Idle -> {
                // Optional to get Idle state
            }
        }
    }
}
