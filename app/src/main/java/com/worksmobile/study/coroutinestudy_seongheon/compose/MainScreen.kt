package com.worksmobile.study.coroutinestudy_seongheon.compose

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.gson.Gson
import com.skydoves.landscapist.glide.GlideImage
import com.worksmobile.study.coroutinestudy_seongheon.MainActivity.Companion.DETAIL_SCREEN_KEY
import com.worksmobile.study.coroutinestudy_seongheon.MainViewModel
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

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
                GlideImage(
                    imageModel = searchResult[index]?.link,
                    modifier = Modifier
                        .height(150.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    searchResult[index]?.let {
                                        val newItem = Item(
                                            it.title,
                                            URLEncoder.encode(it.link, StandardCharsets.UTF_8.toString()),
                                            URLEncoder.encode(it.thumbnail, StandardCharsets.UTF_8.toString()),
                                            it.sizeWidth,
                                            it.sizeHeight
                                        )

                                        navController.navigate("${DETAIL_SCREEN_KEY}/${Gson().toJson(newItem)}")
                                    }
                                }
                            )
                        }
                )
            }
        }
    )
}
