package com.worksmobile.study.coroutinestudy_seongheon.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.gson.Gson
import com.skydoves.landscapist.glide.GlideImage
import com.worksmobile.study.coroutinestudy_seongheon.MainActivity.Companion.DETAIL_SCREEN_KEY
import com.worksmobile.study.coroutinestudy_seongheon.MainViewModel
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchResultType
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadState

@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isDownloadStateTextVisible = remember { mutableStateOf(false) }
    val downloadStateText = remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        LaunchedEffect(true) {
            //https://www.valueof.io/blog/stateflow-vs-sharedflow-jetpack-compose
            viewModel.downloadEventFlow.collect { state ->
                when (state) {
                    is DownloadState.Start -> {
                        isDownloadStateTextVisible.value = true
                        downloadStateText.value = "Download Started!!"
                    }
                    is DownloadState.Progress -> {
                        downloadStateText.value = "${state.progress}% in Progress!!"
                    }
                    is DownloadState.Complete -> {
                        isDownloadStateTextVisible.value = false
                        downloadStateText.value = "Download Completed!! \n uri : ${state.uri} "
                    }
                    is DownloadState.Fail -> {
                        isDownloadStateTextVisible.value = false
                        downloadStateText.value = "Download Fails"
                    }
                }
            }
        }

        Box {
            Column {
                SearchBox(viewModel)
                SearchResultBox(3, viewModel, navController)
            }
            Text(
                text = downloadStateText.value,
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.DarkGray)
                    .padding(10.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = TextUnit.Unspecified
            )
        }
    }
}

@Composable
fun SearchBox(viewModel: MainViewModel) {
    val searchQueryState = viewModel.searchQueryFlow.collectAsState()
    val text by remember { searchQueryState }

    Row {
        TextField(
            value = text,
            onValueChange = { viewModel.changeQuery(it) },
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
fun SearchResultBox(columnCount: Int, viewModel: MainViewModel, navController: NavController) {
    val searchResult = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val searchResultTypeState = viewModel.searchResultTypeFlow.collectAsState()
    val searchResultType by remember { searchResultTypeState }
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        content = {
            items(searchResult.itemCount) { index ->
                val item = searchResult[index] ?: return@items
                val checkedState = remember { mutableStateOf(item.isBookmark) }
                Box {
                    IconToggleButton(
                        checked = checkedState.value,
                        onCheckedChange = {
                            checkedState.value = it
                            viewModel.handleBookmark(item, it)
                        },
                        modifier = Modifier.zIndex(1f)
                    ) {
                        if (searchResultType == SearchResultType.LOCAL) return@IconToggleButton
                        val tint = if (checkedState.value) Color.Red else Color.Black

                        Image(
                            imageVector = if (checkedState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Icon",
                            colorFilter = ColorFilter.tint(tint),
                            alignment = Alignment.TopEnd
                        )
                    }
                    SearchResultItem(item = item, viewModel, navController)
                }
            }
        }
    )
}

@Composable
fun SearchResultItem(item: Item, viewModel: MainViewModel, navController: NavController) {
    GlideImage(
        imageModel = item.link,
        modifier = Modifier
            .height(150.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        navController.navigate("${DETAIL_SCREEN_KEY}/${Gson().toJson(item.getEncodedItem())}")
                    },
                    onDoubleTap = {
                        viewModel.downloadImage(item)
                    }
                )
            }
    )
}


