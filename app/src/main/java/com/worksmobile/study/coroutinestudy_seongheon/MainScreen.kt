package com.worksmobile.study.coroutinestudy_seongheon

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MainScreen(viewModel: MainViewModel) {
    Column {
        SearchBox(viewModel)
        SearchResultBox(3, viewModel)
    }
}

@Composable
fun SearchBox(viewModel: MainViewModel) {
    var text by remember { viewModel.searchQuery }

    Row {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Search") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .weight(weight = 1.0F, fill = true)
                .testTag("TextField"),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Button(onClick = { viewModel.handleQuery() }) {
            Text(text = "Search")
        }
    }
}

@Composable
fun SearchResultBox(columnCount: Int, viewModel: MainViewModel) {
    val searchResult = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val listState = rememberLazyGridState()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        content = {
            items(searchResult.itemCount) { index ->
                GlideImage(
                    imageModel = searchResult[index]?.link,
                    modifier = Modifier
                        .height(150.dp)
                )
            }
        }
    )

    EndlessListHandler(listState = listState, buffer = 2) {
        Log.d(":::", "eod")
    }
}

@Composable
fun EndlessListHandler(listState: LazyGridState, buffer: Int, callback: () -> Unit) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemNum = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemNum - buffer)
        }
    }

    LaunchedEffect(key1 = loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                callback()
            }
    }
}
