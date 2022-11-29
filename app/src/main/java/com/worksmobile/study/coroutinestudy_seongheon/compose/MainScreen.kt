package com.worksmobile.study.coroutinestudy_seongheon.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.gson.Gson
import com.skydoves.landscapist.glide.GlideImage
import com.worksmobile.study.coroutinestudy_seongheon.MainActivity.Companion.DETAIL_SCREEN_KEY
import com.worksmobile.study.coroutinestudy_seongheon.MainViewModel
import com.worksmobile.study.coroutinestudy_seongheon.data.Item

@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavController) {
    Column {
        SearchBox(viewModel)
        SearchResultBox(3, viewModel, navController)
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
fun SearchResultBox(columnCount: Int, viewModel: MainViewModel, navController: NavController) {
    val searchResult = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        state = listState,
        modifier = Modifier.fillMaxWidth(),
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
                        val tint = if (checkedState.value) Color.Red else Color.Black

                        Image(
                            imageVector = if (checkedState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Icon",
                            colorFilter = ColorFilter.tint(tint),
                            alignment = Alignment.TopEnd
                        )
                    }
                    SearchResultItem(item = item, navController)
                }
            }
        }
    )
}

@Composable
fun SearchResultItem(item: Item, navController: NavController) {
    GlideImage(
        imageModel = item.link,
        modifier = Modifier
            .height(150.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        navController.navigate("${DETAIL_SCREEN_KEY}/${Gson().toJson(item.getEncodedItem())}")
                    }
                )
            }
    )
}


